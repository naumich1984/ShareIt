package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private Long expectedUserId;
    private UserDto expectedDtoUser;
    private User expectedUser;

    @InjectMocks
    UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @BeforeEach
    void setUp() {
        expectedUserId = 1L;
        expectedDtoUser = new UserDto(expectedUserId, "name1", "name1@email.ru");
        expectedUser = UserMapper.toUser(expectedDtoUser);
    }

    @Test
    void saveUser_whenInvoked_thenReturnedUser() {
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        User actualUser = userService.saveUser(expectedDtoUser);

        assertEquals(expectedUser, actualUser);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void saveUser_whenNameNotUnique_thenExceptionThrown() {
        when(userRepository.save(expectedUser)).thenThrow(new IllegalArgumentException("User name not unique"));

        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(expectedDtoUser));
        verify(userRepository).save(expectedUser);
    }

    @Test
    void updateUser_whenUserFound_thenReturnedUser() {
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        when(userRepository.findById(expectedUserId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.updateUser(expectedDtoUser, expectedUserId);

        assertEquals(expectedUser, actualUser);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(expectedUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(expectedDtoUser, expectedUserId));
        verify(userRepository, never()).save(expectedUser);
    }

    @Test
    void updateUser_whenUserFound_thenChangeOnlyAvailableFields() {
        Long userId = 1L;
        User oldUser = new User();
        oldUser.setName("oldName1");
        oldUser.setEmail("oldEmail1");
        User newUser = new User();
        newUser.setName("newName1");
        newUser.setEmail("newEmail1");
        UserDto newUserDto = UserMapper.toUserDto(newUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        User actualUser = userService.updateUser(newUserDto, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(newUser.getName(), savedUser.getName());
        assertEquals(newUser.getEmail(), savedUser.getEmail());
    }
    
    @Test
    void deleteUser_whenInvoked_thenReturnedIntegerValue() {
        Integer expectedResult = 0;

        Integer actualResult = userService.deleteUser(expectedUserId);

        assertEquals(expectedResult, actualResult);
        verify(userRepository).deleteById(expectedUserId);
    }

    @Test
    void getUser_whenUserFound_thenReturnedUser() {
        when(userRepository.findById(expectedUserId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUser(expectedUserId);

        assertEquals(expectedUser, actualUser);
        verify(userRepository).findById(expectedUserId);
    }

    @Test
    void getUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(expectedUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUser(expectedUserId));
        verify(userRepository).findById(expectedUserId);
    }

    @Test
    void getAllUsers_whenInvoked_thenReturnUsers() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findAll()).thenReturn(expectedUsers).thenReturn(expectedUsers);

        List<UserDto> actualDtoUsers = userService.getAllUsers();

        assertEquals(expectedUsers
                .stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList()), actualDtoUsers);
        verify(userRepository).findAll();
    }
}
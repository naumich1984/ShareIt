package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    private UserDto expectedDtoUser;
    private User expectedUser;
    private Long expectedUserId;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        expectedUserId = 1L;
        expectedDtoUser  = new UserDto(expectedUserId,"name1","name1@test.ru");
        expectedUser  = new User(expectedUserId,"name1","name1@test.ru");
    }

    @Test
    void saveNewUser_whenInvoked_thenResponseStatusOkWithUserInBody() {
        Mockito.when(userService.saveUser(expectedDtoUser)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.saveNewUser(expectedDtoUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtoUser, response.getBody());
    }

    @Test
    void updateUser_whenInvoked_thenResponseStatusOkWithUserInBody() {
        Mockito.when(userService.updateUser(expectedDtoUser, expectedUserId)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.updateUser(expectedDtoUser, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtoUser, response.getBody());
    }

    @Test
    void getUser_whenInvoked_thenResponseStatusOkWithUserInBody() {
        Mockito.when(userService.getUser(expectedUserId)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.getUser(expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserId, response.getBody().getId());
        assertEquals(expectedUser.getName(), response.getBody().getName());
    }

    @Test
    void deleteUser_whenInvoked_thenResponseStatusOkWithIntegerValueInBody() {
        Integer returnedValue = 0;
        Mockito.when(userService.deleteUser(expectedUserId)).thenReturn(returnedValue);

        ResponseEntity<Integer> response = userController.deleteUser(expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(returnedValue, response.getBody());
    }

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUsersCollectionInBody() {
        List<UserDto> expectedUsers = List.of(expectedDtoUser);
        Mockito.when(userService.getAllUsers()).thenReturn(expectedUsers);
                
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
    }
}
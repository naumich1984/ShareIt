package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto expectedDtoUser;
    private User expectedUser;
    private Long expectedUserId;

    @BeforeEach
    void setUp() {
        expectedUserId = 1L;
        expectedDtoUser  = new UserDto(expectedUserId,"name1","name1@test.ru");
        expectedUser  = new User(expectedUserId,"name1","name1@test.ru");
    }

    @SneakyThrows
    @Test
    void saveNewUser_whenUserIsValid_thenReturnedOk() {
        when(userService.saveUser(expectedDtoUser)).thenReturn(expectedUser);

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedDtoUser), result);
    }

    @SneakyThrows
    @Test
    void saveNewUser_whenUserIsNotValid_thenReturnedBadStatus() {
        expectedUser.setName(null);
        expectedDtoUser.setName(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(expectedDtoUser);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsValid_thenReturnedOk() {
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(userService.updateUser(expectedDtoUser, expectedUserId)).thenReturn(expectedUser);

        String result = mockMvc.perform(patch("/users/{userId}", expectedUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedDtoUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedDtoUser), result);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsNotFound_thenReturnedNotFound() {
        when(userService.updateUser(expectedDtoUser, expectedUserId)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/users/{userId}", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDtoUser)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getUser_whenUserIdValid_thenReturnedOk() {
        when(userService.getUser(expectedUserId)).thenReturn(new User());

        mockMvc.perform(get("/users/{userId}", expectedUserId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUser(expectedUserId);
    }

    @SneakyThrows
    @Test
    void getUser_whenUserIdNotValid_thenReturnedNotFound() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/{userId}", expectedUserId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Integer resultAfterDelete = 0;
        when(userService.deleteUser(expectedUserId)).thenReturn(resultAfterDelete);

        String result = mockMvc.perform(delete("/users/{userId}", expectedUserId)
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .content(resultAfterDelete.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(resultAfterDelete.toString(), result);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersExists_thenReturnedStatusOkListUsersNotEmpty() {
        List<UserDto> expectedDtoUsersList = List.of(expectedDtoUser);
        when(userService.getAllUsers()).thenReturn(expectedDtoUsersList);

        String result = mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedDtoUsersList)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedDtoUsersList), result);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersNotExists_thenReturnedStatusOkListUsersEmpty() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String result = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.emptyList())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(Collections.emptyList()), result);
    }
}
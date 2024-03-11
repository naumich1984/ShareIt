package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;
    private Item expectedItem;
    private User expectedUser;
    private Long expectedItemId;
    private Long expectedUserId;
    private Long expectedItemRequestId;
    private ItemRequest expectedItemRequest;
    private ItemRequestDto expectedItemRequestDto;
    private ItemRequestInfoDto expectedItemRequestInfoDto;

    @BeforeEach
    void setUp() {
        expectedItemId = 1L;
        expectedUserId = 1L;
        expectedItemRequestId = 1L;
        expectedUser = new User(expectedUserId, "userName1", "user1@email.ru");
        expectedItemRequestDto = new ItemRequestDto(expectedItemRequestId, "request1", LocalDateTime.now());
        expectedItemRequest = ItemRequestMapper.toItemRequest(expectedItemRequestDto, expectedUserId);
        expectedItem  = new Item(expectedItemId, "itemName1", "itemDescription1", true, expectedUser, expectedItemRequest);
        expectedItemRequestInfoDto = ItemRequestMapper.toItemRequestDtoInfo(expectedItemRequest, List.of(expectedItem));
    }

    @SneakyThrows
    @Test
    void addRequest_whenItemRequestIsValid_thenReturnedOk() {
        expectedItemRequestDto.setId(null);
        when(itemRequestService.addRequest(expectedItemRequest)).thenReturn(expectedItemRequest);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedItemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedItemRequestDto), result);
        verify(itemRequestService).addRequest(expectedItemRequest);
    }

    @SneakyThrows
    @Test
    void addRequest_whenItemRequestIsNotValid_thenExceptionThrown() {
        expectedItemRequestDto.setDescription(null);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedItemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).addRequest(expectedItemRequest);
    }

    @SneakyThrows
    @Test
    void addRequest_whenUserIdIsNotValid_thenExceptionThrown() {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedItemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).addRequest(expectedItemRequest);
    }

    @SneakyThrows
    @Test
    void addRequest_whenUserIsNotFound_thenExceptionThrown() {
        when(itemRequestService.addRequest(expectedItemRequest)).thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedItemRequestDto)))
                .andExpect(status().isNotFound());

        verify(itemRequestService).addRequest(expectedItemRequest);
    }

    @SneakyThrows
    @Test
    void getAllUserRequests_whenUserIdValid_thenReturnedItemRequestsList() {
        List<ItemRequestInfoDto> ItemRequestInfoDtoList = List.of(expectedItemRequestInfoDto);
        when(itemRequestService.getAllUserRequests(expectedUserId)).thenReturn(ItemRequestInfoDtoList);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(ItemRequestInfoDtoList), result);
        verify(itemRequestService).getAllUserRequests(expectedUserId);
    }

    @SneakyThrows
    @Test
    void getAllUserRequests_whenUserIdNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getAllUserRequests_whenUserIdNotFound_thenExceptionThrown() {
        List<ItemRequestInfoDto> ItemRequestInfoDtoList = List.of(expectedItemRequestInfoDto);
        when(itemRequestService.getAllUserRequests(expectedUserId)).thenReturn(ItemRequestInfoDtoList);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(ItemRequestInfoDtoList), result);
        verify(itemRequestService).getAllUserRequests(expectedUserId);
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenUserValid_thenReturnedItemRequestsList() {
        List<ItemRequestInfoDto> ItemRequestInfoDtoList = List.of(expectedItemRequestInfoDto);
        when(itemRequestService.getAllOtherUsersRequests(expectedUserId, 0, 1)).thenReturn(ItemRequestInfoDtoList);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(ItemRequestInfoDtoList), result);
        verify(itemRequestService).getAllOtherUsersRequests(expectedUserId, 0, 1);
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenUserNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenUserNotFound_thenExceptionThrown() {
        when(itemRequestService.getAllOtherUsersRequests(expectedUserId, 0, 1))
                .thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getAllOtherUsersRequests(expectedUserId, 0, 1);
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenParamSizeNotValid_thenExceptionThrown() {
            mockMvc.perform(get("/requests/all")
                            .header("X-Sharer-User-Id", expectedUserId)
                            .param("from", "0")
                            .param("size", "-1"))
                    .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenParamFromNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getAllOtherUsersRequests_whenParamsNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenValid_thenReturnedItemRequest() {
        when(itemRequestService.getRequestById(expectedItemRequestId, expectedUserId)).thenReturn(expectedItemRequestInfoDto);

        String result = mockMvc.perform(get("/requests/{requestId}", expectedItemRequestId)
                .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedItemRequestInfoDto), result);
        verify(itemRequestService).getRequestById(expectedItemRequestId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenItemRequestNotFound_thenExceptionThrown() {
        when(itemRequestService.getRequestById(expectedItemRequestId, expectedUserId))
                .thenThrow(new NotFoundException("Request not found!"));

        mockMvc.perform(get("/requests/{requestId}", expectedItemRequestId)
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getRequestById(expectedItemRequestId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenUserNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/requests/{requestId}", expectedItemRequestId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }
}
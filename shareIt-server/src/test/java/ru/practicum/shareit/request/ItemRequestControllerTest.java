package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;
    private Item expectedItem;
    private User expectedUser;
    private Long expectedItemId;
    private Long expectedUserId;
    private Long expectedItemRequestId;
    private ItemRequest expectedItemRequest;
    private ItemRequestDto expectedItemRequestDto;
    private ItemRequestInfoDto expectedItemRequestInfoDto;

    @InjectMocks
    private ItemRequestController itemRequestController;

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

    @Test
    void addRequest_whenInvoked_thenResponseStatusOkWithItemRequestInBody() {
        Mockito.when(itemRequestService.addRequest(expectedItemRequest)).thenReturn(expectedItemRequest);

        ResponseEntity<ItemRequestDto> response = itemRequestController.addRequest(expectedItemRequestDto, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemRequestDto.toString(), response.getBody().toString());
    }

    @Test
    void getAllUserRequests() {
        List<ItemRequestInfoDto> expectedItemRequestList = List.of(expectedItemRequestInfoDto);
        Mockito.when(itemRequestService.getAllUserRequests(expectedUserId)).thenReturn(expectedItemRequestList);

        ResponseEntity<List<ItemRequestInfoDto>> response = itemRequestController.getAllUserRequests(expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemRequestList.toString(), response.getBody().toString());
    }

    @Test
    void getAllOtherUsersRequests() {
        List<ItemRequestInfoDto> expectedItemRequestList = List.of(expectedItemRequestInfoDto);
        Mockito.when(itemRequestService.getAllOtherUsersRequests(expectedUserId, 0, 1)).thenReturn(expectedItemRequestList);

        ResponseEntity<List<ItemRequestInfoDto>> response = itemRequestController.getAllOtherUsersRequests(expectedUserId, 0, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemRequestList.toString(), response.getBody().toString());
    }

    @Test
    void getRequestById() {
        Mockito.when(itemRequestService.getRequestById(expectedItemRequestId, expectedUserId)).thenReturn(expectedItemRequestInfoDto);

        ResponseEntity<ItemRequestInfoDto> response = itemRequestController.getRequestById(expectedItemRequestId, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemRequestInfoDto.toString(), response.getBody().toString());
    }
}
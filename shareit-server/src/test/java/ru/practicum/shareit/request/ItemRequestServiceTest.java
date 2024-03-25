package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.CommonPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    private User expectedUser;
    private Long expectedUserId;
    private Long expectedItemRequestId;
    private ItemRequest expectedItemRequest;
    private Item expectedItem;
    private ItemRequestInfoDto expectedItemRequestInfoDto;


    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    @BeforeEach
    void setUp() {
        expectedUserId = 1L;
        expectedUser = new User(expectedUserId, "user1", "user1@email.ru");
        expectedItemRequestId = 1L;
        expectedItemRequest = new ItemRequest(expectedItemRequestId, "request1", expectedUser, LocalDateTime.now());
        expectedItem = new Item(1L, "nameItem1", "descriptionItem1", true, expectedUser, expectedItemRequest);
        expectedItemRequestInfoDto = ItemRequestMapper.toItemRequestDtoInfo(expectedItemRequest,List.of(expectedItem));
    }

    @Test
    void addItemRequest_whenInvoked_thenReturnedItemRequest() {
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(itemRequestRepository.save(expectedItemRequest)).thenReturn(expectedItemRequest);

        ItemRequest actualItemRequest = itemRequestService.addRequest(expectedItemRequest);

        assertEquals(expectedItemRequest, actualItemRequest);
        assertEquals(expectedItemRequest.hashCode(), actualItemRequest.hashCode());
        verify(itemRequestRepository).save(expectedItemRequest);
    }

    @Test
    void addItemRequest_whenUserNotExists_thenNotFoundExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(expectedItemRequest));
        verify(itemRequestRepository, never()).save(expectedItemRequest);
    }

    @Test
    void getAllUserRequests_whenInvoked_thenReturnedItemRequestsList() {
        List<ItemRequest> itemRequestList = List.of(expectedItemRequest);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(itemRequestRepository.findAllRequestWithItemsByUserId(expectedUserId)).thenReturn(itemRequestList);
        when(itemRepository.findAllByRequestIdList(List.of(1L))).thenReturn(List.of(expectedItem));

        List<ItemRequestInfoDto> actualItemRequestList = itemRequestService.getAllUserRequests(expectedUserId);

        assertEquals(expectedItemRequest.getDescription(), actualItemRequestList.get(0).getDescription());
        assertEquals(itemRequestList.size(), actualItemRequestList.size());
        verify(itemRequestRepository).findAllRequestWithItemsByUserId(expectedUserId);
    }

    @Test
    void getAllUserRequests_whenUserNotFound_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllUserRequests(expectedUserId));
        verifyNoInteractions(itemRequestRepository, itemRepository);
    }

    @Test
    void getAllOtherUsersRequests_whenInvoked_thenReturnedItemRequestsList() {
        List<ItemRequest> itemRequestList = List.of(expectedItemRequest);
        CommonPageRequest firstPage = new CommonPageRequest(0, 1);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(itemRequestRepository.findAllRequestWithItemsByNotUserId(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(List.of(expectedItemRequest), firstPage, 1));
        when(itemRepository.findAllByRequestIdList(List.of(1L))).thenReturn(List.of(expectedItem));

        List<ItemRequestInfoDto> actualItemRequestList = itemRequestService.getAllOtherUsersRequests(expectedUserId,0,1);

        assertEquals(expectedItemRequest.getDescription(), actualItemRequestList.get(0).getDescription());
        assertEquals(itemRequestList.size(), actualItemRequestList.size());
        verify(itemRequestRepository).findAllRequestWithItemsByNotUserId(expectedUserId, firstPage);
    }

    @Test
    void getAllOtherUsersRequests_whenInvokedAndSizeIsNull_thenReturnedItemRequestsList() {
        List<ItemRequest> itemRequestList = List.of(expectedItemRequest);
        CommonPageRequest firstPage = new CommonPageRequest(0, 1);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(itemRequestRepository.findAllRequestWithItemsByNotUserId(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(List.of(expectedItemRequest), firstPage, 1));
        when(itemRepository.findAllByRequestIdList(List.of(1L))).thenReturn(List.of(expectedItem));

        List<ItemRequestInfoDto> actualItemRequestList = itemRequestService.getAllOtherUsersRequests(expectedUserId,0,1);

        assertEquals(expectedItemRequest.getDescription(), actualItemRequestList.get(0).getDescription());
        assertEquals(itemRequestList.size(), actualItemRequestList.size());
        verify(itemRequestRepository).findAllRequestWithItemsByNotUserId(expectedUserId, firstPage);
    }

    @Test
    void getRequestById_whenInvoked_thenReturnedItemRequest() {
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(itemRequestRepository.findById(expectedItemRequestId)).thenReturn(Optional.of(expectedItemRequest));
        when(itemRepository.findAllByRequestIdList(List.of(1L))).thenReturn(List.of(expectedItem));

        ItemRequestInfoDto actualItemRequest = itemRequestService.getRequestById(expectedItemRequestId, expectedUserId);

        assertEquals(expectedItemRequest.getDescription(), actualItemRequest.getDescription());
        verify(itemRequestRepository).findById(expectedItemRequestId);
        verify(itemRepository).findAllByRequestIdList(List.of(1L));
    }

    @Test
    void getRequestById_whenUserNotFound_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(expectedItemRequestId, expectedUserId));
        verifyNoInteractions(itemRepository, itemRequestRepository);
    }

    @Test
    void getRequestById_whenItemNotFound_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(itemRequestRepository.findById(expectedItemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(expectedItemRequestId, expectedUserId));
        verify(itemRequestRepository).findById(expectedItemRequestId);
        verifyNoInteractions(itemRepository);
    }
}
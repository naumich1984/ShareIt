package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Value("${booking.statuses.worked}")
    private List<BookingStatus> workedStatuses;
    private Long expectedItemId;
    private Long expectedRequestId;
    private ItemDto expectedDtoItem;
    private ItemDto updatedDtoItem;
    private Item expectedItem;
    private Item updatedItem;
    private ItemRequest expectedRequest;
    private User expectedUser;
    private User expectedUser2;
    private Long expectedUserId;
    private Long expectedUserId2;
    private Booking expectedBooking;
    private PageRequest firstPage;
    private CommentDto expectedCommentDto;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @BeforeEach
    void setUp() {
        expectedItemId = 1L;
        expectedRequestId = 1L;
        expectedUserId = 1L;
        expectedUserId2 = 2L;
        expectedUser = new User(expectedUserId, "user1", "user1@email.ru");
        expectedUser2 = new User(expectedUserId2, "user2", "user2@email.ru");
        expectedRequest = new ItemRequest(expectedRequestId, "requestDescription", expectedUser2, LocalDateTime.now());
        expectedDtoItem = new ItemDto(expectedItemId, "nameItem1", "descriptionItem1", true, expectedRequestId);
        updatedDtoItem = new ItemDto(expectedItemId, "nameItem2", "descriptionItem2", false, expectedRequestId);
        expectedItem = ItemMapper.toItem(expectedDtoItem, expectedRequestId);
        updatedItem = ItemMapper.toItem(updatedDtoItem, expectedRequestId);
        expectedBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                expectedItem, expectedUser, BookingStatus.APPROVED);
        firstPage = PageRequest.of(0, 1);
        expectedCommentDto = new CommentDto(1L, "commentText", "authorName", LocalDateTime.now());
    }

    @Test
    void itemDtoItemCheck() {
        ItemDto checkedDtoItem = new ItemDto(expectedItemId, "nameItem1", "descriptionItem1", true, expectedRequestId);

        assertEquals(expectedDtoItem, checkedDtoItem);
        assertEquals(expectedDtoItem.hashCode(), checkedDtoItem.hashCode());
    }

    @Test
    void itemRequestDtoCheck() {
        ItemRequest checkedRequest = new ItemRequest(expectedRequestId, "requestDescription", expectedUser2, LocalDateTime.now());

        assertEquals(ItemRequestMapper.toItemRequestDto(expectedRequest), ItemRequestMapper.toItemRequestDto(checkedRequest));
        assertEquals(ItemRequestMapper.toItemRequestDto(expectedRequest).hashCode(), ItemRequestMapper.toItemRequestDto(checkedRequest).hashCode());
    }

    @Test
    void saveItem_whenInvoked_thenReturnedItem() {
        when(userService.getUser(expectedUserId)).thenReturn(new User());
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

        Item actualItem = itemService.addItem(expectedDtoItem, expectedUserId);

        assertEquals(expectedItem, actualItem);
        assertEquals(expectedItem.hashCode(), actualItem.hashCode());
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void saveItem_whenUserNotExists_thenNotFoundExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemService.addItem(expectedDtoItem, expectedUserId));
        verify(itemRepository, never()).save(expectedItem);
    }

    @Test
    void saveItem_whenNameNotUnique_thenExceptionThrown() {
        when(itemRepository.save(expectedItem)).thenThrow(new IllegalArgumentException("Item name not unique"));

        assertThrows(IllegalArgumentException.class, () -> itemService.addItem(expectedDtoItem, expectedUserId));
        verify(itemRepository).save(expectedItem);
    }

    @Test
    void updateItem_whenItemFound_thenReturnedItem() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.ofNullable(expectedItem));
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

        Item actualItem = itemService.updateItem(expectedDtoItem, expectedItemId, expectedUserId);

        assertEquals(expectedItem, actualItem);
        verify(itemRepository).save(expectedItem);
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(expectedDtoItem, expectedItemId, expectedUserId));
        verify(itemRepository, never()).save(expectedItem);
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void updateItem_whenItemFoundAndUserNotFound_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.ofNullable(expectedItem));
        expectedItem.setOwner(expectedUser2);

        NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(expectedDtoItem, expectedItemId, expectedUserId));
        assertEquals("User not found!", actualException.getMessage());
        verify(itemRepository, never()).save(expectedItem);
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void updateItem_whenItemFound_thenChangeOnlyAvailableFields() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.ofNullable(expectedItem));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        Item actualItem = itemService.updateItem(updatedDtoItem, expectedItemId, expectedUserId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals(savedItem, updatedItem);
    }

    @Test
    void getAllUserItems_whenInvoked_thenReturnedListItems() {
        List<Item> userItems = List.of(expectedItem);
        when(itemRepository.findAllByOwner(expectedUserId)).thenReturn(userItems);
        when(bookingRepository.findLastItemBooking(expectedItemId, workedStatuses, firstPage))
                .thenReturn(Page.empty());
        when(bookingRepository.findNextItemBooking(expectedItemId, workedStatuses, firstPage))
                .thenReturn(Page.empty());

        List<ItemInfoDto> actualUserItems = itemService.getAllUserItems(expectedUserId);

        assertEquals(userItems.get(0).getName(), actualUserItems.get(0).getName());
        assertEquals(userItems.get(0).getDescription(), actualUserItems.get(0).getDescription());
        assertEquals(userItems.get(0).getAvailable(), actualUserItems.get(0).getAvailable());
    }

    @Test
    void getAllUserItems_whenInvokedAndItemsNotExists_thenReturnedEmptyList() {
        when(itemRepository.findAllByOwner(expectedUserId)).thenReturn(Collections.emptyList());

        List<ItemInfoDto> actualUserItems = itemService.getAllUserItems(expectedUserId);

        assertEquals(0, actualUserItems.size());
        verifyNoInteractions(bookingRepository);
        verify(itemRepository).findAllByOwner(expectedUserId);
    }

    @Test
    void getItem_whenItemFound_thenReturnItem() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.of(expectedItem));

        Item actualItem = itemService.getItem(expectedItemId, expectedUserId);

        assertEquals(expectedItem, actualItem);
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void getItem_whenItemNotFound_thenExceptionThrown() {
        when(itemRepository.findById(expectedItemId)).thenThrow(new NotFoundException("Item not found"));

        assertThrows(NotFoundException.class, () -> itemService.getItem(expectedItemId, expectedUserId));
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void getItem_whenItemNotFound2_thenExceptionThrown() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(expectedItemId, expectedUserId));
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void getItemsBySearch_whenPatternNotBlank_thenReturnedListOfItems() {
        String pattern = "pattern";
        List<Item> expectedItemsList = List.of(expectedItem);
        when(itemRepository.findAllBySearch(pattern)).thenReturn(expectedItemsList);

        List<Item> actualItems = itemService.getItemsBySearch(pattern, expectedUserId);

        assertEquals(expectedItemsList, actualItems);
        verify(itemRepository).findAllBySearch(pattern);
    }

    @Test
    void getItemsBySearch_whenPatternIsBlank_thenReturnedEmptyList() {
        String pattern = "";

        List<Item> actualItems = itemService.getItemsBySearch(pattern, expectedUserId);

        assertEquals(0, actualItems.size());
        verify(itemRepository, never()).findAllBySearch(pattern);
    }

    @Test
    void getItemWithBookings_whenItemExists_thenReturnedItemInfoDto() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.of(expectedItem));
        when(commentRepository.findAllCommentsByItemId(expectedItemId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastItemBooking(expectedItemId, workedStatuses, firstPage))
                .thenReturn(Page.empty());
        when(bookingRepository.findNextItemBooking(expectedItemId, workedStatuses, firstPage))
                .thenReturn(Page.empty());

        ItemInfoDto actualItemDtoInfo = itemService.getItemWithBookings(expectedItemId, expectedUserId);

        assertEquals(expectedItem.getName(), actualItemDtoInfo.getName());
        assertEquals(expectedItem.getDescription(), actualItemDtoInfo.getDescription());
        assertEquals(expectedItem.getAvailable(), actualItemDtoInfo.getAvailable());
        verify(itemRepository).findById(expectedItemId);
        verify(commentRepository).findAllCommentsByItemId(expectedItemId);
        verifyNoMoreInteractions(bookingRepository, commentRepository, itemRepository);
    }

    @Test
    void getItemWithBookings_whenItemExistsAndBookingExists_thenReturnedItemInfoDto() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.of(expectedItem));
        when(commentRepository.findAllCommentsByItemId(expectedItemId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastItemBooking(expectedItemId, workedStatuses, firstPage))
                .thenReturn(new PageImpl<>(List.of(expectedBooking), firstPage, 1));
        when(bookingRepository.findNextItemBooking(expectedItemId, workedStatuses, firstPage))
                .thenReturn(new PageImpl<>(List.of(expectedBooking), firstPage, 1));

        ItemInfoDto actualItemDtoInfo = itemService.getItemWithBookings(expectedItemId, expectedUserId);

        assertEquals(expectedItem.getName(), actualItemDtoInfo.getName());
        assertEquals(expectedItem.getDescription(), actualItemDtoInfo.getDescription());
        assertEquals(expectedItem.getAvailable(), actualItemDtoInfo.getAvailable());
        verify(itemRepository).findById(expectedItemId);
        verify(commentRepository).findAllCommentsByItemId(expectedItemId);
        verifyNoMoreInteractions(bookingRepository, commentRepository, itemRepository);
    }

    @Test
    void getItemWithBookings_whenItemNotExist_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(expectedItemId)).thenThrow(new NotFoundException("Item not found!"));

        assertThrows(NotFoundException.class, () -> itemService.getItemWithBookings(expectedItemId, expectedUserId));
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void getItemWithBookings_whenItemNotExist2_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemWithBookings(expectedItemId, expectedUserId));
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
        verify(itemRepository).findById(expectedItemId);
    }

    @Test
    void getItemWithBookings_whenItemExistAndUserNotOwner_thenReturnedItemInfoDto() {
        when(itemRepository.findById(expectedItemId)).thenReturn(Optional.of(expectedItem));
        when(commentRepository.findAllCommentsByItemId(expectedItemId)).thenReturn(Collections.emptyList());

        ItemInfoDto actualItemDtoInfo = itemService.getItemWithBookings(expectedItemId, expectedUserId2);

        assertEquals(expectedItem.getName(), actualItemDtoInfo.getName());
        assertEquals(expectedItem.getDescription(), actualItemDtoInfo.getDescription());
        assertEquals(expectedItem.getAvailable(), actualItemDtoInfo.getAvailable());
        verify(itemRepository).findById(expectedItemId);
        verifyNoInteractions(bookingRepository);
        verify(commentRepository).findAllCommentsByItemId(expectedItemId);
    }

    @Test
    void addCommentItem_whenBookingFinished_thenAddComment() {
        when(userService.getUser(expectedUserId)).thenReturn(new User());
        when(bookingRepository.findLastFinishedBookingByItemIdAndUserId(expectedItemId, expectedUserId,BookingStatus.APPROVED,
                firstPage)).thenReturn(new PageImpl<>(List.of(expectedBooking), firstPage, 1));

        itemService.addCommentItem(expectedCommentDto, expectedItemId, expectedUserId);

        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment actualComment = commentArgumentCaptor.getValue();
        assertEquals(expectedCommentDto.getText(), actualComment.getText());
        assertEquals(expectedCommentDto.hashCode(), ItemMapper.toCommentDto(actualComment).hashCode());
    }

    @Test
    void addCommentItem_whenUserNotExists_thenNotFoundExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        assertThrows(NotFoundException.class, () ->
                itemService.addCommentItem(expectedCommentDto, expectedItemId, expectedUserId));
        verifyNoInteractions(itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void addCommentItem_whenBookingNotExists_thenBadRequestExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenReturn(new User());
        when(bookingRepository.findLastFinishedBookingByItemIdAndUserId(expectedItemId, expectedUserId,BookingStatus.APPROVED,
                firstPage)).thenReturn(Page.empty());

        assertThrows(BadRequestException.class, () ->
                itemService.addCommentItem(expectedCommentDto, expectedItemId, expectedUserId));
        verifyNoInteractions(commentRepository);
        verify(bookingRepository).findLastFinishedBookingByItemIdAndUserId(expectedItemId, expectedUserId,BookingStatus.APPROVED,
                firstPage);
    }
}
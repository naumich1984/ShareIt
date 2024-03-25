package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.CommonPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
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
class BookingServiceTest {

    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    private Long expectedItemId;
    private Long expectedRequestId;
    private ItemDto expectedDtoItem;
    private Item expectedItem;
    private Item expectedItemOwner;
    private User expectedUser;
    private Long expectedUserId;
    private Long expectedUserId2;
    private Long expectedBookingId;
    private Booking expectedBooking;
    private BookingDto expectedBookingDto;
    private CommonPageRequest firstPage;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        expectedItemId = 1L;
        expectedRequestId = 1L;
        expectedUserId = 1L;
        expectedUserId2 = 2L;
        expectedBookingId = 1L;
        expectedUser = new User(expectedUserId, "user1", "user1@email.ru");
        expectedDtoItem = new ItemDto(expectedItemId, "nameItem1", "descriptionItem1", true, expectedRequestId);
        expectedItem = ItemMapper.toItem(expectedDtoItem, expectedUserId2);
        expectedItemOwner = ItemMapper.toItem(expectedDtoItem, expectedUserId);
        expectedBookingDto = new BookingDto(expectedBookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        expectedBooking = BookingMapper.toBooking(expectedBookingDto, expectedItem, expectedUser);
        firstPage = new CommonPageRequest(0, 1);
    }

    @Test
    void toBookingDto_checkEqualsAndHashCode() {
        BookingDto checkedBookingDto = new BookingDto(expectedBookingId, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        assertEquals(expectedBookingDto, checkedBookingDto);
        assertEquals(expectedBookingDto.hashCode(), checkedBookingDto.hashCode());
    }

    @Test
    void toBookingDtoInfo_checkEqualsAndHashCode() {
        Booking checkedBooking = new Booking(expectedBookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                expectedItem, expectedUser, BookingStatus.WAITING);
        BookingDtoInfo checkedBookingDtoInfo = BookingMapper.toBookingDtoInfo(checkedBooking);
        BookingDtoInfo expectedBookingDtoInfo = BookingMapper.toBookingDtoInfo(expectedBooking);
        expectedBookingDtoInfo.setId(expectedBookingId);

        assertEquals(expectedBookingDtoInfo, checkedBookingDtoInfo);
        assertEquals(expectedBookingDtoInfo.hashCode(), checkedBookingDtoInfo.hashCode());
    }

    @Test
    void addBooking_whenInvoked_thenReturnedBooking() {
        when(itemService.getItem(expectedItemId, expectedUserId)).thenReturn(expectedItem);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.addBooking(expectedBookingDto,expectedUserId);

        assertEquals(expectedBooking, actualBooking);
        assertEquals(expectedBooking, actualBooking);
        assertEquals(expectedBooking.hashCode(), actualBooking.hashCode());
        verify(bookingRepository).save(expectedBooking);
    }

    @Test
    void addBooking_whenItemNotFound_thenExceptionThrown() {
        when(itemService.getItem(expectedItemId, expectedUserId)).thenThrow(new NotFoundException("Item not found!"));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(expectedBookingDto,expectedUserId));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void addBooking_whenItemNotFound2_thenExceptionThrown() {
        when(itemService.getItem(expectedItemId, expectedUserId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(expectedBookingDto,expectedUserId));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void addBooking_whenItemNotAvailable_thenExceptionThrown() {
        expectedItem.setAvailable(false);
        when(itemService.getItem(expectedItemId, expectedUserId)).thenReturn(expectedItem);

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(expectedBookingDto,expectedUserId));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void addBooking_whenUserNotFound_thenExceptionThrown() {
        when(itemService.getItem(expectedItemId, expectedUserId)).thenReturn(expectedItem);
        when(userService.getUser(expectedUserId)).thenReturn(User.builder().id(111L).build());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(expectedBookingDto,expectedUserId));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void addBooking_whenUserIsOwner_thenExceptionThrown() {
        when(itemService.getItem(expectedItemId, expectedUserId)).thenReturn(expectedItemOwner);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(expectedBookingDto,expectedUserId));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void approveBooking_whenInvoked_thenReturnedBooking() {
        expectedBooking.setStatus(BookingStatus.WAITING);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.getByBookingIdAndOwnerItemId(expectedBookingId, expectedUserId)).thenReturn(Optional.of(expectedBooking));
        when(itemService.getItem(expectedItemId, expectedUserId)).thenReturn(expectedItemOwner);
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.approveBooking(expectedBookingId, true, expectedUserId);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingRepository).save(expectedBooking);
    }

    @Test
    void approveBooking_whenUserNotFound_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(expectedBookingId, true, expectedUserId));

        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void approveBooking_whenUserNotFound2_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenReturn(User.builder().id(111L).build());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(expectedBookingId, true, expectedUserId));

        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void approveBooking_whenBookingNotFound_thenExceptionThrown() {
        expectedBooking.setStatus(BookingStatus.WAITING);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.getByBookingIdAndOwnerItemId(expectedBookingId, expectedUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(expectedBookingId, true, expectedUserId));

        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void approveBooking_whenWrongStatus_thenExceptionThrown() {
        expectedBooking.setStatus(BookingStatus.REJECTED);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.getByBookingIdAndOwnerItemId(expectedBookingId, expectedUserId)).thenReturn(Optional.of(expectedBooking));

        assertThrows(BadRequestException.class, () -> bookingService.approveBooking(expectedBookingId, true, expectedUserId));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void approveBooking_whenItemNotOwner_thenExceptionThrown() {
        expectedBooking.setStatus(BookingStatus.WAITING);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.getByBookingIdAndOwnerItemId(expectedBookingId, expectedUserId)).thenReturn(Optional.of(expectedBooking));
        when(itemService.getItem(expectedItemId, expectedUserId)).thenReturn(expectedItem);

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(expectedBookingId, true, expectedUserId));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void getBooking_whenInvoked_thenReturnedBooking() {
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.getByBookingIdAndOwnerItemId(expectedBookingId, expectedItemOwner.getId())).thenReturn(Optional.of(expectedBooking));

        Booking actualBooking = bookingService.getBooking(expectedBookingId, expectedUserId);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingRepository).getByBookingIdAndOwnerItemId(expectedBookingId, expectedItemOwner.getId());
    }

    @Test
    void getBooking_whenUserNotFound_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(expectedBookingId, expectedUserId));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getBooking_whenUserNotFound2_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenReturn(User.builder().id(111L).build());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(expectedBookingId, expectedUserId));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getBooking_whenBookingNotFound_thenExceptionThrown() {
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.getByBookingIdAndOwnerItemId(expectedBookingId, expectedItemOwner.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,() -> bookingService.getBooking(expectedBookingId, expectedUserId));
        verify(bookingRepository).getByBookingIdAndOwnerItemId(expectedBookingId, expectedItemOwner.getId());
    }

    @Test
    void getAllUserBooking_whenInvokedAndStateAll_thenReturnedBookingList() {
        String state = "ALL";
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByUserId(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllUserBooking(state, expectedUserId, 0, 1);

        assertEquals(actualBookingList, actualBookingList);
    }

   @Test
    void getAllUserBooking_whenInvokedAndStateWaiting_thenReturnedBookingList() {
        BookingStatus expectedState = BookingStatus.WAITING;
        expectedBooking.setStatus(expectedState);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByUserIdAndByStatus(expectedUserId, expectedState, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllUserBooking(expectedState.name(), expectedUserId, 0, 1);

        assertEquals(actualBookingList, actualBookingList);
    }

    @Test
    void getAllUserBooking_whenInvokedAndStatePast_thenReturnedBookingList() {
        String state = "PAST";
        expectedBooking.setStatus(BookingStatus.APPROVED);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByUserIdAndByStatusPast(expectedUserId, BookingStatus.APPROVED, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllUserBooking(state, expectedUserId, 0, 1);

        assertEquals(actualBookingList, actualBookingList);
    }

    @Test
    void getAllUserBooking_whenInvokedAndStateCurrent_thenReturnedBookingList() {
        String state = "CURRENT";
        expectedBooking.setStatus(BookingStatus.APPROVED);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByUserIdAndByStatusCurrent(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllUserBooking(state, expectedUserId, 0, 1);

        assertEquals(actualBookingList, actualBookingList);
    }

    @Test
    void getAllUserBooking_whenInvokedAndStateFuture_thenReturnedBookingList() {
        String state = "FUTURE";
        expectedBooking.setStatus(BookingStatus.APPROVED);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByUserIdAndByStatusFuture(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllUserBooking(state, expectedUserId, 0, 1);

        assertEquals(actualBookingList, actualBookingList);
    }

    @Test
    void getAllUserBooking_whenInvokedAndStateRejected_thenReturnedBookingList() {
        String state = "REJECTED";
        expectedBooking.setStatus(BookingStatus.REJECTED);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByUserIdAndByStatus(expectedUserId, BookingStatus.REJECTED, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllUserBooking(state, expectedUserId, 0, 1);

        assertEquals(actualBookingList, actualBookingList);
    }

    @Test
    void getAllUserBooking_whenUserNotFound_thenExceptionThrown() {
        String state = "REJECTED";
        expectedBooking.setStatus(BookingStatus.REJECTED);
        when(userService.getUser(expectedUserId)).thenReturn(User.builder().id(111L).build());

        assertThrows(NotFoundException.class, () -> bookingService.getAllUserBooking(state, expectedUserId, 0, 1));
    }

    @Test
    void getAllUserBooking_whenStateIsNull_thenExceptionThrown() {
        String state = "123";
        expectedBooking.setStatus(BookingStatus.REJECTED);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);

        assertThrows(IllegalArgumentException.class, () -> bookingService.getAllUserBooking(state, expectedUserId, 0, 1));
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateAll_thenReturnedBookingList() {
        String state = "ALL";
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerId(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerId(expectedUserId, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateAll_thenReturnedEmptyList() {
        String state = "ALL";
        List<Booking> expectedBookingList = Collections.emptyList();
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerId(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerId(expectedUserId, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateWaiting_thenReturnedBookingList() {
        String state = "WAITING";
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatus(expectedUserId, status, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatus(expectedUserId, status, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateWaiting_thenReturnedEmptyList() {
        String state = "WAITING";
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> expectedBookingList = Collections.emptyList();
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatus(expectedUserId, status, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatus(expectedUserId, status, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStatePast_thenReturnedBookingList() {
        String state = "PAST";
        BookingStatus status = BookingStatus.APPROVED;
        expectedBooking.setStatus(status);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatusPast(expectedUserId, status, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatusPast(expectedUserId, status, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStatePast_thenReturnedEmptyList() {
        String state = "PAST";
        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> expectedBookingList = Collections.emptyList();
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatusPast(expectedUserId, status, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatusPast(expectedUserId, status, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateCurrent_thenReturnedBookingList() {
        String state = "CURRENT";
        BookingStatus status = BookingStatus.WAITING;
        expectedBooking.setStatus(status);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatusCurrent(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatusCurrent(expectedUserId, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateCurrent_thenReturnedEmptyList() {
        String state = "CURRENT";
        BookingStatus status = BookingStatus.WAITING;
        expectedBooking.setStatus(status);
        List<Booking> expectedBookingList = Collections.emptyList();
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatusCurrent(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatusCurrent(expectedUserId, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateFuture_thenReturnedBookingList() {
        String state = "FUTURE";
        BookingStatus status = BookingStatus.APPROVED;
        expectedBooking.setStatus(status);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatusFuture(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatusFuture(expectedUserId, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateFuture_thenReturnedEmptyList() {
        String state = "FUTURE";
        BookingStatus status = BookingStatus.APPROVED;
        expectedBooking.setStatus(status);
        List<Booking> expectedBookingList = Collections.emptyList();
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatusFuture(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatusFuture(expectedUserId, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenInvokedStateRejected_thenReturnedBookingList() {
        String state = "REJECTED";
        BookingStatus status = BookingStatus.REJECTED;
        expectedBooking.setStatus(status);
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerIdAndByStatus(expectedUserId, status, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerIdAndByStatus(expectedUserId, status, firstPage);
    }

    @Test
    void getAllBookingsUserItems_whenUserNotOwner_thenExceptionThrown() {
        String state = "ALL";
        expectedUser.setId(2L);
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getAllBookingsUserItems_whenStateIsWrong_thenExceptionThrown() {
        String state = "WRONG";
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);

        assertThrows(IllegalArgumentException.class, () -> bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getAllBookingsUserItems_whenStateIsNull_thenReturnedAllStatusBookingList() {
        String state = null;
        List<Booking> expectedBookingList = Collections.emptyList();
        when(userService.getUser(expectedUserId)).thenReturn(expectedUser);
        when(bookingRepository.findAllBookingByOwnerId(expectedUserId, firstPage))
                .thenReturn(new PageImpl<>(expectedBookingList, firstPage, 1));

        List<Booking> actualBookingList = bookingService.getAllBookingsUserItems(state, expectedUserId, 0, 1);

        assertEquals(expectedBookingList, actualBookingList);
        verify(bookingRepository).findAllBookingByOwnerId(expectedUserId, firstPage);

    }

}
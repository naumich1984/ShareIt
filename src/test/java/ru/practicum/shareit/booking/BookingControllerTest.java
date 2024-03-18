package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;
    private Long expectedItemId;
    private Long expectedRequestId;
    private ItemDto expectedDtoItem;
    private Item expectedItem;
    private User expectedUser;
    private Long expectedUserId;
    private Long expectedUserId2;
    private Long expectedBookingId;
    private Booking expectedBooking;
    private BookingDto expectedBookingDto;
    private BookingDtoInfo expectedBookingDtoInfo;

    @InjectMocks
    private BookingController bookingController;

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
        expectedBookingDto = new BookingDto(expectedBookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        expectedBooking = BookingMapper.toBooking(expectedBookingDto, expectedItem, expectedUser);
        expectedBookingDtoInfo = BookingMapper.toBookingDtoInfo(expectedBooking);
    }

    @Test
    void addBooking_whenInvoked_thenResponseStatusOkWithBookingInBody() {
        when(bookingService.addBooking(expectedBookingDto, expectedUserId)).thenReturn(expectedBooking);

        ResponseEntity<BookingDtoInfo> response = bookingController.addBooking(expectedBookingDto, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDtoInfo, response.getBody());
    }

    @Test
    void updateBooking_whenInvoked_thenResponseStatusOkWithBookingInBody() {
        when(bookingService.approveBooking(expectedBookingId, true, expectedUserId)).thenReturn(expectedBooking);

        ResponseEntity<BookingDtoInfo> response = bookingController.updateBooking(expectedBookingId, true, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDtoInfo, response.getBody());
    }

    @Test
    void getBooking_whenInvoked_thenResponseStatusOkWithBookingInBody() {
        when(bookingService.getBooking(expectedBookingId, expectedUserId)).thenReturn(expectedBooking);

        ResponseEntity<BookingDtoInfo> response = bookingController.getBooking(expectedBookingId, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDtoInfo, response.getBody());
    }

    @Test
    void getAllUserBooking_whenInvoked_thenResponseStatusOkWithBookingInBody() {
        List<Booking> expectedBookingsList = List.of(expectedBooking);
        when(bookingService.getAllUserBooking("ALL", expectedUserId, 0, 1)).thenReturn(expectedBookingsList);

        ResponseEntity<List<BookingDtoInfo>> response = bookingController.getAllUserBooking("ALL", expectedUserId, 0, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(expectedBookingDtoInfo), response.getBody());
    }

    @Test
    void getAllBookingsUserItems_whenInvoked_thenResponseStatusOkWithBookingInBody() {
        List<Booking> expectedBookingsList = List.of(expectedBooking);
        when(bookingService.getAllBookingsUserItems("ALL", expectedUserId, 0, 1)).thenReturn(expectedBookingsList);

        ResponseEntity<List<BookingDtoInfo>> response = bookingController.getAllBookingsUserItems("ALL", expectedUserId, 0, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(expectedBookingDtoInfo), response.getBody());

    }
}
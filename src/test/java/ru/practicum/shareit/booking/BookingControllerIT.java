package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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

    @SneakyThrows
    @Test
    void addBooking_whenInvokedAndValid_thenReturnedOk() {
        when(bookingService.addBooking(expectedBookingDto, expectedUserId)).thenReturn(expectedBooking);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedBookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedBookingDtoInfo), result);
        verify(bookingService).addBooking(expectedBookingDto, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addBooking_whenBookingNotValid_thenExceptionThrown() {
        expectedBookingDto.setEnd(null);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedBookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(expectedBookingDto, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addBooking_whenUserNotValid_thenExceptionThrown() {
        expectedBookingDto.setEnd(null);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedBookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(expectedBookingDto, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addBooking_whenUserNotFound_thenExceptionThrown() {
        when(bookingService.addBooking(expectedBookingDto, expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedBookingDto)))
                .andExpect(status().isNotFound());

        verify(bookingService).addBooking(expectedBookingDto, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenApproved_thenReturnedOk() {
        expectedBooking.setStatus(BookingStatus.APPROVED);
        expectedBookingDtoInfo.setStatus(BookingStatus.APPROVED);
        Boolean approved = true;
        when(bookingService.approveBooking(expectedBookingId, approved, expectedUserId)).thenReturn(expectedBooking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", expectedBookingId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedBookingDtoInfo), result);
        verify(bookingService).approveBooking(expectedBookingId, approved, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenRejected_thenReturnedOk() {
        expectedBooking.setStatus(BookingStatus.REJECTED);
        expectedBookingDtoInfo.setStatus(BookingStatus.REJECTED);
        Boolean approved = false;
        when(bookingService.approveBooking(expectedBookingId, approved, expectedUserId)).thenReturn(expectedBooking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", expectedBookingId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedBookingDtoInfo), result);
        verify(bookingService).approveBooking(expectedBookingId, approved, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenBookingNotFound_thenExceptionThrown() {
        Boolean approved = true;
        when(bookingService.approveBooking(expectedBookingId, approved, expectedUserId)).thenThrow(new NotFoundException("Booking not found!"));

        mockMvc.perform(patch("/bookings/{bookingId}", expectedBookingId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("approved", approved.toString()))
                .andExpect(status().isNotFound());

        verify(bookingService).approveBooking(expectedBookingId, approved, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenBookingStatusNotValid_thenExceptionThrown() {
        Boolean approved = true;
        when(bookingService.approveBooking(expectedBookingId, approved, expectedUserId)).thenThrow(new BadRequestException("Status incorrect!"));

        mockMvc.perform(patch("/bookings/{bookingId}", expectedBookingId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("approved", approved.toString()))
                .andExpect(status().isBadRequest());

        verify(bookingService).approveBooking(expectedBookingId, approved, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenUserNotValid_thenExceptionThrown() {
        Boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", expectedBookingId)
                        .param("approved", approved.toString()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(expectedBookingId, approved, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenApprovedNotValid_thenExceptionThrown() {
        Boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", expectedBookingId)
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(expectedBookingId, approved, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getBooking_whenInvoked_thenReturnedOk() {
        when(bookingService.getBooking(expectedBookingId, expectedUserId)).thenReturn(expectedBooking);

        String result = mockMvc.perform(get("/bookings/{bookingId}", expectedBookingId)
                .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedBookingDtoInfo), result);
        verify(bookingService).getBooking(expectedBookingId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getBooking_whenBookingNotFound_thenExceptionThrown() {
        when(bookingService.getBooking(expectedBookingId, expectedUserId)).thenThrow(new NotFoundException("Booking not found!"));

        mockMvc.perform(get("/bookings/{bookingId}", expectedBookingId)
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isNotFound());

        verify(bookingService).getBooking(expectedBookingId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getBooking_whenUserNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/bookings/{bookingId}", expectedBookingId))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBooking(expectedBookingId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getAllUserBooking_whenInvoked_thenReturnedOk() {
        List<Booking> expectedBookingList = List.of(expectedBooking);
        when(bookingService.getAllUserBooking("ALL", expectedUserId, 0, 1)).thenReturn(expectedBookingList);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size","1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(expectedBookingDtoInfo)), result);
        verify(bookingService).getAllUserBooking("ALL", expectedUserId, 0, 1);
    }

    @SneakyThrows
    @Test
    void getAllUserBooking_whenUserNotFound_thenExceptionThrown() {
        when(bookingService.getAllUserBooking("ALL", expectedUserId, 0, 1)).thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size","1"))
                .andExpect(status().isNotFound());

        verify(bookingService).getAllUserBooking("ALL", expectedUserId, 0, 1);
    }

    @SneakyThrows
    @Test
    void getAllUserBooking_whenBookingStatusNotFound_thenExceptionThrown() {
        when(bookingService.getAllUserBooking("ANY", expectedUserId, 0, 1)).thenThrow(new IllegalArgumentException("Status illegal!"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .param("state", "ANY")
                        .param("from", "0")
                        .param("size","1"))
                .andExpect(status().is5xxServerError());

        verify(bookingService).getAllUserBooking("ANY", expectedUserId, 0, 1);
    }

    @Test
    void getAllBookingsUserItems() {
    }
}
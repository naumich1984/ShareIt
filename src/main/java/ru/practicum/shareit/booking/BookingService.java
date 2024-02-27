package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingDto bookingDto, Long userId);

    Booking approveBooking(Long bookingId, Boolean approved, Long userId);

    Booking getBooking(long bookingId, Long userId);

    List<Booking> getAllUserBooking(String state, Long userId);

    List<Booking> getAllBookingsUserItems(String state, Long userId);

}

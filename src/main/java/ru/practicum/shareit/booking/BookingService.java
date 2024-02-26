package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(Booking booking, Long userId);

    Booking approveBooking(Booking booking, Long bookingId, Long id);

    Booking getBooking(long bookingId, Long userId);

    List<Booking> getAllUserBooking(BookingStatus status, Long userId);

    List<Booking> getAllBookingsUserItems(BookingStatus status, Long userId);

}

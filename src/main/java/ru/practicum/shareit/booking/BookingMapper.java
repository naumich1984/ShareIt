package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;


public class BookingMapper {
    public static BookingDtoInfo toBookingDto(Booking booking) {
        return new BookingDtoInfo(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDtoInfo.UserDto(booking.getBooker().getId()),
                new BookingDtoInfo.ItemDto(booking.getItem().getId(), booking.getItem().getName()));
    }

    public static Booking toBooking(BookingDto bookingDto, Long userId) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(bookingDto.getItemId(), null, null, null,
                        new User(userId, null, null), null),
                new User(userId, null, null),
                BookingStatus.WAITING
        );
    }
}

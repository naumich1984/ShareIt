package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;


public class BookingMapper {
    public static BookingDtoInfo toBookingDtoInfo(Booking booking) {
        return new BookingDtoInfo(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDtoInfo.UserDto(booking.getBooker().getId()),
                new BookingDtoInfo.ItemDto(booking.getItem().getId(), booking.getItem().getName()));
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd());
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING).build();
    }
}

package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoInfo {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserDto booker;
    private ItemDto item;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ItemDto {
        private Long id;
        private String name;
    }
}

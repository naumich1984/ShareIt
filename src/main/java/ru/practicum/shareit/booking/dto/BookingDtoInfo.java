package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDtoInfo {

    private Long id;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private BookingStatus status;
    private UserDto booker;
    private ItemDto item;

    @AssertTrue
    boolean isValidStartEndDates() {
        if (start != null && end != null) {
            return start.isBefore(end);
        }

        return false;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDto {
        Long id;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ItemDto {
        Long id;
        String name;
    }
}

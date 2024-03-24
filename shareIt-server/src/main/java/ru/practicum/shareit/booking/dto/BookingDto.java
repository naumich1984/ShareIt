package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"itemId"})
public class BookingDto {

    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;

    @AssertTrue
    boolean isValidStartEndDates() {
        if (start != null && end != null) {
            return start.isBefore(end);
        }

        return false;
    }
}

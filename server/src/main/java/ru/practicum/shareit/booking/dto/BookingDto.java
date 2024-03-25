package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"itemId"})
public class BookingDto {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}

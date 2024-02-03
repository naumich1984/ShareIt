package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Booking {
    private Long id;
    private Timestamp start;
    private Timestamp end;
    @NotNull
    private Item item;
    @NotNull
    private User booker;
    @NotNull
    private BookingStatus status;
}


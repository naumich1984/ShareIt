package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    @Size(max = 32000, message = "description length should be less 32000 symbols")
    private String description;
    @NotNull
    private User requestor;
    @NotNull
    private Timestamp created;
}
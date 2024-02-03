package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "name should not be blank")
    private String name;
    @Size(max = 32000, message = "description length should be less 32000 symbols")
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
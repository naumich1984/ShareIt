package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto, Long itemId, Long userId) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                new User(userId, userId.toString(), userId.toString()),
                null
        );
    }
}

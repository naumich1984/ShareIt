package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, Long userId);

    Item updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<Item> getAllUserItems(Long userId);

    Item getItem(Long itemId, Long userId);

    List<Item> getItemsBySearch(String pattern, Long userId);
}

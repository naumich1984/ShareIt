package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Item item, Long itemId, Long userId);

    List<Item> getAllUserItems(Long userId);

    Item getItem(Long itemId);

    List<Item> getItemsBySearch(String pattern);
}

package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, Long userId);

    Item updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemInfoDto> getAllUserItems(Long userId);

    Item getItem(Long itemId, Long userId);

    List<Item> getItemsBySearch(String pattern, Long userId);

    ItemInfoDto getItemWithBookings(Long itemId, Long userId);

    Comment addCommentItem(CommentDto commentDto,Long itemId, User user);
}

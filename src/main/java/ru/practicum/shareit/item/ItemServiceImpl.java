package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public Item addItem(ItemDto itemDto, Long userId) {

        return itemStorage.addItem(ItemMapper.toItem(itemDto, 0L, userId));
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long itemId, Long userId) {

        return itemStorage.updateItem(ItemMapper.toItem(itemDto, itemId, userId), itemId, userId);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {

        return itemStorage.getAllUserItems(userId);
    }

    @Override
    public Item getItem(Long itemId, Long userId) {

        return itemStorage.getItem(itemId);
    }

    @Override
    public List<Item> getItemsBySearch(String pattern, Long userId) {

        return itemStorage.getItemsBySearch(pattern);
    }
}

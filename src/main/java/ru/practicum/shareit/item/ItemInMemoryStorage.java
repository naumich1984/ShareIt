package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Component("ItemInMemoryStorage")
@RequiredArgsConstructor
@Slf4j
public class ItemInMemoryStorage implements ItemStorage {

    private Map<Long, Item> itemRepository = new HashMap<>();
    private Long itemSequence = 1L;
    private UserService userService;

    @Override
    public Item addItem(Item item) {
        log.debug("Add item to storage");
        Long itemId = itemRepository.keySet().stream().filter(k -> k.equals(item.getId())).findFirst().orElse(null);
        if (itemId != null ) {
            log.error("adding item already exists!");
            throw new ValidationException("User validation error");
        }
        item.setId(itemSequence);
        itemRepository.put(itemSequence++, item);

        return item;
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        log.debug("Update item in storage");
        Long updatedItemId = itemRepository.keySet()
                .stream().filter(k -> k.equals(itemId)).findFirst().orElse(null);
        if (updatedItemId == null) {
            log.error("updating item not exists!");
            throw new ValidationException("updating user not exists!");
        }
        Item updatedItem = itemRepository.get(updatedItemId);
        if (!updatedItem.getOwner().getId().equals(userId)) {
            log.error("updating item does not belong to the specified user!");
            throw new NotFoundException("updating item does not belong to the specified user!");
        }
        updatedItem.setName(Optional.ofNullable(item.getName()).orElse(updatedItem.getName()));
        updatedItem.setDescription(Optional.ofNullable(item.getDescription()).orElse(updatedItem.getDescription()));
        updatedItem.setAvailable(Optional.ofNullable(item.getAvailable()).orElse(updatedItem.getAvailable()));
        itemRepository.put(updatedItemId, updatedItem);

        return updatedItem;
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        log.debug("getAllUserItems");

        return itemRepository.values()
                .stream()
                .filter(v -> userId.equals(v.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(Long itemId) {
        log.debug("getItem");

        return itemRepository.get(itemId);
    }

    @Override
    public List<Item> getItemsBySearch(String pattern) {
        log.debug("getItemsBySearch");
        if (pattern.isBlank()) return Collections.EMPTY_LIST;

        return itemRepository.values()
                .stream()
                .filter(v -> v.getDescription().toLowerCase().contains(pattern.toLowerCase()))
                .filter(v -> v.getAvailable() == true)
                .collect(Collectors.toList());
    }
}

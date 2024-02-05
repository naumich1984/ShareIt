package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping("/items")
    public ResponseEntity<Item> addItem(@RequestBody @Valid ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("POST /items request");
        log.debug("X-Sharer-User-Id: {}", userId);

        return ResponseEntity.ok(itemService.addItem(itemDto, userService.getUser(userId).getId()));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Item> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("PATCH /items request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemId: {}", itemId);

        return ResponseEntity.ok(itemService.updateItem(itemDto, itemId, userService.getUser(userId).getId()));
    }

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /items request");
        log.debug("X-Sharer-User-Id: {}", userId);

        return ResponseEntity.ok(itemService.getAllUserItems(userId));
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<Item> getItem(@PathVariable long itemId,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /items/{itemId} request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemId: {}", itemId);

        return ResponseEntity.ok(itemService.getItem(itemId, userId));
    }

    @GetMapping("/items/search")
    public ResponseEntity<List<Item>> getItemsBySearch(@RequestParam(value = "text") String text,
                                                       @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /items/search?text={text}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("text: {}", text);

        return ResponseEntity.ok(itemService.getItemsBySearch(text, userId));
    }
}

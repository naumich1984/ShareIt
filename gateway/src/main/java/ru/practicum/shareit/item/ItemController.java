package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping("/items/{itemId}/comment")
    public ResponseEntity<Object> addCommentItem(@RequestBody @Valid CommentDto commentDto,
                                                  @PathVariable @NotNull Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("POST /items/{itemId}/comment");
        log.debug("X-Sharer-User-Id: {}", userId);

        return itemClient.addCommentItem(commentDto, itemId, userId);
    }

    @PostMapping("/items")
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("POST /items request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemDto.getId(): {}", itemDto.getId());

        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("PATCH /items request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemId: {}", itemId);

        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /items/{itemId} request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemId: {}", itemId);

        return itemClient.getItemWithBookings(itemId, userId);
    }

    @GetMapping("/items")
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /items request");
        log.debug("X-Sharer-User-Id: {}", userId);

        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/items/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam(value = "text") String text,
                                                       @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /items/search?text={text}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("text: {}", text);

        return itemClient.getItemsBySearch(text, userId);
    }
}

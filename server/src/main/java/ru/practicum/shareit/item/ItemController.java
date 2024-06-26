package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/items/{itemId}/comment")
    public ResponseEntity<CommentDto> addCommentItem(@RequestBody CommentDto commentDto,
                                                  @PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("POST /items/{itemId}/comment");
        log.debug("X-Sharer-User-Id: {}", userId);

        return ResponseEntity.ok(ItemMapper.toCommentDto(itemService.addCommentItem(commentDto, itemId, userId)));
    }

    @PostMapping("/items")
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("POST /items request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemDto.getId(): {}", itemDto.getId());

        return ResponseEntity.ok(ItemMapper.toItemDto(itemService.addItem(itemDto, userId)));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("PATCH /items request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemId: {}", itemId);

        return ResponseEntity.ok(ItemMapper.toItemDto(itemService.updateItem(itemDto, itemId, userId)));
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ItemInfoDto> getItem(@PathVariable long itemId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET /items/{itemId} request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("itemId: {}", itemId);

        return ResponseEntity.ok(itemService.getItemWithBookings(itemId, userId));
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemInfoDto>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET /items request");
        log.debug("X-Sharer-User-Id: {}", userId);

        return ResponseEntity.ok(itemService.getAllUserItems(userId));
    }

    @GetMapping("/items/search")
    public ResponseEntity<List<ItemDto>> getItemsBySearch(@RequestParam(value = "text") String text,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET /items/search?text={text}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("text: {}", text);

        return ResponseEntity.ok(itemService.getItemsBySearch(text, userId)
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList()));
    }
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping("/requests")
    public ResponseEntity<Object> addRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                 @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("POST /requests");
        log.debug("X-Sharer-User-Id: {}", userId);

        return itemRequestClient.addRequest(itemRequestDto, userId);
    }

    @GetMapping("/requests")
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /requests");
        log.debug("X-Sharer-User-Id: {}", userId);

        return itemRequestClient.getAllUserRequests(userId);
    }

    @GetMapping("/requests/all")
    public ResponseEntity<Object> getAllOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                                   @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                                             @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.debug("GET /requests/all?from={from}&size={size}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("from: {}", from);
        log.debug("size: {}", size);

        return itemRequestClient.getAllOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /requests/{requestId}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("requestId: {}", userId);

        return itemRequestClient.getRequestById(requestId, userId);
    }
}

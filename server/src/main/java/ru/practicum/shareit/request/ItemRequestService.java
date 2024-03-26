package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(ItemRequest itemRequest);

    List<ItemRequestInfoDto> getAllUserRequests(Long userId);

    List<ItemRequestInfoDto> getAllOtherUsersRequests(Long userId, Integer from, Integer size);

    ItemRequestInfoDto getRequestById(Long requestId, Long userId);
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    @Value("${itemrequest.pagination.default.size}")
    private Integer sizeItemRequestPage;

    @Override
    @Transactional
    public ItemRequest addRequest(ItemRequest itemRequest) {
        log.debug("addRequest");
        User user = userService.getUser(itemRequest.getRequestor().getId());

        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestInfoDto> getAllUserRequests(Long userId) {
        log.debug("getAllUserRequests");
        User user = userService.getUser(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllRequestWithItemsByUserId(userId);
        List<Item> itemList = itemRepository.findAllByRequestIdList(itemRequestList
                .stream()
                .map(item -> item.getId())
                .collect(Collectors.toList()));

        return itemRequestList
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoInfo(itemRequest, itemList
                        .stream()
                        .filter(f -> f.getRequest().getId().equals(itemRequest.getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestInfoDto> getAllOtherUsersRequests(Long userId, Integer from, Integer size) {
        log.debug("getAllOtherUsersRequests");
        from = from == null ? 0 : from;
        size = size == null ? 1 : size;
        Pageable pageable = PageRequest.of(from, size);
        User user = userService.getUser(userId);
        Page<ItemRequest> itemRequestList = itemRequestRepository.findAllRequestWithItemsByNotUserId(userId, pageable);
        List<Item> itemList = itemRepository.findAllByRequestIdList(itemRequestList
                .map(item -> item.getId())
                .getContent());

        return itemRequestList
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoInfo(itemRequest, itemList
                        .stream()
                        .filter(f -> f.getRequest().getId().equals(itemRequest.getId()))
                        .collect(Collectors.toList())))
                .getContent();
    }

    @Override
    public ItemRequestInfoDto getRequestById(Long requestId, Long userId) {
        log.debug("getRequestById");
        User user = userService.getUser(userId);
        Optional<ItemRequest> itemRequestO = itemRequestRepository.findById(requestId);
        if (!itemRequestO.isPresent()) {
            throw new NotFoundException("ItemRequest not found");
        }
        ItemRequest itemRequest = itemRequestO.get();
        List<Item> itemList = itemRepository.findAllByRequestIdList(List.of(itemRequest.getId()));

        return ItemRequestMapper.toItemRequestDtoInfo(itemRequest, itemList);
    }
}

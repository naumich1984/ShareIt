package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    @Value("${booking.statuses.worked}")
    private final List<BookingStatus> workedStatuses;


    @Override
    @Transactional
    public Item addItem(ItemDto itemDto, Long userId) {
        log.debug("addItem");
        userService.getUser(userId);

        return itemRepository.save(ItemMapper.toItem(itemDto, userId));
    }

    @Override
    @Transactional
    public Item updateItem(ItemDto itemDto, Long itemId, Long userId) {
        log.debug("updateItem");
        Optional<Item> itemO = itemRepository.findById(itemId);
        if (!itemO.isPresent()) {
            throw new NotFoundException("Item not found!");
        }
        if (!itemO.get().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User not found!");
        }
        Item itemUpdated = itemO.get();
        itemUpdated.setName(Optional.ofNullable(itemDto.getName()).orElse(itemUpdated.getName()));
        itemUpdated.setDescription(Optional.ofNullable(itemDto.getDescription()).orElse(itemUpdated.getDescription()));
        itemUpdated.setAvailable(Optional.ofNullable(itemDto.getAvailable()).orElse(itemUpdated.getAvailable()));

        return itemRepository.save(itemUpdated);
    }

    @Override
    public List<ItemInfoDto> getAllUserItems(Long userId) {
        log.debug("getAllUserItems");
        Booking lastBooking;
        Booking nextBooking;
        List<ItemInfoDto> itemInfoDtos = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwner(userId);
        for (Item item : items) {
            lastBooking = null;
            nextBooking = null;
            Pageable firstInPage = PageRequest.of(0, 1);
            Page<Booking> lastBookingPage = bookingRepository.findLastItemBooking(item.getId(), workedStatuses, firstInPage);
            Page<Booking> nextBookingPage = bookingRepository.findNextItemBooking(item.getId(), workedStatuses, firstInPage);
            if (!lastBookingPage.isEmpty()) {
                lastBooking = lastBookingPage.getContent().get(0);
            }
            if (!nextBookingPage.isEmpty()) {
                nextBooking = nextBookingPage.getContent().get(0);
            }

            itemInfoDtos.add(ItemMapper.toItemInfoDto(item, null, lastBooking, nextBooking));
        }

        return itemInfoDtos;
    }

    @Override
    public ItemInfoDto getItemWithBookings(Long itemId, Long userId) {
        log.debug("getItemWithBookings");
        Booking lastBooking = null;
        Booking nextBooking = null;
        Optional<Item> itemO = itemRepository.findById(itemId);
        if (!itemO.isPresent()) {
            throw new NotFoundException("Item not found!");
        }
        if (itemO.get().getOwner().getId().equals(userId)) {
            Pageable firstInPage = PageRequest.of(0, 1);
            Page<Booking> lastBookingPage = bookingRepository.findLastItemBooking(itemId, workedStatuses, firstInPage);
            Page<Booking> nextBookingPage = bookingRepository.findNextItemBooking(itemId, workedStatuses, firstInPage);
            if (!lastBookingPage.isEmpty()) {
                lastBooking = lastBookingPage.getContent().get(0);
            }
            if (!nextBookingPage.isEmpty()) {
                nextBooking = nextBookingPage.getContent().get(0);
            }
        }
        List<Comment> commentList = commentRepository.findAllCommentsByItemId(itemId);

        return ItemMapper.toItemInfoDto(itemO.get(), commentList, lastBooking, nextBooking);
    }

    @Override
    public Item getItem(Long itemId, Long userId) {
        log.debug("getItem");
        Optional<Item> itemO = itemRepository.findById(itemId);
        if (!itemO.isPresent()) {
            throw new NotFoundException("Item not found!");
        }

        return itemO.get();
    }

    @Override
    public List<Item> getItemsBySearch(String pattern, Long userId) {
        log.debug("getItemsBySearch");
        if (pattern.isBlank()) {
            return Collections.EMPTY_LIST;
        }

        return itemRepository.findAllBySearch(pattern, userId);
    }

    @Override
    public Comment addCommentItem(CommentDto comment, Long itemId, Long userId) {
        log.debug("addCommentItem");
        User user = userService.getUser(userId);

        Pageable firstInPage = PageRequest.of(0, 1);
        Page<Booking> bookingPage = bookingRepository.findLastFinishedBookingByItemIdAndUserId(itemId, userId,
                BookingStatus.APPROVED, firstInPage);
        if (bookingPage.isEmpty()) {
            throw new BadRequestException("Booking for comment not found");
        }
        Booking booking = bookingPage.getContent().get(0);

        return commentRepository.save(ItemMapper.toComment(comment, booking.getItem(), user));
    }
}

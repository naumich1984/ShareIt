package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CommonPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public Booking addBooking(BookingDto bookingDto, Long userId) {
        log.debug("addBooking");
        Optional<Item> itemO = Optional.ofNullable(itemService.getItem(bookingDto.getItemId(), userId));
        if (!itemO.isPresent()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemO.get();
        if (!item.getAvailable()) {
            throw new BadRequestException("Item not available");
        }
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("User is owner");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approveBooking(Long bookingId, Boolean approved, Long userId) {
        log.debug("approveBooking");
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        Optional<Booking> bookingO = Optional.ofNullable(bookingRepository.getByBookingIdAndOwnerItemId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found")));
        Booking booking = bookingO.get();
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Cannot change booking status: " + booking.getStatus().name());
        }
        Item item = itemService.getItem(booking.getItem().getId(), userId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item not found");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking getBooking(long bookingId, Long userId) {
        log.debug("getBooking");
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }

        return bookingRepository.getByBookingIdAndOwnerItemId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @Override
    @Transactional
    public List<Booking> getAllUserBooking(String state, Long userId, Integer from, Integer size) {
        log.debug("getAllUserBooking");
        CommonPageRequest pageable = new CommonPageRequest(from, size);
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        BookingStatus status = BookingStatus.from(state);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        Page<Booking> bookingsList = null;
        switch (status) {
            case ALL:
                bookingsList = bookingRepository.findAllBookingByUserId(userId, pageable);
                break;
            case WAITING:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case PAST:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatusPast(userId, BookingStatus.APPROVED, pageable);
                break;
            case CURRENT:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatusCurrent(userId, pageable);
                break;
            case FUTURE:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatusFuture(userId, pageable);
                break;
            case REJECTED:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }

        return bookingsList == null ? Collections.EMPTY_LIST : bookingsList.getContent();
    }

    @Override
    @Transactional
    public List<Booking> getAllBookingsUserItems(String state, Long userId, Integer from, Integer size) {
        log.debug("getAllBookingsUserItems");
        CommonPageRequest pageable = new CommonPageRequest(from, size);
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        BookingStatus status = BookingStatus.from(state);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        Page<Booking> bookingsList = null;
        switch (status) {
            case ALL:
                bookingsList = bookingRepository.findAllBookingByOwnerId(userId, pageable);
                break;
            case WAITING:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case PAST:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatusPast(userId, BookingStatus.APPROVED, pageable);
                break;
            case CURRENT:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatusCurrent(userId, pageable);
                break;
            case FUTURE:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatusFuture(userId, pageable);
                break;
            case REJECTED:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }

        return bookingsList == null ? Collections.EMPTY_LIST : bookingsList.getContent();
    }
}

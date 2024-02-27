package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
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
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        Optional<Booking> bookingO = Optional.ofNullable(getBooking(bookingId, userId));
        if (!bookingO.isPresent()) {
            throw new NotFoundException("Booking not found");
        }
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
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }

        return bookingRepository.getByBookingIdAndOwnerItemId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @Override
    @Transactional
    public List<Booking> getAllUserBooking(String state, Long userId) {
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        BookingStatus status = BookingStatus.from(state);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        List<Booking> bookingsList = new ArrayList<>();
        switch (status) {
            case ALL:
                bookingsList = bookingRepository.findAllBookingByUserId(userId);
                break;
            case WAITING:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatus(userId, BookingStatus.WAITING);
                break;
            case PAST:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatusPast(userId, BookingStatus.APPROVED);
                break;
            case CURRENT:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatusCurrent(userId);
                break;
            case FUTURE:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatusFuture(userId);
                break;
            case REJECTED:
                bookingsList = bookingRepository.findAllBookingByUserIdAndByStatus(userId, BookingStatus.REJECTED);
                break;
        }

        return bookingsList;
    }

    @Override
    @Transactional
    public List<Booking> getAllBookingsUserItems(String state, Long userId) {
        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        BookingStatus status = BookingStatus.from(state);
        if (status == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        List<Booking> bookingsList = new ArrayList<>();
        switch (status) {
            case ALL:
                bookingsList = bookingRepository.findAllBookingByOwnerId(userId);
                break;
            case WAITING:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatus(userId, BookingStatus.WAITING);
                break;
            case PAST:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatusPast(userId, BookingStatus.APPROVED);
                break;
            case CURRENT:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatusCurrent(userId);
                break;
            case FUTURE:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatusFuture(userId);
                break;
            case REJECTED:
                bookingsList = bookingRepository.findAllBookingByOwnerIdAndByStatus(userId, BookingStatus.REJECTED);
                break;
        }

        return bookingsList;
    }
}

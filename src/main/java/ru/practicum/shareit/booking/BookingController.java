package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping("/bookings")
    public ResponseEntity<BookingDtoInfo> addBooking(@RequestBody @Valid BookingDto bookingDto,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("POST /booking request");
        log.debug("X-Sharer-User-Id: {}", userId);
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

        return ResponseEntity.ok(BookingMapper.toBookingDto(bookingService.addBooking(booking, userId)));
    }

    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingDtoInfo> updateBooking(@PathVariable Long bookingId,
                                                        @RequestParam(value = "approved") @NotNull Boolean approved,
                                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("PATCH /bookings/{bookingId}?approved={approved}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("bookingId: {}", bookingId);

        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        Optional<Booking> bookingO = Optional.ofNullable(bookingService.getBooking(bookingId, userId));
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

        return ResponseEntity.ok(BookingMapper.toBookingDto(bookingService.approveBooking(booking, bookingId, userId)));
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingDtoInfo> getBooking(@PathVariable long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /bookings/{bookingId} request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("bookingId: {}", bookingId);

        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }

        return ResponseEntity.ok(BookingMapper.toBookingDto(bookingService.getBooking(bookingId, userId)));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDtoInfo>> getAllUserBooking(@RequestParam(value = "state", required = false) String state,
                                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /bookings?state={state}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("BookingStatus: {}", state);

        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        BookingStatus stateBooking = BookingStatus.from(state);
        if (stateBooking == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        return ResponseEntity.ok(bookingService.getAllUserBooking(stateBooking, userId)
                .stream()
                .map(booking -> BookingMapper.toBookingDto(booking))
                .collect(Collectors.toList()));
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<List<BookingDtoInfo>> getAllBookingsUserItems(@RequestParam(value = "state", required = false) String state,
                                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /bookings/owner?state={state}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("BookingStatus: {}", state);

        User user = userService.getUser(userId);
        if (!userId.equals(user.getId())) {
            throw new NotFoundException("User not found");
        }
        BookingStatus stateBooking = BookingStatus.from(state);
        if (stateBooking == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        return ResponseEntity.ok(bookingService.getAllBookingsUserItems(stateBooking, userId)
                .stream()
                .map(booking -> BookingMapper.toBookingDto(booking))
                .collect(Collectors.toList()));
    }
}

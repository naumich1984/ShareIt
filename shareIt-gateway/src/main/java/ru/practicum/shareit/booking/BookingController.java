package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping("/bookings")
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingDto bookingDto,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("POST /booking request");
        log.debug("X-Sharer-User-Id: {}", userId);

        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable Long bookingId,
                                                        @RequestParam(value = "approved") @NotNull Boolean approved,
                                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("PATCH /bookings/{bookingId}?approved={approved}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("bookingId: {}", bookingId);

        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.debug("GET /bookings/{bookingId} request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("bookingId: {}", bookingId);

        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping("/bookings")
    public ResponseEntity<Object> getAllUserBooking(@RequestParam(value = "state", required = false) String state,
                                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                                  @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.debug("GET /bookings?state={state}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("BookingStatus: {}", state);
        log.debug("from: {}", from);
        log.debug("size: {}", size);
        String stateCorrect = Optional.ofNullable(Optional.ofNullable(BookingStatus.from(state))
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state))).get().name();

        return bookingClient.getBookings(userId, stateCorrect, from, size);
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<Object> getAllBookingsUserItems(@RequestParam(value = "state", required = false) String state,
                                                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                                        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                                        @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.debug("GET /bookings/owner?state={state}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("BookingStatus: {}", state);
        log.debug("from: {}", from);
        log.debug("size: {}", size);
        String stateCorrect = Optional.ofNullable(Optional.ofNullable(BookingStatus.from(state))
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state))).get().name();

        return bookingClient.getOwnerBookings(userId, stateCorrect, from, size);
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/bookings")
    public ResponseEntity<BookingDtoInfo> addBooking(@RequestBody BookingDto bookingDto,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("POST /booking request");
        log.debug("X-Sharer-User-Id: {}", userId);

        return ResponseEntity.ok(BookingMapper.toBookingDtoInfo(bookingService.addBooking(bookingDto, userId)));
    }

    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingDtoInfo> updateBooking(@PathVariable Long bookingId,
                                                        @RequestParam(value = "approved") Boolean approved,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("PATCH /bookings/{bookingId}?approved={approved}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("bookingId: {}", bookingId);

        return ResponseEntity.ok(BookingMapper.toBookingDtoInfo(bookingService.approveBooking(bookingId, approved, userId)));
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingDtoInfo> getBooking(@PathVariable long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET /bookings/{bookingId} request");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("bookingId: {}", bookingId);

        return ResponseEntity.ok(BookingMapper.toBookingDtoInfo(bookingService.getBooking(bookingId, userId)));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDtoInfo>> getAllUserBooking(@RequestParam(value = "state", required = false) String state,
                                                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.debug("GET /bookings?state={state}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("BookingStatus: {}", state);
        log.debug("from: {}", from);
        log.debug("size: {}", size);

        return ResponseEntity.ok(bookingService.getAllUserBooking(state, userId, from, size)
                .stream()
                .map(booking -> BookingMapper.toBookingDtoInfo(booking))
                .collect(Collectors.toList()));
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<List<BookingDtoInfo>> getAllBookingsUserItems(@RequestParam(value = "state", required = false) String state,
                                                                        @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.debug("GET /bookings/owner?state={state}");
        log.debug("X-Sharer-User-Id: {}", userId);
        log.debug("BookingStatus: {}", state);
        log.debug("from: {}", from);
        log.debug("size: {}", size);

        return ResponseEntity.ok(bookingService.getAllBookingsUserItems(state, userId, from, size)
                .stream()
                .map(booking -> BookingMapper.toBookingDtoInfo(booking))
                .collect(Collectors.toList()));
    }
}

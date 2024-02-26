package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Booking addBooking(Booking booking, Long userId) {

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approveBooking(Booking booking, Long bookingId, Long id) {

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking getBooking(long bookingId, Long userId) {

        return bookingRepository.getByBookingIdAndOwnerItemId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @Override
    @Transactional
    public List<Booking> getAllUserBooking(BookingStatus status, Long userId) {
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
    public List<Booking> getAllBookingsUserItems(BookingStatus status, Long userId) {
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

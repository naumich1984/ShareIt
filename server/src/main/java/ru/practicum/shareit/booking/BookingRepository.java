package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.id = ?1 " +
            "and (b.booker.id = ?2 or b.item.owner.id = ?2) ")
    Optional<Booking> getByBookingIdAndOwnerItemId(Long bookingId, Long itemOwnerId);

    //SAME OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 " +
            " order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatus(Long userId, BookingStatus name, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc ")
    Page<Booking> findAllBookingByOwnerId(Long userId, Pageable pageable);

    //PAST OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < now() and b.status = ?2 " +
            "order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatusPast(Long userId, BookingStatus name, Pageable pageable);

    //CURRENT OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < now() and b.end > now() order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatusCurrent(Long userId, Pageable pageable);

    //FUTURE OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > now() order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatusFuture(Long userId, Pageable pageable);

    //SAME BOOKER
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatus(Long userId, BookingStatus name, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc ")
    Page<Booking> findAllBookingByUserId(Long userId, Pageable pageable);

    //CURRENT
    @Query("select b from Booking b where b.booker.id = ?1 and b.start < now() and b.end > now() order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatusCurrent(Long userId, Pageable pageable);

    //FUTURE
    @Query("select b from Booking b where b.booker.id = ?1 and b.start > now() order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatusFuture(Long userId, Pageable pageable);

    //PAST
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < now() and b.status = ?2 " +
            " order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatusPast(Long userId, BookingStatus name, Pageable pageable);

    //LAST NEXT
    @Query("select b from Booking b where b.start < now() and b.item.id = ?1 " +
            " and b.status in ?2 order by b.start DESC ")
    Page<Booking> findLastItemBooking(Long itemId, List<BookingStatus> workedStatuses, Pageable pageable);


    @Query("select b from Booking b where b.start >= now() and b.end > now() and b.item.id = ?1 " +
            " and b.status in ?2 order by b.start ")
    Page<Booking> findNextItemBooking(Long itemId, List<BookingStatus> workedStatuses, Pageable pageable);

    @Query("select b from Booking b where b.end <= now() and b.item.id = ?1 and b.booker.id = ?2 and b.status = ?3 ")
    Page<Booking> findLastFinishedBookingByItemIdAndUserId(Long itemId, Long userId, BookingStatus name, Pageable pageable);

}

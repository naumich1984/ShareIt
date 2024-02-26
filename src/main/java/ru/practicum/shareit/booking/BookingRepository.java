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
    List<Booking> findAllBookingByOwnerIdAndByStatus(Long userId, BookingStatus name);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc ")
    List<Booking> findAllBookingByOwnerId(Long userId);

    //PAST OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < now() and b.status = ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingByOwnerIdAndByStatusPast(Long userId, BookingStatus name);

    //CURRENT OWNER
    @Query("select b from Booking b where b.item.owner = ?1 and b.start < now() and b.end > now() order by b.start desc ")
    List<Booking> findAllBookingByOwnerIdAndByStatusCurrent(Long userId);

    //FUTURE OWNER
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > now() order by b.start desc ")
    List<Booking> findAllBookingByOwnerIdAndByStatusFuture(Long userId);

    //SAME BOOKER
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start desc ")
    List<Booking> findAllBookingByUserIdAndByStatus(Long userId, BookingStatus name);

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc ")
    List<Booking> findAllBookingByUserId(Long userId);

    //CURRENT
    @Query("select b from Booking b where b.booker.id = ?1 and b.start < now() and b.end > now() order by b.start desc ")
    List<Booking> findAllBookingByUserIdAndByStatusCurrent(Long userId);

    //FUTURE
    @Query("select b from Booking b where b.booker.id = ?1 and b.start > now() order by b.start desc ")
    List<Booking> findAllBookingByUserIdAndByStatusFuture(Long userId);

    //PAST
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < now() and b.status = ?2 " +
            " order by b.start desc ")
    List<Booking> findAllBookingByUserIdAndByStatusPast(Long userId, BookingStatus name);

    //LAST NEXT
    @Query("select b from Booking b where b.start < now() and b.item.id = ?1 " +
            " and b.status in ?2 order by b.start DESC ")
    Page<Booking> findLastItemBooking(Long itemId, List<BookingStatus> workedStatuses, Pageable pageable);


    @Query("select b from Booking b where b.start >= now() and b.end > now() and b.item.id = ?1 " +
            " and b.status in ?2 order by b.start ")
    Page<Booking> findNextItemBooking(Long itemId, List<BookingStatus> workedStatuses, Pageable pageable);
}

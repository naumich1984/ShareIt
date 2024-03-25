package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.id = :bookingId " +
            "and (b.booker.id = :itemOwnerId or b.item.owner.id = :itemOwnerId) ")
    Optional<Booking> getByBookingIdAndOwnerItemId(@Param("bookingId") Long bookingId,@Param("itemOwnerId") Long itemOwnerId);

    //SAME OWNER
    @Query("select b from Booking b where b.item.owner.id = :userId and b.status = :name " +
            " order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatus(@Param("userId") Long userId, @Param("name") BookingStatus name, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId order by b.start desc ")
    Page<Booking> findAllBookingByOwnerId(@Param("userId") Long userId, Pageable pageable);

    //PAST OWNER
    @Query("select b from Booking b where b.item.owner.id = :userId and b.end < now() and b.status = :name " +
            "order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatusPast(@Param("userId") Long userId, @Param("name") BookingStatus name, Pageable pageable);

    //CURRENT OWNER
    @Query("select b from Booking b where b.item.owner.id = :userId and b.start < now() and b.end > now() order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatusCurrent(@Param("userId") Long userId, Pageable pageable);

    //FUTURE OWNER
    @Query("select b from Booking b where b.item.owner.id = :userId and b.start > now() order by b.start desc ")
    Page<Booking> findAllBookingByOwnerIdAndByStatusFuture(@Param("userId") Long userId, Pageable pageable);

    //SAME BOOKER
    @Query("select b from Booking b where b.booker.id = :userId and b.status = :name order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatus(@Param("userId") Long userId, @Param("name") BookingStatus name, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :userId order by b.start desc ")
    Page<Booking> findAllBookingByUserId(@Param("userId") Long userId, Pageable pageable);

    //CURRENT
    @Query("select b from Booking b where b.booker.id = :userId and b.start < now() and b.end > now() order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatusCurrent(@Param("userId") Long userId, Pageable pageable);

    //FUTURE
    @Query("select b from Booking b where b.booker.id = :userId and b.start > now() order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatusFuture(@Param("userId") Long userId, Pageable pageable);

    //PAST
    @Query("select b from Booking b where b.booker.id = :userId and b.end < now() and b.status = :name " +
            " order by b.start desc ")
    Page<Booking> findAllBookingByUserIdAndByStatusPast(@Param("userId")Long userId, @Param("name") BookingStatus name, Pageable pageable);

    //LAST NEXT
    @Query("select b from Booking b where b.start < now() and b.item.id = :itemId " +
            " and b.status in :statuses order by b.start DESC ")
    Page<Booking> findLastItemBooking(@Param("itemId") Long itemId, @Param("statuses") List<BookingStatus> workedStatuses, Pageable pageable);


    @Query("select b from Booking b where b.start >= now() and b.end > now() and b.item.id = :itemId " +
            " and b.status in :statuses order by b.start ")
    Page<Booking> findNextItemBooking(@Param("itemId") Long itemId, @Param("statuses") List<BookingStatus> workedStatuses, Pageable pageable);

    @Query("select b from Booking b where b.end < now() and b.item.id = :itemId and b.booker.id = :userId and b.status = :name ")
    Page<Booking> findLastFinishedBookingByItemIdAndUserId(@Param("itemId") Long itemId, @Param("userId") Long userId,
                                                           @Param("name") BookingStatus name, Pageable pageable);

}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user;
    private User userOwner;
    private Item item;
    @Value("${booking.statuses.worked}")
    private List<BookingStatus> workedStatuses;

    private PageRequest pageable = PageRequest.of(0, 1);

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("user1")
                .email("user1@email.ru")
                .build();
        userRepository.save(user);

        userOwner = User.builder()
                .name("user2")
                .email("user2@email.ru")
                .build();
        userRepository.save(userOwner);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("descriptionR")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
        itemRequestRepository.save(itemRequest);

        item = Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .owner(userOwner)
                .request(itemRequest)
                .build();
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
        bookingRepository.save(booking);
    }

    @Test
    void getByBookingIdAndOwnerItemId() {
        Long bookingId = 1L;
        Long itemOwnerId = 2L;

        Optional<Booking> bookingO = bookingRepository.getByBookingIdAndOwnerItemId(bookingId, itemOwnerId);

        assertTrue(bookingO.isPresent());
        assertEquals(itemOwnerId, bookingO.get().getItem().getOwner().getId());
    }

    @Test
    void findAllBookingByOwnerIdAndByStatus() {
        long userId = 2L;

        Page<Booking> bookingPage = bookingRepository.findAllBookingByOwnerIdAndByStatus(userId, BookingStatus.WAITING, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByOwnerId() {
        long userId = 2L;

        Page<Booking> bookingPage = bookingRepository.findAllBookingByOwnerId(userId, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByOwnerIdAndByStatusPast() {
        long userId = 2L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByOwnerIdAndByStatusPast(userId, BookingStatus.APPROVED, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByOwnerIdAndByStatusCurrent() {
        long userId = 2L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByOwnerIdAndByStatusCurrent(userId, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByOwnerIdAndByStatusFuture() {
        long userId = 2L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByOwnerIdAndByStatusFuture(userId, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByUserIdAndByStatus() {
        long userId = 1L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByUserIdAndByStatus(userId, BookingStatus.APPROVED, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByUserId() {
        long userId = 1L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByUserId(userId, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByUserIdAndByStatusCurrent() {
        long userId = 1L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByUserIdAndByStatusCurrent(userId, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByUserIdAndByStatusFuture() {
        long userId = 1L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByUserIdAndByStatusFuture(userId, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findAllBookingByUserIdAndByStatusPast() {
        long userId = 1L;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findAllBookingByUserIdAndByStatusPast(userId, BookingStatus.APPROVED, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findLastItemBooking() {
        long userId = 1L;

        Page<Booking> bookingPage = bookingRepository.findLastItemBooking(userId, workedStatuses, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findNextItemBooking() {
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findNextItemBooking(item.getId(), workedStatuses, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }

    @Test
    void findLastFinishedBookingByItemIdAndUserId() {
        BookingStatus status = BookingStatus.APPROVED;
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(status)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookingPage = bookingRepository.findLastFinishedBookingByItemIdAndUserId(item.getId(), user.getId(),
                status, pageable);

        assertTrue(!bookingPage.isEmpty());
        assertEquals(1L, bookingPage.stream().count());
    }
}
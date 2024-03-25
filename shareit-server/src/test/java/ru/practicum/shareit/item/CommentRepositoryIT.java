package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryIT {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("user1")
                .email("user1@email.ru")
                .build();
        userRepository.save(user);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("descriptionR")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
        itemRequestRepository.save(itemRequest);

        Item item = Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        Comment comment = Comment.builder()
                .text("comment1")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    @Test
    void findAllCommentsByItemId() {
        Long expectedItemId = 1L;
        List<Comment> actualComments = commentRepository.findAllCommentsByItemId(expectedItemId);

        assertTrue(!actualComments.isEmpty());
        assertEquals(1, actualComments.size());
    }
}
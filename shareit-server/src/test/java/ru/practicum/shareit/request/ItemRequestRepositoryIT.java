package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("user1")
                .email("user1@email.ru")
                .build();
        userRepository.save(user);

        User user2 = User.builder()
                .name("user2")
                .email("user2@email.ru")
                .build();
        userRepository.save(user2);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("descriptionR1")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
        itemRequestRepository.save(itemRequest);

        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("descriptionR2")
                .created(LocalDateTime.now())
                .requestor(user2)
                .build();
        itemRequestRepository.save(itemRequest2);

        Item item = Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        itemRepository.save(item);
    }

    @Test
    void findAllRequestWithItemsByUserId() {
        long userId = 1L;
        List<ItemRequest> actualItemRequests = itemRequestRepository.findAllRequestWithItemsByUserId(userId);

        assertTrue(!actualItemRequests.isEmpty());
        assertEquals(1, actualItemRequests.size());
    }

    @Test
    void findAllRequestWithItemsByNotUserId() {
        long userId = 2L;
        Page<ItemRequest> actualItemRequests = itemRequestRepository.findAllRequestWithItemsByNotUserId(userId, PageRequest.of(0,1));

        assertTrue(!actualItemRequests.isEmpty());
        assertEquals(1, actualItemRequests.stream().count());
    }
}
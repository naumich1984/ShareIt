package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
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
class ItemRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void addItems() {
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
    }

    @Test
    void findAllByRequestIdList() {
        List<Long> requestIds = List.of(1L);
        List<Item> actualItems = itemRepository.findAllByRequestIdList(requestIds);

        assertTrue(!actualItems.isEmpty());
        assertEquals(1, actualItems.size());
    }

    @Test
    void findAllByOwner() {
        Long userId = 1L;
        List<Item> actualItems = itemRepository.findAllByOwner(userId);

        assertTrue(!actualItems.isEmpty());
        assertEquals(1, actualItems.size());
    }

    @Test
    void findAllBySearch() {
        Long userId = 1L;
        List<Item> actualItems = itemRepository.findAllBySearch("description1", userId);

        assertTrue(!actualItems.isEmpty());
        assertEquals(1, actualItems.size());
    }

    @AfterEach
    public void deleteItems() {
        itemRepository.deleteAll();
    }
}
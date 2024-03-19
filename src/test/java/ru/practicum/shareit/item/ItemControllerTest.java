package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;
    private ItemDto expectedDtoItem;
    private Item expectedItem;
    private ItemInfoDto expectedInfoDtoItem;
    private User expectedUser;
    private Long expectedItemId;
    private Long expectedUserId;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        expectedItemId = 1L;
        expectedUserId = 1L;
        expectedUser = new User(expectedUserId, "userName1", "user1@email.ru");
        expectedDtoItem  = new ItemDto(expectedItemId, "itemName1", "itemDescription1", true, null);
        expectedItem = ItemMapper.toItem(expectedDtoItem, expectedUserId);
        expectedItem.setId(expectedItemId);
        expectedInfoDtoItem = ItemMapper.toItemInfoDto(expectedItem, Collections.emptyList(), null, null);
    }

    @Test
    void addCommentItem_whenInvoked_thenResponseStatusOkWithCommentInBody() {
        CommentDto commentDto = new CommentDto(null, "commentText", expectedUser.getName(), LocalDateTime.now());
        Comment comment = ItemMapper.toComment(commentDto, expectedItem, expectedUser);
        Mockito.when(itemService.addCommentItem(commentDto, expectedItemId, expectedUserId)).thenReturn(comment);

        ResponseEntity<CommentDto> response = itemController.addCommentItem(commentDto, expectedItemId, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentDto.getText(), response.getBody().getText());
        assertEquals(commentDto.getAuthorName(), response.getBody().getAuthorName());
    }

    @Test
    void addItem_whenInvoked_thenResponseStatusOkWithItemInBody() {
        Mockito.when(itemService.addItem(expectedDtoItem, expectedUserId)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.addItem(expectedDtoItem, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtoItem.toString(), response.getBody().toString());
    }

    @Test
    void updateItem_whenInvoked_thenResponseStatusOkWithItemInBody() {
        Mockito.when(itemService.updateItem(expectedDtoItem, expectedItemId, expectedUserId)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.updateItem(expectedDtoItem, expectedItemId, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDtoItem.toString(), response.getBody().toString());
    }

    @Test
    void getItem() {
        Mockito.when(itemService.getItemWithBookings(expectedItemId, expectedUserId)).thenReturn(expectedInfoDtoItem);

        ResponseEntity<ItemInfoDto> response = itemController.getItem(expectedItemId, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedInfoDtoItem.toString(), response.getBody().toString());
    }

    @Test
    void getAllUserItems() {
        List<ItemInfoDto> expectedItemsList = List.of(expectedInfoDtoItem);
        Mockito.when(itemService.getAllUserItems(expectedUserId)).thenReturn(expectedItemsList);

        ResponseEntity<List<ItemInfoDto>> response = itemController.getAllUserItems(expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemsList.get(0).toString(), response.getBody().get(0).toString());
        assertEquals(expectedItemsList.size(), response.getBody().size());
    }

    @Test
    void getItemsBySearch() {
        String pattern = "pattern";
        List<Item> expectedItemsList = List.of(expectedItem);
        Mockito.when(itemService.getItemsBySearch(pattern, expectedUserId)).thenReturn(expectedItemsList);

        ResponseEntity<List<ItemDto>> response = itemController.getItemsBySearch(pattern, expectedUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemsList
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList())
                .get(0).toString(), response.getBody().get(0).toString());
    }
}
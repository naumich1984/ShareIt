package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ItemDto expectedDtoItem;
    private Item expectedItem;
    private ItemInfoDto expectedInfoDtoItem;
    private User expectedUser;
    private Long expectedItemId;
    private Long expectedUserId;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        expectedItemId = 1L;
        expectedUserId = 1L;
        expectedUser = new User(expectedUserId, "userName1", "user1@email.ru");
        expectedItem = new Item(expectedItemId, "itemName1", "itemDescription1", true, expectedUser, null);
        expectedDtoItem  = ItemMapper.toItemDto(expectedItem);
        expectedInfoDtoItem = ItemMapper.toItemInfoDto(expectedItem, Collections.emptyList(), null, null);
        comment = new Comment(1L, "commentText", expectedItem, expectedUser, LocalDateTime.now());
        commentDto = ItemMapper.toCommentDto(comment);
    }

    @SneakyThrows
    @Test
    void addCommentItem_whenValid_thenReturnedOk() {
        when(itemService.addCommentItem(commentDto, expectedItemId, expectedUserId)).thenReturn(comment);

        String result = mockMvc.perform(post("/items/{itemId}/comment", expectedItemId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
        verify(itemService).addCommentItem(commentDto, expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addCommentItem_whenCommentNotValid_thenExceptionThrown() {
        commentDto.setText(null);

        mockMvc.perform(post("/items/{itemId}/comment", expectedItemId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addCommentItem(commentDto, expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addCommentItem_whenUserNotValid_thenExceptionThrown() {
        mockMvc.perform(post("/items/{itemId}/comment", expectedItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addCommentItem(commentDto, expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addCommentItem_whenUserNotFound_thenExceptionThrown() {
        when(itemService.addCommentItem(commentDto, expectedItemId, expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(post("/items/{itemId}/comment", expectedItemId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());

        verify(itemService).addCommentItem(commentDto, expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addCommentItem_whenBookingNotFound_thenExceptionThrown() {
        when(itemService.addCommentItem(commentDto, expectedItemId, expectedUserId))
                .thenThrow(new BadRequestException("Booking not found!"));

        mockMvc.perform(post("/items/{itemId}/comment", expectedItemId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService).addCommentItem(commentDto, expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addItem_whenItemIsValid_thenReturnedOk() {
        when(itemService.addItem(expectedDtoItem, expectedUserId)).thenReturn(expectedItem);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDtoItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedDtoItem), result);
        verify(itemService).addItem(expectedDtoItem, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addItem_whenItemIsNotValid_thenReturnedBadStatus() {
        expectedDtoItem.setName(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDtoItem)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(expectedDtoItem, expectedUserId);
    }

    @SneakyThrows
    @Test
    void addItem_whenUserIdIsNotValid_thenReturnedBadStatus() {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDtoItem)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(expectedDtoItem, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemIsValid_thenReturnedOk() {
        when(itemService.updateItem(expectedDtoItem, expectedItemId, expectedUserId)).thenReturn(expectedItem);

        String result = mockMvc.perform(patch("/items/{itemID}", expectedItemId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDtoItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedDtoItem), result);
        verify(itemService).updateItem(expectedDtoItem, expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemIsNotFound_thenExceptionThrown() {
        when(itemService.updateItem(expectedDtoItem, expectedItemId, expectedUserId)).thenThrow(new NotFoundException("Item not found!"));

        mockMvc.perform(patch("/items/{itemID}", expectedItemId)
                        .header("X-Sharer-User-Id", expectedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDtoItem)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void updateItem_whenUserIsNotFound_thenExceptionThrown() {
        mockMvc.perform(patch("/items/{itemID}", expectedItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDtoItem)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).updateItem(expectedDtoItem, expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getItem_whenItemIsValid_thenReturnedOk() {
        when(itemService.getItemWithBookings(expectedItemId, expectedUserId)).thenReturn(expectedInfoDtoItem);

        String result = mockMvc.perform(get("/items/{itemID}", expectedItemId)
                .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getItemWithBookings(expectedItemId, expectedUserId);
        assertEquals(objectMapper.writeValueAsString(expectedInfoDtoItem), result);
    }

    @SneakyThrows
    @Test
    void getItem_whenItemIsNotFound_thenExceptionThrown() {
        when(itemService.getItemWithBookings(expectedItemId, expectedUserId)).thenThrow(new NotFoundException("Item not found!"));

        mockMvc.perform(get("/items/{itemID}", expectedItemId)
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getItem_whenUserIsNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/items/{itemID}", expectedItemId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemWithBookings(expectedItemId, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getAllUserItems_whenUserIdIsValid_thenReturnedOk() {
        List<ItemInfoDto> itemInfoDtoList = List.of(expectedInfoDtoItem);
        when(itemService.getAllUserItems(expectedUserId)).thenReturn(itemInfoDtoList);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemInfoDtoList), result);
        verify(itemService).getAllUserItems(expectedUserId);
    }

    @SneakyThrows
    @Test
    void getAllUserItems_whenUserIdIsNotFound_thenExceptionThrown() {
        when(itemService.getAllUserItems(expectedUserId)).thenThrow(new NotFoundException("User not found!"));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isNotFound());

        verify(itemService).getAllUserItems(expectedUserId);
    }

    @SneakyThrows
    @Test
    void getAllUserItems_whenUserIdIsNotValid_thenExceptionThrown() {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllUserItems(expectedUserId);
    }

    @SneakyThrows
    @Test
    void getItemsBySearch_whenValid_thenReturnedOk() {
        String pattern = "pattern";
        when(itemService.getItemsBySearch(pattern, expectedUserId)).thenReturn(List.of(expectedItem));

        String result = mockMvc.perform(get("/items/search")
                        .param("text", pattern)
                        .header("X-Sharer-User-Id", expectedUserId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(expectedDtoItem)), result);
        verify(itemService).getItemsBySearch(pattern, expectedUserId);
    }

    @SneakyThrows
    @Test
    void getItemsBySearch_whenUserIdNotValid_thenExceptionThrown() {
        String pattern = "pattern";

        mockMvc.perform(get("/items/search")
                        .param("text", pattern))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemsBySearch(pattern, expectedUserId);
    }
}
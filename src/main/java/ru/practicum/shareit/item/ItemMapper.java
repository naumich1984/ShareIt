package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto, Long userId) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                new User(userId, userId.toString(), userId.toString()),
                null
        );
    }

    public static ItemInfoDto toItemInfoDto(Item item, List<Comment> comments, Booking lastBooking, Booking nextBooking) {
        return new ItemInfoDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                comments == null ? Collections.EMPTY_LIST : comments
                        .stream()
                        .map(comment -> ItemCommentToCommentDto(comment))
                        .collect(Collectors.toList()),
                lastBooking == null ? null : new ItemInfoDto.BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()),
                nextBooking == null ? null : new ItemInfoDto.BookingDto(nextBooking.getId(), nextBooking.getBooker().getId())
        );
    }

    public static ItemInfoDto.CommentDto ItemCommentToCommentDto(Comment comment) {

        return new ItemInfoDto.CommentDto(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {

        return new Comment(null,
                commentDto.getText(),
                new Item(item.getId(), null, null, null, null, null),
                user,
                LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {

        return new CommentDto(comment.getId(), comment.getText(),comment.getAuthor().getName(), comment.getCreated());
    }

}

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
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                new User(userId, userId.toString(), userId.toString()),
                itemDto.getRequestId() != null ? ItemRequest.builder().id(itemDto.getRequestId()).build() : null
        );
    }

    public static ItemInfoDto toItemInfoDto(Item item, List<Comment> comments, Booking lastBooking, Booking nextBooking) {
        return new ItemInfoDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                comments == null ? Collections.EMPTY_LIST : comments
                        .stream()
                        .map(comment -> new ItemInfoDto.CommentDto(comment.getId(), comment.getText(),
                                comment.getAuthor().getName(), comment.getCreated()))
                        .collect(Collectors.toList()),
                lastBooking == null ? null : new ItemInfoDto.BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()),
                nextBooking == null ? null : new ItemInfoDto.BookingDto(nextBooking.getId(), nextBooking.getBooker().getId())
        );
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {

        return  Comment.builder()
                .text(commentDto.getText())
                .item(Item.builder().id(item.getId()).build())
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {

        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

}

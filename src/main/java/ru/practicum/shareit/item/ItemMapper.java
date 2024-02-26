package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

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
                comments == null ? null : comments
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

}

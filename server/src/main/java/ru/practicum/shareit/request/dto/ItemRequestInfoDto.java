package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestInfoDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestInfoDto {
    private Long id;
    @NotBlank(message = "description should not be blank")
    @Size(max = 32000, message = "description length should be less 32000 symbols")
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
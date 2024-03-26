package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"description"})
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}

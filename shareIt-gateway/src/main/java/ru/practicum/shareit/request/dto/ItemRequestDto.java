package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"description"})
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "description should not be blank")
    @Size(max = 32000, message = "description length should be less 32000 symbols")
    private String description;
    private LocalDateTime created;
}

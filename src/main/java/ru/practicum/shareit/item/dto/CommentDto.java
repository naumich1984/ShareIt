package ru.practicum.shareit.item.dto;

import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentDto {

    Long id;
    @NotBlank
    String text;
    String authorName;
    LocalDateTime created;
}

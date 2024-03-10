package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {
    private Long id;
    @NotBlank(message = "name should not be blank")
    private String name;
    @NotBlank(message = "description should not be blank")
    @Size(max = 32000, message = "description length should be less 32000 symbols")
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(id, itemDto.id) && Objects.equals(name, itemDto.name) && Objects.equals(description, itemDto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
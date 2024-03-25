package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "email"})
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
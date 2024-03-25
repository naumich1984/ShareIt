package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "email"})
public class UserDto {
    private Long id;
    @NotBlank(message = "name should not be blank")
    private String name;
    @NotBlank(message = "email should not be blank")
    @Email(message = "email should exists @ symbol")
    private String email;
}
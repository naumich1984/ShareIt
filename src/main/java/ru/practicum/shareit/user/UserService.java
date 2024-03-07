package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);

    User updateUser(UserDto userDto, Long userId);

    int deleteUser(Long userId);

    User getUser(Long userId);

    List<UserDto> getAllUsers();
}

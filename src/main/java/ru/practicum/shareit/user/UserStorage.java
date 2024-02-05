package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    int deleteUser(Long userId);

    User getUser(Long userId);

    List<User> getAllUsers();
}

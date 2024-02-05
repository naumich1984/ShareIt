package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("UserInMemoryStorage")
@RequiredArgsConstructor
@Slf4j
public class UserInMemoryStorage implements UserStorage {

    private Map<Long, User> userRepository = new HashMap<>();
    private Long userSequence = 1L;

    @Override
    public User addUser(User user) {
        log.debug("Add user to storage");
        Long userId = userRepository.keySet().stream().filter(k -> k.equals(user.getId())).findFirst().orElse(null);
        if (userId != null || checkEmail(user.getEmail())) {
            log.error("adding user already exists!");
            throw new ValidationException("User validation error");
        }
        user.setId(userSequence);
        userRepository.put(userSequence++, user);

        return user;
    }

    private boolean checkEmail(String email) {
        log.debug("checkEmail");

        return userRepository.values().stream().filter(f -> email.equals(f.getEmail())).findFirst().isPresent();
    }

    @Override
    public User updateUser(User user) {
        log.debug("Update user in storage");
        Long updatedUserId = userRepository.keySet()
                .stream().filter(k -> k.equals(user.getId())).findFirst().orElse(null);
        if (updatedUserId == null) {
            log.error("updating user not exists!");
            throw new ValidationException("updating user not exists!");
        }
        String emailToUpdate = Optional.ofNullable(user.getEmail()).orElse(null);
        User updatedUser = userRepository.get(updatedUserId);
        boolean emailIsUse = userRepository.values()
                .stream().filter(v -> v.getEmail().equals(emailToUpdate)).findFirst().isPresent();
        if (!updatedUser.getEmail().equals(emailToUpdate) && emailIsUse) {
            log.error("email already use!");
            throw new ValidationException("email already use!");
        }
        updatedUser.setName(Optional.ofNullable(user.getName()).orElse(updatedUser.getName()));
        updatedUser.setEmail(Optional.ofNullable(emailToUpdate).orElse(updatedUser.getEmail()));
        userRepository.put(updatedUserId, updatedUser);

        return updatedUser;
    }

    @Override
    public int deleteUser(Long userId) {
        log.debug("Delete user from storage");
        userRepository.remove(userId);

        return 0;
    }

    @Override
    public User getUser(Long userId) {
        log.debug("Get user from storage");
        User user = userRepository.get(userId);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Get all users from storage");

        return userRepository.values().stream().collect(Collectors.toList());
    }
}

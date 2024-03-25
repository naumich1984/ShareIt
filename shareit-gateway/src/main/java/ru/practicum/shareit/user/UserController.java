package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping("/users")
    public ResponseEntity<Object> saveNewUser(@RequestBody @Valid UserDto userDto) {
        log.debug("POST /users request");

        return userClient.saveUser(userDto);
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.debug("PATCH /users request");
        log.debug("userId: {}", userId);

        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.debug("GET /users/{id} request");
        log.debug("id: {}", userId);

        return userClient.getUser(userId);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.debug("DELETE /users/{userId}");
        log.debug("userId: {}", userId);

        return userClient.deleteUser(userId);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        log.debug("GET /users request");

        return userClient.getAllUsers();
    }
}

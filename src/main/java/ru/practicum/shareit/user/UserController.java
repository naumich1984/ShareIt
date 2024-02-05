package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody @Valid UserDto userDto) {
        log.debug("POST /users request");

        return ResponseEntity.ok(userService.addUser(userDto));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.debug("PATCH /users request");
        log.debug("userId: {}", userId);

        return ResponseEntity.ok(userService.updateUser(userDto, userId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Long userId) {
        log.debug("DELETE /users/{userId}");
        log.debug("userId: {}", userId);

        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("GET /users request");

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUser(@PathVariable long userId) {
        log.debug("GET /users/{id} request");
        log.debug("id: {}", userId);

        return ResponseEntity.ok(userService.getUser(userId));
    }
}

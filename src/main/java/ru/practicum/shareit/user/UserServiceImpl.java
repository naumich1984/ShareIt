package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User addUser(UserDto userDto) {

        return userStorage.addUser(UserMapper.toUser(userDto));
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);

        return  userStorage.updateUser(user);
    }

    @Override
    public int deleteUser(Long userId) {

        return userStorage.deleteUser(userId);
    }

    @Override
    public User getUser(Long userId) {

        return userStorage.getUser(userId);
    }

    @Override
    public List<User> getAllUsers() {

        return userStorage.getAllUsers();
    }
}

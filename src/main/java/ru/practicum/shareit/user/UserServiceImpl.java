package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User saveUser(UserDto userDto) {

        return userRepository.save(UserMapper.toUser(userDto));
    }

    @Transactional
    @Override
    public User updateUser(UserDto userDto, Long userId) {
        Optional<User> userO = userRepository.findById(userId);
        if (!userO.isPresent()) {
            throw new NotFoundException("User not found!");
        }
        User userUpdated = userO.get();
        userUpdated.setName(Optional.ofNullable(userDto.getName()).orElse(userUpdated.getName()));
        userUpdated.setEmail(Optional.ofNullable(userDto.getEmail()).orElse(userUpdated.getEmail()));

        return userRepository.save(userUpdated);
    }

    @Transactional
    @Override
    public int deleteUser(Long userId) {
        userRepository.deleteById(userId);

        return 0;
    }

    @Override
    public User getUser(Long userId) {
        Optional<User> userO = userRepository.findById(userId);
        if (!userO.isPresent()) {
             throw new NotFoundException("User not found!");
        }

        return userO.get();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users;
    }
}

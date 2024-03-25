package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User saveUser(UserDto userDto) {
        log.debug("saveUser");

        return userRepository.save(UserMapper.toUser(userDto));
    }

    @Transactional
    @Override
    public User updateUser(UserDto userDto, Long userId) {
        log.debug("updateUser");
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
        log.debug("deleteUser");
        userRepository.deleteById(userId);

        return 0;
    }

    @Override
    public User getUser(Long userId) {
        log.debug("getUser");
        Optional<User> userO = userRepository.findById(userId);
        if (!userO.isPresent()) {
             throw new NotFoundException("User not found!");
        }

        return userO.get();
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("getAllUsers");
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }
}

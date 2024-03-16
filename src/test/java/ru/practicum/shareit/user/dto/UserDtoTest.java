package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @SneakyThrows
    @Test
    public void testSerializeUserDto() {
        UserDto userDto = new UserDto(1L, "userName", "user1@email.ru");

        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).hasJsonPathStringValue("name");
        assertThat(result).extractingJsonPathStringValue("name").isEqualTo("userName");
        assertThat(result).extractingJsonPathStringValue("email").isEqualTo("user1@email.ru");
    }

    @SneakyThrows
    @Test
    void deserializeFromCorrectFormatUserDto() {
        String userDtoJson = "{\n" +
                "    \"name\": \"user\",\n" +
                "    \"email\": \"user@user.com\"\n" +
                "}";

        UserDto userDto = jacksonTester.parseObject(userDtoJson);

        assertThat(userDto.getEmail()).isEqualTo("user@user.com");
        assertThat(userDto.getName()).isEqualTo("user");
    }
}
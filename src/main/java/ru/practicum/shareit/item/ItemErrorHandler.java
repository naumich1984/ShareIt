package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ItemErrorHandler {

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.CONFLICT, reason = "failed validation")
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.debug("Ошибка валидации:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found!")
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.debug("Ошибка:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "InternalServerException")
    public Map<String, String> handleInternalServerException(final NullPointerException e) {
        log.debug("Ошибка сервера:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка сервера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "BadRequestException")
    public Map<String, String> handleBadRequestException(final HttpClientErrorException e) {
        log.debug("Ошибка сервера:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка сервера", e.getMessage());
    }

}

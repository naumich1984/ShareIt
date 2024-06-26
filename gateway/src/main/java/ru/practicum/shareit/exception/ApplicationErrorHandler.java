package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApplicationErrorHandler {

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "BadRequestException")
    public Map<String, String> handleBadRequestException(final BadRequestException e) {
        log.debug("Ошибка сервера:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка сервера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.debug("Ошибка сервера:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out, true, Charset.defaultCharset()));
        String stackTrace = out.toString(Charset.defaultCharset());

        return new ErrorResponse(e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "NotFoundException")
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.debug("Ошибка:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "ConstraintViolationException")
    public Map<String, String> handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("Ошибка сервера:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка сервера", e.getMessage());
    }
}

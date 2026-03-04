package ru.job4j.site.controller.rest;

import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.job4j.site.exception.AppException;
import ru.job4j.site.exception.IdNotFoundException;
import ru.job4j.site.exception.UnauthorizedException;
import ru.job4j.site.exception.UnknownException;

import javax.xml.bind.ValidationException;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorMessage> unauthorizedException(UnauthorizedException exception) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(IdNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> validationException(ValidationException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(UnknownException.class)
    public ResponseEntity<ErrorMessage> unknownException(UnknownException exception) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorMessage(exception.getMessage()));
    }
}

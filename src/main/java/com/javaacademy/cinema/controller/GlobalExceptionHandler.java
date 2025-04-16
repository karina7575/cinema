package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.exception.AlreadyBoughtTicketException;
import com.javaacademy.cinema.exception.AlreadyExistsFilmException;
import com.javaacademy.cinema.exception.AlreadyExistsSessionException;
import com.javaacademy.cinema.exception.InvalidAuthorization;
import com.javaacademy.cinema.exception.NotFoundMovieException;
import com.javaacademy.cinema.exception.NotFoundPlaceException;
import com.javaacademy.cinema.exception.NotFoundSessionException;
import com.javaacademy.cinema.exception.NotFoundTicketException;
import com.javaacademy.cinema.exception.OccupiedPlaceException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAuthorization.class)
    public ResponseEntity<?> handle403Exception(InvalidAuthorization e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            NotFoundMovieException.class,
            NotFoundSessionException.class,
            NotFoundTicketException.class,
            NotFoundPlaceException.class
    })
    public ResponseEntity<?> handle404Exception(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            AlreadyBoughtTicketException.class,
            AlreadyExistsFilmException.class,
            AlreadyExistsSessionException.class,
            OccupiedPlaceException.class
    })
    public ResponseEntity<?> handle409Exception(RuntimeException e) {
        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}

package com.javaacademy.cinema.controller.admin;

import com.javaacademy.cinema.controller.Validator;
import com.javaacademy.cinema.dto.CreateSessionDto;
import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("cinema/session")
@Tag(
        name = "Контроллер для работы с сеансами",
        description = "Содержит команды для совершения действий с сеансами"
)
public class SessionController {
    private final SessionService sessionService;
    private final Validator validator;

    @Operation(summary = "Создание сеанса",
        description = "Создание сеанса с его номером, временем, фильмом и ценой")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Успешное создание сеанса.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketDto.class))),
            @ApiResponse(responseCode = "404", description = "Фильм не существует.",
                    content = @Content(mediaType = "plain/text")),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен.",
                    content = @Content(mediaType = "plain/text"))
    })
    @CacheEvict(value = "sessions", allEntries = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<TicketDto> saveSession(@RequestHeader("token") String token,
                                       @RequestHeader("password") String password,
                                       @RequestBody CreateSessionDto sessionDto) {
        validator.checkAdmin(token, password);
        return sessionService.saveSession(sessionDto);
    }
}

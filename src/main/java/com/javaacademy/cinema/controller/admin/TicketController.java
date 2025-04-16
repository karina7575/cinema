package com.javaacademy.cinema.controller.admin;

import com.javaacademy.cinema.controller.Validator;
import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("cinema/ticket")
@Tag(
        name = "Контроллер для работы с билетами",
        description = "Содержит команды для совершения действий с билетами"
)
public class TicketController {
    private final TicketService ticketService;
    private final Validator validator;

    @Operation(summary = "Получение списка купленных билетов",
        description = "Получение списка купленных билетов по номеру сеанса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное получение списка купленных билетов на сеанс.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketDto.class))),
            @ApiResponse(responseCode = "404", description = "Сеанса не существует.",
                    content = @Content(mediaType = "plain/text"))
    })
    @Cacheable("buyTickets")
    @GetMapping("/saled/{id}")
    public List<TicketDto> findBuyTickets(@RequestHeader("token") String token,
                                          @RequestHeader("password") String password,
                                          @PathVariable Integer id) {
        validator.checkAdmin(token, password);
        return ticketService.findAllBuyTicket(id);
    }
}

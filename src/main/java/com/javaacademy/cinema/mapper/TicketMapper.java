package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketMapper {
    public Ticket toEntity(TicketDto ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setId(ticketDto.getId());
        ticket.setSession(ticketDto.getSession());
        ticket.setPlace(ticketDto.getPlace());
        ticket.setIsBuy(ticketDto.getIsBuy());
        return ticket;
    }

    public TicketDto toDto(Ticket ticket) {
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(ticket.getId());
        ticketDto.setSession(ticket.getSession());
        ticketDto.setPlace(ticket.getPlace());
        ticketDto.setIsBuy(ticket.getIsBuy());
        return ticketDto;
    }

    public List<TicketDto> toDtos(List<Ticket> tickets) {
        return tickets.stream().map(this::toDto).toList();
    }
}

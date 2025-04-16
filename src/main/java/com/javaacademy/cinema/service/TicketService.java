package com.javaacademy.cinema.service;

import com.javaacademy.cinema.dto.BookingDto;
import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.dto.TicketResponse;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.OccupiedPlaceException;
import com.javaacademy.cinema.mapper.TicketMapper;
import com.javaacademy.cinema.repository.PlaceRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;

    /**
     Покупка билета
     */
    public TicketResponse buyTicket(BookingDto bookingDto) {
        sessionRepository.checkPresenceSession(bookingDto.getSessionId());
        Session session = sessionRepository.selectById(bookingDto.getSessionId()).get();
        Place place = placeRepository.selectByNumber(bookingDto.getPlaceNumber()).get();
        Optional<Integer> ticketId = ticketRepository.findByNumber(bookingDto);
        log.info("Выполнен поиск id билета по сеансу и номеру места.");
        ticketRepository.buy(ticketId.get());
        Movie movie = session.getMovie();

        TicketResponse ticketResponse = new TicketResponse(ticketId.get(), place.getNumber(),
                movie.getTitle(), session.getLocalDateTime());
        log.info("Получен билет для посетителя");
        return ticketResponse;
    }

    /**
     Поиск всех купленных билетов на сеанс
     */
    public List<TicketDto> findAllBuyTicket(Integer id) {
        List<Ticket> ticketList = ticketRepository.selectBuyTickets(id);
        log.info("Получены все купленные билеты на сеанс.\n");
        return ticketMapper.toDtos(ticketList);
    }

    /**
     Показать свободные места на сеанс
     */
    public List<String> findFreePlaces(Integer id) {
        List<Ticket> tickets = ticketRepository.selectNotBuyTickets(id);
        List<String> numbers = tickets.stream()
                .map(e -> e.getPlace().getNumber())
                .toList();
        log.info("Получены номера свободных мест на сеанс.\n");
        return numbers;
    }

    private void checkOccupiedPlaces(BookingDto bookingDto) {
        List<String> freePlaces = findFreePlaces(bookingDto.getSessionId());
        List<String> occupiedPlace = freePlaces.stream()
                .filter(e -> Objects.equals(e, bookingDto.getPlaceNumber()))
                .toList();
        if (!occupiedPlace.isEmpty()) {
            throw new OccupiedPlaceException("Место уже занято");
        }
    }

}

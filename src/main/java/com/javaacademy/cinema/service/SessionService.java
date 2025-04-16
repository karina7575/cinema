package com.javaacademy.cinema.service;

import com.javaacademy.cinema.dto.CreateSessionDto;
import com.javaacademy.cinema.dto.SessionResponse;
import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.exception.NotFoundMovieException;
import com.javaacademy.cinema.mapper.SessionMapper;
import com.javaacademy.cinema.mapper.TicketMapper;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.PlaceRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;
    private final SessionMapper sessionMapper;
    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    /**
     Создание сеанса
     */
    public List<TicketDto> saveSession(CreateSessionDto sessionDto) {
        Optional<Movie> movie = movieRepository.selectMovieById(sessionDto.getMovieId());
        if (movie.isEmpty()) {
            throw new NotFoundMovieException("Фильм с таким ID не существует.");
        }
        Session sessionToData = sessionMapper.toEntity(sessionDto);
        sessionToData.setMovie(movie.get());
        Session session = sessionRepository.save(sessionToData);

        log.info("Создан сеанс № {}.\n", session.getId());
        List<Place> allPlace = placeRepository.selectAll();
        List<TicketDto> allTicket = allPlace.stream()
                .map(e -> TicketDto.builder()
                    .place(e)
                    .isBuy(false)
                    .session(session)
                    .build())
                .toList();
        allTicket.stream().forEach(e -> ticketRepository.save(ticketMapper.toEntity(e)));
        log.info("Созданы не проданные билеты на сеанс {}.\n", session.getId());
        log.info("Созданные места: {}", allTicket.size());
        return allTicket;
    }

    /**
     Показать все сеансы
     */
    public List<SessionResponse> findAll() {
        List<Session> sessions = sessionRepository.findAll();
        List<SessionResponse> sessionResponses = sessionMapper.toSessions(sessions);
        log.info("Получен список сеансов\n");
        return sessionResponses;
    }
}

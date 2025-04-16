package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.CreateSessionDto;
import com.javaacademy.cinema.dto.SessionDto;
import com.javaacademy.cinema.dto.SessionResponse;
import com.javaacademy.cinema.entity.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionMapper {

    public Session toEntity(CreateSessionDto sessionDto) {
        Session session = new Session();
        session.setPrice(sessionDto.getPrice());
        session.setLocalDateTime(sessionDto.getLocalDateTime());
        return session;
    }

    public SessionDto toDto(Session session) {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(session.getId());
        sessionDto.setMovieId(session.getMovie().getId());
        sessionDto.setPrice(session.getPrice());
        sessionDto.setLocalDateTime(session.getLocalDateTime());
        return sessionDto;
    }

    public List<SessionResponse> toSessions(List<Session> sessions) {
        return sessions.stream().map(this::toSessionResponse).toList();
    }

    private SessionResponse toSessionResponse(Session session) {
        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setId(session.getId());
        sessionResponse.setMovieName(session.getMovie().getTitle());
        sessionResponse.setLocalDateTime(session.getLocalDateTime());
        sessionResponse.setPrice(session.getPrice());
        return sessionResponse;
    }
}

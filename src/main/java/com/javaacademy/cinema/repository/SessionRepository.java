package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.exception.AlreadyExistsSessionException;
import com.javaacademy.cinema.exception.NotFoundSessionException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SessionRepository {
    private final JdbcTemplate jdbcTemplate;
    private final MovieRepository movieRepository;

    public Session save(Session session) {
        checkPresenceSession(session);
        Integer movieId = session.getMovie().getId();
        Timestamp dateAndTime = Timestamp.valueOf(session.getLocalDateTime());
        BigDecimal price = session.getPrice();
        String sql = """
                insert into session (movie_id, date_and_time, price)
                values (?, ?, ?)
                returning id;
                """;
        log.info("Выполнен SQL запрос на сохранение сеанса: {}\n фильм: {}, цена: {}\n", sql,
                session.getMovie().getTitle(), price);
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, movieId, dateAndTime, price);
        session.setId(id);
        return session;
    }

    public List<Session> findAll() {
        String sql = """
                select *
                from session;
                """;
        log.info("Выполнен SQL запрос на получение всех сессий: {}", sql);
        return jdbcTemplate.query(sql, this::mapToSession);
    }

    public Optional<Session> selectById(Integer id) {
        String sql = """
                select *
                from Session
                where id = ?;
                """;
        try {
            Optional<Session> result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToSession, id));
            log.info("Выполнен SQL запрос: {}, по id = {}, результат: {}", sql, id, result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return (Optional.empty());
        }
    }

    @SneakyThrows
    private Session mapToSession(ResultSet rs, int rowNum) {
        Session session = new Session();
        session.setId(rs.getInt("id"));
        session.setLocalDateTime(rs.getTimestamp("date_and_time").toLocalDateTime());
        Movie movie = movieRepository.selectMovieById(rs.getInt("movie_id")).get();
        session.setMovie(movie);
        session.setPrice(rs.getBigDecimal("price"));
        return session;
    }

    public void checkPresenceSession(Session session) {
        Timestamp dateTime = Timestamp.valueOf(session.getLocalDateTime());
        BigDecimal price = session.getPrice();
        Integer movieId = session.getMovie().getId();
        String sql = """
                select *
                from Session
                where date_and_time = ? and price = ? and movie_id = ?;
                """;
        Optional<Session> result;
        try {
            result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToSession,
                    dateTime, price, movieId));
        } catch (EmptyResultDataAccessException e) {
            result = Optional.empty();
        }
        if (result.isPresent()) {
            throw new AlreadyExistsSessionException("Сеанс уже существует");
        }
    }

    public void checkPresenceSession(Integer id) {
        Optional<Session> session = selectById(id);
        if (session.isEmpty()) {
            throw new NotFoundSessionException("Сеанса не существует.");
        }
    }
}

package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.exception.AlreadyExistsFilmException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MovieRepository {
    private final JdbcTemplate jdbcTemplate;

    public Movie save(Movie movie) {
        checkPresenceMovie(movie);
        String title = movie.getTitle();
        String description = movie.getDescription();
        String sql = """
                insert into movie (title, description)
                values (?, ?)
                returning id;
                """;
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, title, description);
        log.info("Выполнен SQL запрос на сохранение фильма {}:\n {}\n", title, sql);
        movie.setId(id);
        return movie;
    }

    public Optional<Movie> selectMovieById(Integer id) {
        String sql = """
        select *
        from movie
        where id = ?;
        """;
        try {
            Optional<Movie> result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToMovie, id));
            log.info("Выполнен SQL запрос: {}, по id = {}, результат: {}", sql, id, result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return (Optional.empty());
        }
    }

    public List<Movie> selectAll() {
        String sql = """
        select *
        from movie
        """;
        return jdbcTemplate.query(sql, this::mapToMovie);
    }

    @SneakyThrows
    private Movie mapToMovie(ResultSet rs, int rowNum) {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setDescription(rs.getString("description"));
        return movie;
    }

    private void checkPresenceMovie(Movie movie) {
        String title = movie.getTitle();
        String sql = """
        select *
        from movie
        where title = ?;
        """;
        Optional<Movie> result;
        try {
            result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToMovie, title));
        } catch (EmptyResultDataAccessException e) {
            result = Optional.empty();
        }
        if (result.isPresent()) {
            throw new AlreadyExistsFilmException("Фильм уже существует");
        }

        log.info("Выполнен SQL запрос проверки наличия фильма \"{}\" в БД", title);
    }
}

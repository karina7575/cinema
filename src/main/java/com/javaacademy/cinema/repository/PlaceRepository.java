package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.exception.NotFoundPlaceException;
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
public class PlaceRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Place> selectById(Integer id) {
        String sql = """
                select *
                from place
                where id = ?
                """;
        try {
            Optional<Place> result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToPlace, id));
            log.info("Выполнен SQL запрос: {}, по id = {}, результат: {}", sql, id, result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return (Optional.empty());
        }
    }

    public List<Place> selectAll() {
        String sql = """
                select *
                from place;
                """;
        return jdbcTemplate.query(sql, this::mapToPlace);
    }

    public Optional<Place> selectByNumber(String number) {
        String sql = """
                select *
                from place
                where number = ?
                """;
        Optional<Place> result;
        try {
            result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToPlace, number));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundPlaceException("Место не существует.");
        }
        log.info("Выполнен SQL запрос поиска по номеру: {}", sql);
        return result;
    }

    @SneakyThrows
    private Place mapToPlace(ResultSet rs, int rowNum) {
        Place place = new Place();
        place.setId(rs.getInt("id"));
        place.setNumber(rs.getString("number"));
        return place;
    }
}

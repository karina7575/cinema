package com.javaacademy.cinema.repository;

import com.javaacademy.cinema.dto.BookingDto;
import com.javaacademy.cinema.entity.Ticket;
import com.javaacademy.cinema.exception.AlreadyBoughtTicketException;
import com.javaacademy.cinema.exception.NotFoundTicketException;
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
public class TicketRepository {
    private final JdbcTemplate jdbcTemplate;
    private final SessionRepository sessionRepository;
    private final PlaceRepository placeRepository;

    public Ticket save(Ticket ticket) {
        Integer placeId = ticket.getPlace().getId();
        Integer sessionId = ticket.getSession().getId();
        Boolean isBuy = ticket.getIsBuy();

        String sql = """
                insert into ticket (place_id, session_id, is_buy)
                values (?, ?, ?)
                returning id;
                """;
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, placeId, sessionId, isBuy);
        log.info("Выполнен SQL запрос на сохранение билета: {}\nsession_id = {}, place_id = {}, is_buy = {}",
                sql, sessionId, placeId, isBuy);
        ticket.setId(id);
        return ticket;
    }

    public void buy(Integer id) {
        String sql = """
                update ticket
                set is_buy = true
                where id = ?;
                """;

        Optional<Ticket> currentTicket = selectById(id);
        if (currentTicket.isEmpty()) {
            throw new NotFoundTicketException("Билет не найден.");
        }
        if (currentTicket.get().getIsBuy()) {
            throw new AlreadyBoughtTicketException("Билет уже куплен.");
        }
        jdbcTemplate.update(sql, id);
        log.info("Выполнен SQL запрос на обновление статуса билета на \"куплен\": {}", sql);
    }

    public List<Ticket> selectBuyTickets(Integer id) {
        sessionRepository.checkPresenceSession(id);
        return findTargetList(id, true);
    }

    public List<Ticket> selectNotBuyTickets(Integer id) {
        sessionRepository.checkPresenceSession(id);
        return findTargetList(id, false);
    }

    public Optional<Ticket> selectById(Integer id) {
        String sql = """
                select *
                from ticket
                where id = ?
                """;
        try {
            Optional<Ticket> result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapToTicket, id));
            log.info("Выполнен SQL запрос поиска по ID: {}", sql);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return (Optional.empty());
        }
    }

    @SneakyThrows
    private Ticket mapToTicket(ResultSet rs, int rowNum) {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setIsBuy(rs.getBoolean("is_buy"));
        ticket.setSession(sessionRepository.selectById(rs.getInt("session_id")).get());
        ticket.setPlace(placeRepository.selectById(rs.getInt("place_id")).get());
        return ticket;
    }

    private List<Ticket> findTargetList(Integer sessionId, Boolean isBuy) {
        String sql = """
                select *
                from ticket
                where is_buy = ? and session_id = ?;
                """;
        List<Ticket> result = jdbcTemplate.query(sql, this::mapToTicket, isBuy, sessionId);
        log.info("Выполнен SQl запрос на получение купленных/проданных билетов");
        return result;
    }

    public Optional<Integer> findByNumber(BookingDto bookingDto) {
        String sql = """
                select t.id
                from ticket t join place p on p.id =t.place_id
                where number = ? and t.session_id = ?;
                """;
        String number = bookingDto.getPlaceNumber();
        Integer sessionId = bookingDto.getSessionId();
        Optional<Integer> result;
        try {
            result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class, number, sessionId));
            log.info("Выполнен SQL запрос поиска по номеру сеанса и места: {}", sql);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

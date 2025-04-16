package javaacademy.cinema.web.controller;

import com.javaacademy.cinema.dto.BookingDto;
import com.javaacademy.cinema.dto.CreateSessionDto;
import com.javaacademy.cinema.dto.MovieDto;
import com.javaacademy.cinema.dto.SessionResponse;
import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.dto.TicketResponse;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.mapper.SessionMapper;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.service.SessionService;
import com.javaacademy.cinema.service.TicketService;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;

@AutoConfigureMockMvc
@DisplayName("Тесты контроллера кинотеарта")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CinemaControllerTest {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private SessionService sessionService;

    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("cinema/api")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    private static final int TEST_SESSION_ID = 5;
    private static final int EXPECTED_EMPTY_PLACES = 9;;
    private static final String CLEAN_TABLES = "delete from ticket; delete from session; delete from movie;";
    @BeforeEach()
    public void cleanUpData() {
        jdbcTemplate.execute(CLEAN_TABLES);
    }

    @Test
    @DisplayName("Успешное получение всех фильмов")
    public void findAllMovies() {
        int expectedSize = 1;
        MovieDto movieDto = createTestMovie();
        movieRepository.save(Movie.builder()
                .title(movieDto.getTitle())
                .description(movieDto.getDescription())
                .build());

        List<MovieDto> tickets = given(requestSpecification)
                .get("/movie")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        Assertions.assertEquals(expectedSize, tickets.size());
    }

    @Test
    @DisplayName("Успешное получение всех сеансов")
    public void findAllSessions() {
        int expectedSize = 1;
        Session session = createTestSession();
        sessionRepository.save(session);

        List<SessionResponse> sessions = given(requestSpecification)
                .get("/session")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        Assertions.assertEquals(expectedSize, sessions.size());
    }

    @Test
    @DisplayName("Успешня покупка билета")
    public void buyTicketSuccess() {
        String expectedNumber = "A1";
        Integer sessionId = getSessionId();
        BookingDto bookingDto = new BookingDto(sessionId, expectedNumber);

        TicketResponse ticketResponse = given(requestSpecification)
                .body(bookingDto)
                .post("/ticket/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(TicketResponse.class);
        Assertions.assertEquals(expectedNumber, ticketResponse.getPlaceNumber());
    }

    @Test
    @DisplayName("Покупка билета - ошибка: Сеанс не найден")
    public void buyTicketFailedNotFoundSession() {
        String placeTestNumber = "A1";


        BookingDto bookingDto = new BookingDto(TEST_SESSION_ID, placeTestNumber);

        given(requestSpecification)
                .body(bookingDto)
                .post("/ticket/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
    @DisplayName("Покупка билета - ошибка: несуществующее место")
    public void buyTicketFailedIncorrectPlace() {
        String testPlaceNumber = "A100";
        Integer sessionId = getSessionId();
        BookingDto bookingDto = new BookingDto(sessionId, testPlaceNumber);

        given(requestSpecification)
                .body(bookingDto)
                .post("/ticket/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Успешное получение всех свободных мест на сеанс")
    public void findEmptyPlaces() {
        Integer sessionId = getSessionId();
        String placeNumber = "A1";
        BookingDto bookingDto = new BookingDto(sessionId, placeNumber);
        ticketService.buyTicket(bookingDto);

        List<String> emptyPlaceTickets = given(requestSpecification)
                .pathParam("id", sessionId)
                .get("/session/{id}/free-place")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        Assertions.assertEquals(EXPECTED_EMPTY_PLACES, emptyPlaceTickets.size());
    }

    @Test
    @DisplayName("Покупка билета - ошибка: место занято")
    public void buyTicketFailedOccupiedPlace() {
        String testPlaceNumber = "A2";
        Integer sessionId = getSessionId();
        BookingDto bookingDto = new BookingDto(sessionId, testPlaceNumber);
        ticketService.buyTicket(bookingDto);

        given(requestSpecification)
                .body(bookingDto)
                .post("/ticket/booking")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.CONFLICT.value());
    }

    private MovieDto createTestMovie() {
        String expectedTitle = "Форсаж";
        String expectedDescription = "Полицейский под прикрытием Брайан О'Коннор (Пол Уокер) внедряется в команду"
                + " Доминика Торетто (Вин Дизель), чтобы раскрыть банду стритрейсеров, грабящих грузовики";
        return new MovieDto(expectedTitle, expectedDescription);
    }

    private Session createTestSession() {
        MovieDto movieDto = createTestMovie();
        Movie realMovie = movieRepository.save(Movie.builder()
                .title(movieDto.getTitle())
                .description(movieDto.getDescription())
                .build());
        CreateSessionDto sessionDto = CreateSessionDto.builder()
                .localDateTime(LocalDateTime.now())
                .movieId(realMovie.getId())
                .price(BigDecimal.TEN)
                .build();
        Session session = sessionMapper.toEntity(sessionDto);
        session.setMovie(realMovie);
        return session;
    }

    private Integer getSessionId() {
        MovieDto movieDto = createTestMovie();
        Movie realMovie = movieRepository.save(Movie.builder()
                .title(movieDto.getTitle())
                .description(movieDto.getDescription())
                .build());
        CreateSessionDto sessionDto = CreateSessionDto.builder()
                .localDateTime(LocalDateTime.now())
                .movieId(realMovie.getId())
                .price(BigDecimal.TEN)
                .build();
        List<TicketDto> tickets = sessionService.saveSession(sessionDto);
        return tickets.stream().findFirst().get().getSession().getId();
    }

}

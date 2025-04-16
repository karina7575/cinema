package javaacademy.cinema.web.controller;

import com.javaacademy.cinema.dto.CreateSessionDto;
import com.javaacademy.cinema.dto.MovieDto;
import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.entity.Session;
import com.javaacademy.cinema.mapper.SessionMapper;
import com.javaacademy.cinema.repository.MovieRepository;
import com.javaacademy.cinema.repository.SessionRepository;
import com.javaacademy.cinema.repository.TicketRepository;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;

@AutoConfigureMockMvc
@DisplayName("Тесты контроллера билетов")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TicketControllerTest {
    @Value("${app.admin_token}")
    String trueToken;
    @Value("${app.admin_password}")
    String truePassword;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int FAKE_SESSION_ID = -5;
    private static final String CLEAN_TABLES = "delete from session; delete from movie; delete from ticket";
    @BeforeEach()
    public void cleanUpData() {
        jdbcTemplate.execute(CLEAN_TABLES);
    }

    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("cinema/ticket")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();


    @Test
    @DisplayName("Успешное получение купленных билетов")
    public void findBuyTicketsSuccess() {
        Session session = createTestSession();

        List<TicketDto> tickets = given(requestSpecification)
                .header("token", trueToken)
                .header("password", truePassword)
                .pathParam("id", session.getId())
                .get("/saled/{id}")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

    }

    @Test
    @DisplayName("Получение купленных билетов - ошибка: Нет такого сеанса")
    public void findBuyTicketsFailed() {
        Session session = createTestSession();

        given(requestSpecification)
                .header("token", trueToken)
                .header("password", truePassword)
                .pathParam("id", FAKE_SESSION_ID)
                .get("/saled/{id}")
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value());
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
        return sessionRepository.save(session);
    }
}

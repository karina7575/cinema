package javaacademy.cinema.web.controller;

import com.javaacademy.cinema.dto.CreateSessionDto;
import com.javaacademy.cinema.dto.MovieDto;
import com.javaacademy.cinema.dto.TicketDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.repository.MovieRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@DisplayName("Тесты контроллера сеансов")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SessionControllerTest {
    @Value("${app.admin_token}")
    String trueToken;
    @Value("${app.admin_password}")
    String truePassword;

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int FAKE_FILM_ID = 5;
    private static final int EXPECTED_COUNT_PLACE = 10;
    private static final String CLEAN_TABLES = "truncate table session, movie, ticket;";
    @BeforeEach()
    public void cleanUpData() {
        jdbcTemplate.execute(CLEAN_TABLES);
    }

    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("cinema/session")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @Test
    @DisplayName("Создание сеанса - успешно")
    public void createSessionSuccess() {
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

        List<TicketDto> tickets = given(requestSpecification)
                .header("token", trueToken)
                .header("password", truePassword)
                .body(sessionDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });

        assertEquals(EXPECTED_COUNT_PLACE, tickets.size());
    }

    @Test
    @DisplayName("Создание сеанса - обишка: Фильм не существует")
    public void createSessionFailed() {
        CreateSessionDto sessionDto = CreateSessionDto.builder()
                .localDateTime(LocalDateTime.now())
                .movieId(FAKE_FILM_ID)
                .price(BigDecimal.TEN)
                .build();

        given(requestSpecification)
                .header("token", trueToken)
                .header("password", truePassword)
                .body(sessionDto)
                .post()
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
}

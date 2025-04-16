package javaacademy.cinema.web.controller;

import com.javaacademy.cinema.dto.AdminMovieDto;
import com.javaacademy.cinema.dto.MovieDto;
import com.javaacademy.cinema.entity.Movie;
import com.javaacademy.cinema.repository.MovieRepository;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@DisplayName("Тесты контроллера фильмов")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MovieControllerTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MovieRepository movieRepository;
    @Value("${app.admin_token}")
    String trueToken;
    @Value("${app.admin_password}")
    String truePassword;

    private static final String CLEAN_MOVIE_TABLE = "truncate table session, movie, ticket;";

    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("cinema/movie")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();

    @BeforeEach()
    public void cleanUpData() {
        jdbcTemplate.execute(CLEAN_MOVIE_TABLE);
    }

    @Test
    @DisplayName("Сохранение фильма - успешно")
    public void createMovieSuccess() {
        MovieDto movieDto = createTestMovie();

        AdminMovieDto resultMovie = given(requestSpecification)
                .header("token", trueToken)
                .header("password", truePassword)
                .body(movieDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(AdminMovieDto.class);

        assertEquals(movieDto.getTitle(), resultMovie.getTitle());
        assertEquals(movieDto.getDescription(), resultMovie.getDescription());
    }

    @Test
    @DisplayName("Сохранение фильма - ошибка: Фильм уже существует")
    public void createMovieFailed() {
        MovieDto movieDto = createTestMovie();
        movieRepository.save(Movie.builder()
                .title(movieDto.getTitle())
                .description(movieDto.getDescription())
                .build());

        given(requestSpecification)
                .header("token", trueToken)
                .header("password", truePassword)
                .body(movieDto)
                .post()
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

}

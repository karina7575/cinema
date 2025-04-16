package com.javaacademy.cinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
    @Schema(description = "id сеанса")
    private Integer id;
    @JsonProperty("date_and_time")
    @Schema(description = "время сеанса")
    private LocalDateTime localDateTime;
    @Schema(description = "цена")
    private BigDecimal price;
    @JsonProperty("movie_id")
    @Schema(description = "id фильма")
    private Integer movieId;
}

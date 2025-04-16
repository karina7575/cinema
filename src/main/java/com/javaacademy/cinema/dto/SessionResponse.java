package com.javaacademy.cinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionResponse {
    @Schema(description = "id сеанса")
    private Integer id;
    @JsonProperty("movie_name")
    @Schema(description = "Название фильма")
    private String movieName;
    @JsonProperty("date")
    @Schema(description = "Время начала сеанса")
    private LocalDateTime localDateTime;
    @Schema(description = "цена")
    private BigDecimal price;
}

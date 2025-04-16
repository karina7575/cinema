package com.javaacademy.cinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    @JsonProperty("ticket_id")
    @Schema(description = "id билета")
    private Integer id;
    @JsonProperty("place_name")
    @Schema(description = "номер места")
    private String placeNumber;
    @JsonProperty("movie_name")
    @Schema(description = "название фильма")
    private String movieName;
    @JsonProperty
    @Schema(description = "время сеанса")
    private LocalDateTime date;
}

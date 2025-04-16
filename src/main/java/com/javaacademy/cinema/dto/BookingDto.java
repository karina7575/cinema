package com.javaacademy.cinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    @JsonProperty("session_id")
    @Schema(description = "id сеанса")
    private Integer sessionId;
    @JsonProperty("place_name")
    @Schema(description = "номер места")
    private String placeNumber;
}

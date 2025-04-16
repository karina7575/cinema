package com.javaacademy.cinema.dto;

import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDto {
    @Schema(description = "id билета")
    private Integer id;
    @Schema(description = "сеанс")
    private Session session;
    @Schema(description = "место")
    private Place place;
    @Schema(description = "куплен / не куплен")
    private Boolean isBuy;
}

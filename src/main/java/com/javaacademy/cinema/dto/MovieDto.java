package com.javaacademy.cinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    @JsonProperty("name")
    @Schema(description = "Название")
    private String title;
    @Schema(description = "Описание")
    private String description;
}

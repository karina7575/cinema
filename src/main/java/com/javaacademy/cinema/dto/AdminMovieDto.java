package com.javaacademy.cinema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMovieDto {
    @Schema(description = "id фильма")
    private Integer id;
    @Schema(description = "Название")
    private String title;
    @Schema(description = "Описание")
    private String description;
}

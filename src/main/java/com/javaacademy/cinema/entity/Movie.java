package com.javaacademy.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {
    private Integer id;
    private String title;
    private String description;
}

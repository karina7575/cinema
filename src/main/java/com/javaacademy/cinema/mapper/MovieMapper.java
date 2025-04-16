package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.dto.AdminMovieDto;
import com.javaacademy.cinema.dto.MovieDto;
import com.javaacademy.cinema.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieMapper {

    public Movie toEntity(MovieDto movieDto) {
        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        return movie;
    }

    public AdminMovieDto toDto(Movie movie) {
        AdminMovieDto movieDto = new AdminMovieDto();
        movieDto.setId(movie.getId());
        movieDto.setTitle(movie.getTitle());
        movieDto.setDescription(movie.getDescription());
        return movieDto;
    }

    public List<MovieDto> toResponses(List<Movie> movies) {
        return movies.stream().map(this::toResponse).toList();
    }

    public MovieDto toResponse(Movie movie) {
        MovieDto createMovieDto = new MovieDto();
        createMovieDto.setTitle(movie.getTitle());
        createMovieDto.setDescription(movie.getDescription());
        return createMovieDto;
    }
}

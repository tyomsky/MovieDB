package com.tyomsky.moviedb.mapper;

import com.tyomsky.moviedb.api.dto.MovieDTO;
import com.tyomsky.moviedb.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesMapper {

    public static List<Movie> map(List<MovieDTO> movieDTOs) {
        List<Movie> movies = new ArrayList<>(movieDTOs.size());
        for (MovieDTO movieDTO : movieDTOs) {
            movies.add(MovieMapper.map(movieDTO));
        }
        return movies;
    }

}

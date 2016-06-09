package com.tyomsky.moviedb.mapper;

import com.tyomsky.moviedb.api.dto.MovieDTO;
import com.tyomsky.moviedb.model.Movie;

public class MovieMapper {

    public static Movie map(MovieDTO movieDTO) {
        return new Movie.Builder()
                .id(movieDTO.getId())
                .title(movieDTO.getTitle())
                .popularity(movieDTO.getPopularity())
                .overview(movieDTO.getOverview())
                .posterPath(movieDTO.getPosterPath())
                .voteAverage(movieDTO.getVoteAverage())
                .voteCount(movieDTO.getVoteCount())
                .backdropPath(movieDTO.getBackdropPath())
                .build();
    }

}

package com.tyomsky.moviedb.api;

import com.tyomsky.moviedb.api.dto.MoviesCollectionDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {

    String BASE_URL = "http://api.themoviedb.org/3/";

    @GET(value = "movie/top_rated/")
    Call<MoviesCollectionDTO> getTopRatedMovies();

    @GET(value = "movie/popular")
    Call<MoviesCollectionDTO> getPopularMovies();

    @GET(value = "discover/movie")
    Call<MoviesCollectionDTO> getMovies(@Query(value = "sort_by") String sortBy,
                                        @Query(value = "page") int page);
}

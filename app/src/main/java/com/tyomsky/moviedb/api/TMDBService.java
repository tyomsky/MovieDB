package com.tyomsky.moviedb.api;

import com.tyomsky.moviedb.model.MoviesCollection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {

    String BASE_URL = "http://api.themoviedb.org/3/";

    @GET(value = "movie/top_rated/")
    Call<MoviesCollection> getTopRatedMovies();

    @GET(value = "movie/popular")
    Call<MoviesCollection> getPopularMovies();

    @GET(value = "discover/movie")
    Call<MoviesCollection> getMovies(@Query(value = "sort_by") String sortBy,
                                     @Query(value = "page") int page);
}

package tyomsky.com.moviedb.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDBService {

    @GET(value = "movie/top_rated/")
    Call<MovieDBResponse> getTopRatedMovies();

    @GET(value = "movie/popular")
    Call<MovieDBResponse> getPopularMovies();

    @GET(value = "discover/movie")
    Call<MovieDBResponse> getMovies(@Query(value = "sort_by") String sortBy);
}

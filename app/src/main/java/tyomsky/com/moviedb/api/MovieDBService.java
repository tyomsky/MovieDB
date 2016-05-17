package tyomsky.com.moviedb.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MovieDBService {

    @GET(value = "movie/top_rated/")
    Call<MovieDBResponse> getTopRatedMovies();
    @GET(value = "movie/popular")
    Call<MovieDBResponse> getPopularMovies();

}

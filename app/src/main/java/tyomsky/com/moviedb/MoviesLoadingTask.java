package tyomsky.com.moviedb;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tyomsky.com.moviedb.api.MovieDBService;
import tyomsky.com.moviedb.model.Movie;

public class MoviesLoadingTask extends AsyncTask<String, Void, List<Movie>> {

    private static final String TAG = "LoadingTask";
    private static final String TOP_RATED_METHOD = "vote_average.desc";
    private static final String POPULAR_METHOD = "popularity.desc";
    private Context context;
    private RecyclerView recyclerView;

    public MoviesLoadingTask(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        String sortBy = params[0];
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder()
                                .addQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                                .build();
                        request = request.newBuilder().url(url).build();
                        Log.d(TAG, "OkHTTP request: " + url);
                        return chain.proceed(request);
                    }
                }).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        MovieDBService service = retrofit.create(MovieDBService.class);
        List<Movie> movies = null;
        try {
            movies = service.getMovies(
                    context.getString(R.string.pref_sortBy_popular).equals(sortBy) ?
                            POPULAR_METHOD : TOP_RATED_METHOD)
                    .execute().body().getResults();
        } catch (IOException e) {
            Log.e(TAG, "Cant get movies", e);
        }
        return movies;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        recyclerView.setAdapter(new MovieListAdapter(context, movies));
    }
}

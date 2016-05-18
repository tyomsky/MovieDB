package tyomsky.com.moviedb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tyomsky.com.moviedb.api.MovieDBService;
import tyomsky.com.moviedb.model.Movie;

public class MainActivity extends AppCompatActivity {

    private static final String TOP_RATED_METHOD = "vote_average.desc";
    private static final String POPULAR_METHOD = "popularity.desc";

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setAdapter(new MovieListAdapter(this, new ArrayList<Movie>()));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        updateMovies("popularity");
    }

    private void updateMovies(String sortBy) {
        new LoadingTask().execute(sortBy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_sort_by);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_by_entries,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortBy = getResources().getStringArray(R.array.sort_by_entry_values)[position];
                updateMovies(sortBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
        return true;
    }

    private class LoadingTask extends AsyncTask<String, Void, List<Movie>> {

        private static final String TAG = "LoadingTask";

        @Override
        protected List<Movie> doInBackground(String... params) {
            String sortBy = params[0];
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            HttpUrl url = request.url().newBuilder()
                                    .addQueryParameter("api_key",BuildConfig.THEMOVIEDB_API_KEY)
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
                movies = service.getMovies("popular".equals(sortBy) ? POPULAR_METHOD : TOP_RATED_METHOD)
                        .execute().body().getResults();
            } catch (IOException e) {
                Log.e(TAG, "Cant get movies", e);
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            recyclerView.setAdapter(new MovieListAdapter(MainActivity.this, movies));
        }
    }
}

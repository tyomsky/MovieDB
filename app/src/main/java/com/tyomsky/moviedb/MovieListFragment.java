package com.tyomsky.moviedb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tyomsky.moviedb.api.ServiceGenerator;
import com.tyomsky.moviedb.api.TMDBService;
import com.tyomsky.moviedb.model.Movie;
import com.tyomsky.moviedb.model.MoviesCollection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListFragment extends Fragment {

    public static final int PAGE_SIZE = 20;

    private int currentPage;
    private TMDBService tmdbService;
    private List<Call> calls;
    private MoviesAdapter moviesAdapter;

    private Callback<MoviesCollection> moviesFirstFetchCallback = new Callback<MoviesCollection>() {
        @Override
        public void onResponse(Call<MoviesCollection> call, Response<MoviesCollection> response) {
            if (response != null && response.isSuccessful()) {
                MoviesCollection collection = response.body();
                if (collection != null) {
                    List<Movie> movies = collection.getResults();
                    if (movies != null) {
                        moviesAdapter.setMovies(movies);
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<MoviesCollection> call, Throwable t) {
            Log.e(getClass().getName(), "Unable to load data!", t);
        }
    };

    private Callback<MoviesCollection> moviesNextFetchCallback = new Callback<MoviesCollection>() {
        @Override
        public void onResponse(Call<MoviesCollection> call, Response<MoviesCollection> response) {

        }

        @Override
        public void onFailure(Call<MoviesCollection> call, Throwable t) {

        }
    };

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        calls = new ArrayList<>();
        tmdbService = ServiceGenerator.getService(TMDBService.class, TMDBService.BASE_URL,
                BuildConfig.THEMOVIEDB_API_KEY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_sort_by);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.sort_by_entries,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final String sortByPrefKey = getString(R.string.pref_sortBy_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int position = prefs.getInt(sortByPrefKey, 1);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putInt(sortByPrefKey, position).apply();
                String sortBy = getResources().getStringArray(R.array.sort_by_entry_values)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_list_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int prefIndex = prefs.getInt(getString(R.string.pref_sortBy_key), 1);
        String sortBy = getResources().getStringArray(R.array.sort_by_entry_values)[prefIndex];
        Call<MoviesCollection> call = tmdbService.getMovies(sortBy);
        call.enqueue(moviesFirstFetchCallback);
        calls.add(call);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (Call call : calls) {
            call.cancel();
        }
    }

}

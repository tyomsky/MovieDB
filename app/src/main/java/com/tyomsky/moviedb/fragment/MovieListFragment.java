package com.tyomsky.moviedb.fragment;

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

import com.tyomsky.moviedb.BuildConfig;
import com.tyomsky.moviedb.MoviesAdapter;
import com.tyomsky.moviedb.R;
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
    private static final int THRESHOLD = 16;

    private int currentPage = 1;
    private boolean isLastPage;
    private TMDBService tmdbService;
    private MoviesAdapter moviesAdapter;
    private boolean isLoading;
    private List<Call> calls;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    private GridLayoutManager layoutManager;

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
        int position = getPreferredSortingSpinnerPosition(sortByPrefKey);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putInt(sortByPrefKey, position).apply();
                fetchFirstMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    private int getPreferredSortingSpinnerPosition(String sortByPrefKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getInt(sortByPrefKey, 1);
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
        int spanCount = getResources().getInteger(R.integer.span_count);
        layoutManager = new GridLayoutManager(getActivity(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(onScrollListener);
        fetchFirstMovies();
    }

    private void fetchFirstMovies() {
        moviesAdapter.clear();
        currentPage = 1;
        String sortBy = getPreferredSorting();
        Call<MoviesCollection> call = tmdbService.getMovies(sortBy, currentPage);
        call.enqueue(moviesFetchCallback);
        calls.add(call);
    }

    private String getPreferredSorting() {
        int prefIndex = getPreferredSortingSpinnerPosition(getString(R.string.pref_sortBy_key));
        return getResources().getStringArray(R.array.sort_by_entry_values)[prefIndex];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        currentPage = 1;
        for (Call call : calls) {
            call.cancel();
        }
    }

    //callbacks
    private Callback<MoviesCollection> moviesFetchCallback = new Callback<MoviesCollection>() {
        @Override
        public void onResponse(Call<MoviesCollection> call, Response<MoviesCollection> response) {
            isLoading = false;
            if (response != null && response.isSuccessful()) {
                MoviesCollection collection = response.body();
                if (collection != null) {
                    isLastPage = collection.getTotalPages() <= currentPage;
                    List<Movie> movies = collection.getResults();
                    if (movies != null) {
                        moviesAdapter.addAll(movies, true);
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<MoviesCollection> call, Throwable t) {
            Log.e(getClass().getName(), "Unable to load data!", t);
        }
    };

    //listeners
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE - THRESHOLD) {
                    fetchMoreItems();
                }
            }
        }
    };

    private void fetchMoreItems() {
        isLoading = true;
        currentPage += 1;
        String sortBy = getPreferredSorting();
        Call<MoviesCollection> call = tmdbService.getMovies(sortBy, currentPage);
        call.enqueue(moviesFetchCallback);
        calls.add(call);
    }

}

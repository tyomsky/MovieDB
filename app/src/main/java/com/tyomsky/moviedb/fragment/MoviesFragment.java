package com.tyomsky.moviedb.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.tyomsky.moviedb.R;
import com.tyomsky.moviedb.adapter.MoviesAdapter;
import com.tyomsky.moviedb.api.ServiceGenerator;
import com.tyomsky.moviedb.api.TMDBService;
import com.tyomsky.moviedb.api.dto.MovieDTO;
import com.tyomsky.moviedb.api.dto.MoviesCollectionDTO;
import com.tyomsky.moviedb.mapper.MoviesMapper;
import com.tyomsky.moviedb.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment implements MoviesAdapter.OnItemClickListener {

    public static final int PAGE_SIZE = 20;
    private static final int THRESHOLD = 0;

    private int currentPage = 1;
    private boolean isLastPage;
    private TMDBService tmdbService;
    private MoviesAdapter moviesAdapter;
    private boolean isLoading;
    private List<Call> calls;
    private Callbacks callbacks;

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
                ((AppCompatActivity)getActivity()).getSupportActionBar().getThemedContext(),
                R.array.sort_by_entries,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int position = getPreferredSortingSpinnerPosition();
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setPreferredSortingSpinnerPosition(position);
                fetchFirstMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    private void setPreferredSortingSpinnerPosition(int position) {
        final String sortByPrefKey = getString(R.string.pref_sortBy_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt(sortByPrefKey, position).apply();
    }

    private int getPreferredSortingSpinnerPosition() {
        final String sortByPrefKey = getString(R.string.pref_sortBy_key);
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
        int spanCount = getResources().getInteger(R.integer.span_count);
        layoutManager = new GridLayoutManager(getActivity(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        moviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        moviesAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(moviesAdapter);
        SlideInUpAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(400);
        recyclerView.setItemAnimator(animator);
        recyclerView.addOnScrollListener(onScrollListener);

        fetchFirstMovies();
    }

    private void fetchFirstMovies() {
        moviesAdapter.clear();
        currentPage = 1;
        String sortBy = getPreferredSorting();
        Call<MoviesCollectionDTO> call = tmdbService.getMovies(sortBy, currentPage);
        call.enqueue(moviesFetchCallback);
        calls.add(call);
    }

    private String getPreferredSorting() {
        int prefIndex = getPreferredSortingSpinnerPosition();
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
    private Callback<MoviesCollectionDTO> moviesFetchCallback = new Callback<MoviesCollectionDTO>() {
        @Override
        public void onResponse(Call<MoviesCollectionDTO> call, Response<MoviesCollectionDTO> response) {
            isLoading = false;
            if (response != null && response.isSuccessful()) {
                MoviesCollectionDTO collection = response.body();
                if (collection != null) {
                    isLastPage = collection.getTotalPages() <= currentPage;
                    List<MovieDTO> movies = collection.getResults();
                    if (movies != null) {
                        moviesAdapter.addAll(MoviesMapper.map(movies), true);
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<MoviesCollectionDTO> call, Throwable t) {
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
        Call<MoviesCollectionDTO> call = tmdbService.getMovies(sortBy, currentPage);
        call.enqueue(moviesFetchCallback);
        calls.add(call);
    }

    @Override
    public void onClick(int position) {
        Movie movie = moviesAdapter.get(position);
        if (callbacks != null) {
            callbacks.onItemClickListener(movie);
        }
    }

    public interface Callbacks {

        void onItemClickListener(Movie movie);

    }

}

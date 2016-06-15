package com.tyomsky.moviedb.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tyomsky.moviedb.R;
import com.tyomsky.moviedb.model.Movie;
import com.tyomsky.moviedb.util.PosterHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieFragment extends Fragment {

    private static final String ARGS_MOVIE = "args_movie";

    private Movie movie;

    @Bind(R.id.backdrop)
    ImageView backdrop;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.favorite_fab)
    FloatingActionButton fab;
    @Bind(R.id.overview)
    TextView overview;
    @Bind(R.id.release_year)
    TextView releaseYear;
    @Bind(R.id.rating)
    RatingBar rating;

    public static MovieFragment newInstance(Movie movie) {

        Bundle args = new Bundle();
        args.putParcelable(ARGS_MOVIE, movie);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        ButterKnife.bind(this, rootView);
        movie = getArguments().getParcelable(ARGS_MOVIE);
        if (movie != null) {
            toolbar.setTitle(movie.getTitle());
            toolbar.setTitleTextAppearance(getActivity(), R.style.MovieTitleStyle);
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "button clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
        loadBackdrop();
        showMovieDetails();

        return rootView;
    }

    private void showMovieDetails() {
        if (movie != null) {
            overview.setText(movie.getOverview());
            releaseYear.setText(movie.getReleaseDate().substring(0, 4));
            rating.setRating((float)movie.getVoteAverage().doubleValue() % 5); // max rating = 10
        }
    }

    private void loadBackdrop() {
        if (movie != null && movie.getBackdropPath() != null) {
            Uri backdropUri = PosterHelper.buildBackdropUri(movie.getBackdropPath());
            Picasso.with(getActivity()).load(backdropUri).into(backdrop);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}

package com.tyomsky.moviedb.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.tyomsky.moviedb.api.dto.MovieDTO;

public class MovieFragment extends Fragment {

    private static final String ARGS_MOVIE = "args_movie";

    public static MovieFragment newInstance(MovieDTO movie) {

        Bundle args = new Bundle();
        args.putInt(ARGS_MOVIE, movie.getId());
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

}

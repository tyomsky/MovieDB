package com.tyomsky.moviedb.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tyomsky.moviedb.R;
import com.tyomsky.moviedb.fragment.MovieFragment;
import com.tyomsky.moviedb.model.Movie;

public class MovieActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);
        Movie movie = getIntent().getExtras().getParcelable(EXTRA_MOVIE);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = MovieFragment.newInstance(movie);
        manager.beginTransaction().add(R.id.container, fragment).commit();
    }

}

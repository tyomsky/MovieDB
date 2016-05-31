package com.tyomsky.moviedb.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tyomsky.moviedb.R;
import com.tyomsky.moviedb.fragment.MovieFragment;
import com.tyomsky.moviedb.model.Movie;

public class MovieActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.movie_activity);

        Movie movie = getIntent().getExtras().getParcelable(EXTRA_MOVIE);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = MovieFragment.newInstance(movie);
        manager.beginTransaction().add(R.id.container, fragment).commit();
    }
}

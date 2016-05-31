package com.tyomsky.moviedb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tyomsky.moviedb.R;
import com.tyomsky.moviedb.fragment.MoviesFragment;
import com.tyomsky.moviedb.model.Movie;

public class MoviesActivity extends AppCompatActivity implements MoviesFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list_activity);
        Fragment fragment = new MoviesFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onItemClickListener(Movie movie) {
        Intent intent = new Intent(this, MovieActivity.class);
        intent.putExtra(MovieActivity.EXTRA_MOVIE, movie);
    }
}

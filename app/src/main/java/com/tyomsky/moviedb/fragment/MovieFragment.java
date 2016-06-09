package com.tyomsky.moviedb.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tyomsky.moviedb.R;
import com.tyomsky.moviedb.model.Movie;
import com.tyomsky.moviedb.util.PosterHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieFragment extends Fragment {

    private static final String ARGS_MOVIE = "args_movie";

    private Movie movie;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

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
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(movie.getTitle());
        loadBackdrop();
        return rootView;
    }

    private void loadBackdrop() {
        if (movie != null && movie.getBackdropPath() != null) {
            Uri backdropUri = PosterHelper.buildBackdropUri(movie.getBackdropPath());
            Picasso.with(getActivity()).load(backdropUri).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    toolbar.setBackground(new BitmapDrawable(getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}

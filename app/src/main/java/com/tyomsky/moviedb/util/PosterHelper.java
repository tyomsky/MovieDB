package com.tyomsky.moviedb.util;

import android.net.Uri;

import com.tyomsky.moviedb.MovieDBApplication;
import com.tyomsky.moviedb.model.Movie;

public class PosterHelper {

    public static Uri buildPosterUri(Movie movie) {
        String moviePoster = movie.getPosterPath();
        return Uri.parse(MovieDBApplication.BASE_IMAGE_URI).buildUpon()
                .appendPath(MovieDBApplication.POSTER_SIZE_PATH)
                .appendEncodedPath(moviePoster)
                .build();
    }

    public static Uri buildBackdropUri(String backgroundPath) {
        return Uri.parse(MovieDBApplication.BASE_IMAGE_URI).buildUpon()
                .appendPath(MovieDBApplication.BACKGROUND_POSTER_SIZE_PATH)
                .appendEncodedPath(backgroundPath)
                .build();
    }

}

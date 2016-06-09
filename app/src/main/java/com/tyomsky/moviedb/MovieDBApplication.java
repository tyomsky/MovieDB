package com.tyomsky.moviedb;

import android.app.Application;

import java.io.File;

public class MovieDBApplication extends Application {

    public static Application currentApplication;
    public static final String BASE_IMAGE_URI = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE_PATH = "w185";
    public static final String BACKGROUND_POSTER_SIZE_PATH = "w342";


    public static File getCacheDirectory() {
        return currentApplication.getCacheDir();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentApplication = this;
    }
}

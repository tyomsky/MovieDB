package com.tyomsky.moviedb;

import android.app.Application;

import java.io.File;

public class MovieDBApplication extends Application {

    public static Application currentApplication;

    public static File getCacheDirectory() {
        return currentApplication.getCacheDir();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentApplication = this;
    }
}

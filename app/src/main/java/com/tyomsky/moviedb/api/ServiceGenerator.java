package com.tyomsky.moviedb.api;


import android.util.Log;

import com.tyomsky.moviedb.MovieDBApplication;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final String TAG = "ServiceGenerator";
    private static final long CACHE_SIZE = 50 * 1024 * 1024;

    public static <S> S getService(Class<S> serviceClass, String baseUrl, String apiKey) {
        OkHttpClient client = getClient().newBuilder()
                .addInterceptor(new ApiKeyInterceptor(apiKey))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient getClient() {
        File cacheDir = new File(MovieDBApplication.getCacheDirectory(), "http");
        Cache cache = new Cache(cacheDir, CACHE_SIZE);
        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }

    private static class ApiKeyInterceptor implements Interceptor {

        private String apiKey;

        public ApiKeyInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder()
                    .addQueryParameter("api_key", apiKey)
                    .build();
            request = request.newBuilder().url(url).build();
            Log.d(TAG, "OkHTTP request: " + url);
            return chain.proceed(request);
        }
    }
}
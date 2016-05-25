package com.tyomsky.moviedb;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tyomsky.moviedb.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = "MovieListAdapter";
    private static final String BASE_IMAGE_URI = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE_PATH = "w185";
    private Context context;
    private List<Movie> movies = new ArrayList<>();

    public MoviesAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MovieViewHolder(inflater.inflate(R.layout.movie_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bindMovie(movies.get(position));
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void addAll(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public void addAll(List<Movie> movies, boolean preloadImages) {
        this.movies.addAll(movies);
        preloadImages(movies);
        notifyDataSetChanged();
    }

    private void preloadImages(List<Movie> movies) {
        for (Movie movie : movies) {
            Uri posterUri = buildPosterUri(movie);
            Picasso.with(context).load(posterUri);
        }
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_item_image_view)
        ImageView imageView;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindMovie(Movie movie) {
            Uri imageUri = buildPosterUri(movie);
            Log.d(TAG, imageUri.toString());

            Picasso.with(context)
                    .load(imageUri)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(imageView);
        }
    }

    private Uri buildPosterUri(Movie movie) {
        String moviePoster = movie.getPosterPath();
        return Uri.parse(BASE_IMAGE_URI).buildUpon()
                .appendPath(IMAGE_SIZE_PATH)
                .appendEncodedPath(moviePoster)
                .build();
    }

}



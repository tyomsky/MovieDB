package com.tyomsky.moviedb.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tyomsky.moviedb.R;
import com.tyomsky.moviedb.model.Movie;
import com.tyomsky.moviedb.util.PosterHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = "MovieListAdapter";

    private Context context;
    private List<Movie> movies = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public Movie get(int position) {
        return movies.get(position);
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public MoviesAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.movies_item, parent, false);
        return new MovieViewHolder(rootView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bindMovie(movies.get(position));
    }

    public void addAll(List<Movie> movies) {
        for (Movie movie : movies) {
            add(movie);
        }
    }

    public void addAll(List<Movie> movies, boolean preloadImages) {
        addAll(movies);
        preloadImages(movies);
    }

    private void add(Movie movie) {
        movies.add(movie);
        notifyItemInserted(movies.size() - 1);
    }

    private void preloadImages(List<Movie> movies) {
        for (Movie movie : movies) {
            Uri posterUri = PosterHelper.buildPosterUri(movie);
            Picasso.with(context).load(posterUri).fetch();
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });
            ButterKnife.bind(this, view);
        }

        public void bindMovie(Movie movie) {
            Uri imageUri = PosterHelper.buildPosterUri(movie);
            Log.d(TAG, imageUri.toString());

            Picasso.with(context)
                    .load(imageUri)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(imageView);
        }
    }

}



package tyomsky.com.moviedb;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import tyomsky.com.moviedb.model.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>{

    private static final String TAG = "MovieListAdapter";
    private static final String BASE_IMAGE_URI = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE_PATH = "w185";
    private Context context;
    private List<Movie> movies = new ArrayList<>();

    public MovieListAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
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

    class MovieViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.movie_item_image_view)
        ImageView imageView;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindMovie(Movie movie) {
            String moviePoster = movie.getPosterPath();
            Uri imageUri = Uri.parse(BASE_IMAGE_URI).buildUpon()
                    .appendPath(IMAGE_SIZE_PATH)
                    .appendEncodedPath(moviePoster)
                    .build();
            Log.d(TAG, imageUri.toString());

            Picasso.with(context)
                    .load(imageUri)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(imageView);
        }
    }

}



package tyomsky.com.moviedb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tyomsky.com.moviedb.model.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>{

    private Context context;
    private List<Movie> movies = new ArrayList<>();

    public MovieListAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MovieViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bindText(movies.get(position));
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public MovieViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(android.R.id.text1);
        }

        public void bindText(Movie movie) {
            textView.setText(movie.getTitle());
        }
    }

}



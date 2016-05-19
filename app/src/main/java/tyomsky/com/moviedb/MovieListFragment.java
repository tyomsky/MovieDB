package tyomsky.com.moviedb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import tyomsky.com.moviedb.model.Movie;

public class MovieListFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    private Spinner spinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_sort_by);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.sort_by_entries,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final String sortByPrefKey = getString(R.string.pref_sortBy_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int position = prefs.getInt(sortByPrefKey, 1);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putInt(sortByPrefKey, position).apply();
                String sortBy = getResources().getStringArray(R.array.sort_by_entry_values)[position];
                updateMovies(sortBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_list_fragment, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setAdapter(new MovieListAdapter(getActivity(), new ArrayList<Movie>()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int prefIndex = prefs.getInt(getString(R.string.pref_sortBy_key), 1);
        String sortBy = getResources().getStringArray(R.array.sort_by_entry_values)[prefIndex];
        updateMovies(sortBy);
        return view;
    }

    private void updateMovies(String sortBy) {
        new MoviesLoadingTask(getActivity(), recyclerView).execute(sortBy);
    }
}

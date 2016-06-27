package com.owenlarosa.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieActivityFragment extends Fragment {

    TMDBClient client = new TMDBClient();
    FetchMovieTask fetchMovieTask = new FetchMovieTask();
    MovieImageAdapter movieImageAdapter;

    DrawerLayout drawerLayout;
    ListView drawerList;
    String[] movieCategories = {
            "Favorites",
            "Popular",
            "Top Rated"
    };
    ArrayAdapter<String> drawerListAdapter;

    public MovieActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        drawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);

        drawerList = (ListView) rootView.findViewById(R.id.left_drawer);

        drawerListAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.drawer_list_item,
                R.id.drawer_list_item_textview,
                movieCategories
        );
        drawerList.setAdapter(drawerListAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("", drawerListAdapter.getItem(position));
            }
        });

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        movieImageAdapter = new MovieImageAdapter(getContext(), R.layout.movie_grid_item);
        gridView.setAdapter(movieImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: display the detail view.
                // for now, just display a toast
                Toast.makeText(getContext(), "This will launch the detail view.", Toast.LENGTH_SHORT);
            }
        });
        fetchMovieTask.execute("popular");

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            String method = params[0];
            if (params.length == 0) return null;
            return client.taskForMovieSearch(method);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);

            for (int i = 0; i < movies.length; i++) {
                Log.d("", movies[i].title);
            }

            // refresh the grid with the new data
            movieImageAdapter.clear();
            movieImageAdapter.addAll(movies);
        }
    }

}

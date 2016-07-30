package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieActivityFragment extends Fragment {

    TMDBClient client;
    FetchMovieTask fetchMovieTask = new FetchMovieTask();
    MovieImageAdapter mMovieImageAdapter;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.left_drawer) ListView drawerList;
    @BindView(R.id.gridview) GridView gridView;

    private Unbinder unbinder;

    // key to be used when saving restoring the instance
    private static final String MOVIES_KEY = "movies";

    private static final LinkedHashMap<String, String> movieCategories;
    static {
        // use static initializer as Java has no dictionary literals
        movieCategories = new LinkedHashMap<String, String>();
        movieCategories.put("Favorites", ""); // no data needed for API call
        movieCategories.put("Popular", "movie/popular");
        movieCategories.put("Top Rated", "movie/top_rated");
        movieCategories.put("Action", "28");
        movieCategories.put("Adventure", "12");
        movieCategories.put("Animation", "16");
        movieCategories.put("Comedy", "35");
        movieCategories.put("Crime", "80");
        movieCategories.put("Documentary", "99");
        movieCategories.put("Drama", "18");
        movieCategories.put("Family", "10751");
        movieCategories.put("Fantasy", "14");
        movieCategories.put("Foreign", "10769");
        movieCategories.put("Mystery", "36");
        movieCategories.put("Horror", "27");
        movieCategories.put("Music", "10402");
        movieCategories.put("Mystery", "9648");
        movieCategories.put("Romance", "10749");
        movieCategories.put("Science Fiction", "878");
        movieCategories.put("TV Movie", "10770");
        movieCategories.put("Thriller", "53");
        movieCategories.put("War", "10752");
        movieCategories.put("Western", "37");
    }

    ArrayAdapter<String> drawerListAdapter;

    public MovieActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_KEY, mMovieImageAdapter.movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        client = new TMDBClient(getContext());

        drawerListAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.drawer_list_item,
                R.id.drawer_list_item_textview,
                new ArrayList(movieCategories.keySet())
        );
        drawerList.setAdapter(drawerListAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // update the movie list based on the selection
                updateMovieList(((TextView) view.findViewById(R.id.drawer_list_item_textview)).getText().toString());
                // dismiss the drawer: http://stackoverflow.com/questions/26833741/hide-navigation-drawer-when-user-presses-back-button
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        mMovieImageAdapter = new MovieImageAdapter(getContext(), R.layout.movie_grid_item);
        gridView.setAdapter(mMovieImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: display the detail view.
                // for now, just display a toast
                //Toast.makeText(getContext(), "This will launch the detail view.", Toast.LENGTH_SHORT);

                Movie movie = (Movie) mMovieImageAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Movie.class.getSimpleName(), movie);
                startActivity(detailIntent);
            }
        });

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_KEY)) {
            fetchMovieTask.execute("movie/popular");
        } else {
            mMovieImageAdapter.movies = savedInstanceState.getParcelableArrayList("movies");
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            String method = params[0];
            String parameters = "";
            if (params.length > 1) parameters = params[1];
            if (params.length == 0) return null;
            return client.getMovies(method, parameters);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);

            // refresh the grid with the new data
            mMovieImageAdapter.clear();
            if (movies != null) {
                mMovieImageAdapter.addAll(movies);
            }
        }
    }

    private void updateMovieList(String category) {
        if (category == "Favorites") {
            // TODO: show favorite movies
            Toast.makeText(getContext(), "Favorites has not been implemented!", Toast.LENGTH_SHORT).show();
            return;
        }
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        if (category == "Popular") {
            fetchMovieTask.execute(movieCategories.get(category));
        } else if (category == "Top Rated") {
            fetchMovieTask.execute(movieCategories.get(category));
        } else {
            fetchMovieTask.execute("discover/movie", "&with_genres=" + movieCategories.get(category));
        }
    }

}

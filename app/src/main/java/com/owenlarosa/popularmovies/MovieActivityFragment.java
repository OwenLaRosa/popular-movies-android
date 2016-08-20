package com.owenlarosa.popularmovies;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.owenlarosa.popularmovies.db.DaoMaster;
import com.owenlarosa.popularmovies.db.DaoSession;
import com.owenlarosa.popularmovies.db.Movie;
import com.owenlarosa.popularmovies.db.MovieDao;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieActivityFragment extends Fragment {

    public interface Callback {
        public void onShowDetail(Movie movie);
    }

    TMDBClient client;
    MovieImageAdapter mMovieImageAdapter;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.left_drawer) ListView drawerList;
    @BindView(R.id.gridview) GridView gridView;
    @BindView(R.id.no_favorites_text_view) TextView noFavoritesTextView;

    private ActionBarDrawerToggle mDrawerToggle;

    MovieDao movieDao;

    private Unbinder unbinder;

    // key to be used when saving restoring the instance
    private static final String MOVIES_KEY = "movies";
    // key for storing whether or not the "favorites" collection is shown
    private static final String SHOWS_FAVORITES_KEY = "showsFavorites";
    // last position the grid view was scrolled
    private static final String SCROLL_POSITION_KEY = "scrollPosition";
    // used to preserve the selected position
    static int index;

    // keys to be used with SharedPreferences
    private static final String CATEGORY_INDEX_PREF_KEY = "index";

    private static final LinkedHashMap<String, String> movieCategories;
    static {
        // use static initializer as Java has no dictionary literals
        movieCategories = new LinkedHashMap<String, String>();
        movieCategories.put("★ Favorites", ""); // no data needed for API call
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

    // whether or not the grid view is showing the favorite movie list
    public boolean showsFavorites = false;

    public MovieActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MOVIES_KEY, mMovieImageAdapter.movies);
        outState.putBoolean(SHOWS_FAVORITES_KEY, showsFavorites);
        outState.putInt(SCROLL_POSITION_KEY, gridView.getFirstVisiblePosition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        client = new TMDBClient(getContext());

        // get access to database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "movies-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        DaoSession daoSession = daoMaster.newSession();
        movieDao = daoSession.getMovieDao();

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
                //gridView.setSelection(0);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(CATEGORY_INDEX_PREF_KEY, position);
                editor.commit();
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                R.drawable.menu,
                R.string.app_name,
                R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        mMovieImageAdapter = new MovieImageAdapter(getContext(), R.layout.movie_grid_item);
        gridView.setAdapter(mMovieImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // display the detail view.
                Movie movie = (Movie) mMovieImageAdapter.getItem(position);
                ((Callback) getActivity()).onShowDetail(movie);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SHOWS_FAVORITES_KEY)) {
            if (savedInstanceState.getBoolean(SHOWS_FAVORITES_KEY)) {
                showsFavorites = true;
            }
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int position = sharedPreferences.getInt(CATEGORY_INDEX_PREF_KEY, 1);
        if (position != 0) showsFavorites = false;
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_KEY)) {
            mMovieImageAdapter.setMovies((ArrayList<Movie>) savedInstanceState.getSerializable(MOVIES_KEY));
        } else {
            // load the last shown category from the preferences
            // if first launch, select Popular category
            drawerList.setSelection(position);
            drawerList.performItemClick(drawerListAdapter.getView(position, null, null),
                    position,
                    drawerListAdapter.getItemId(position));
        }

        gridView.setSelection(index);

        AppCompatActivity compatActivity = (AppCompatActivity) getActivity();
        compatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        compatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        compatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // refresh the data if it comes from the database
        if (showsFavorites) {
            updateMovieList("★ Favorites");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    // these two methods are required to toggle the navigation drawer

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

        index = gridView.getFirstVisiblePosition();
    }

    private void updateMovieList(String category) {
        noFavoritesTextView.setVisibility(View.GONE);
        if (category == "★ Favorites") {
            showsFavorites = true;
            loadFavorites();
            return;
        } else {
            showsFavorites = false;
        }

        String url = "";

        if (category == "Popular") {
            url = client.buildMovieURL(movieCategories.get(category), "");
        } else if (category == "Top Rated") {
            url = client.buildMovieURL(movieCategories.get(category), "");
        } else {
            url = client.buildMovieURL("discover/movie", "&with_genres=" + movieCategories.get(category));
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Movie[] movies = client.getMoviesFromJSON(response);
                if (movies != null) {
                    mMovieImageAdapter.clear();
                    mMovieImageAdapter.addAll(movies);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    public void loadFavorites() {

        QueryBuilder qb = movieDao.queryBuilder();
        ArrayList<Movie> favorites = new ArrayList<Movie>(qb.list());
        if (favorites.size() == 0) {
            noFavoritesTextView.setVisibility(View.VISIBLE);
        } else {
            noFavoritesTextView.setVisibility(View.GONE);
        }
        mMovieImageAdapter.setMovies(favorites);
    }

}

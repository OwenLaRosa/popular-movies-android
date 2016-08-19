package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.owenlarosa.popularmovies.db.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MovieActivity extends AppCompatActivity implements MovieActivityFragment.Callback, FavoritesDelegate {

    private boolean mTwoPane;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    ImageButton dismissDetailButton;
    @BindView(R.id.movie_detail_container) FrameLayout movieDetailContainer;
    View overlay;
    Unbinder unbinder;

    // whether or not the detail popover should be shown next creation
    private static final String SHOULD_SHOW_DETAIL_KEY = "shouldShowDetail";
    boolean shouldShowDetail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        unbinder = ButterKnife.bind(this);

        getSupportActionBar().setElevation(0);

        if (savedInstanceState != null && savedInstanceState.getBoolean(SHOULD_SHOW_DETAIL_KEY)) {
            shouldShowDetail = true;
        }

        overlay = findViewById(R.id.overlay);
        dismissDetailButton = (ImageButton) findViewById(R.id.dismiss_detail_button);
        if (findViewById(R.id.movie_detail_container) != null) {
            if (dismissDetailButton != null) {
                dismissDetailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shouldShowDetail = false;
                        setDetailVisible(false);
                    }
                });
                if (shouldShowDetail) {
                    setDetailVisible(true);
                }
            }
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SHOULD_SHOW_DETAIL_KEY, shouldShowDetail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowDetail(Movie movie) {
        if (mTwoPane) {

            DetailActivityFragment oldDetail = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (oldDetail != null && oldDetail.movie != null && oldDetail.movie.equals(movie)) {
                // layout is already shown, don't replace it
                return;
            }

            if (movieDetailContainer != null && movieDetailContainer.getVisibility() == GONE) {
                setDetailVisible(true);
            }
            // make the popover visible next time the activity is created
            shouldShowDetail = true;

            Bundle args = new Bundle();
            args.putSerializable(Movie.class.getSimpleName(), movie);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(Movie.class.getSimpleName(), movie);
            startActivity(intent);
        }
    }

    private void setDetailVisible(boolean flag) {
        int state = (flag) ? VISIBLE : GONE;
        movieDetailContainer.setVisibility(state);
        dismissDetailButton.setVisibility(state);
        overlay.setVisibility(state);
        if (!flag) {
            getSupportFragmentManager().
        }
    }

    @Override
    public void favoritesDidChange() {
        if (mTwoPane) {
            MovieActivityFragment fragment = (MovieActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if (fragment.showsFavorites) {
                fragment.loadFavorites();
            }
        }
    }
}

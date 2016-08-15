package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.owenlarosa.popularmovies.db.Movie;

public class MovieActivity extends AppCompatActivity implements MovieActivityFragment.Callback {

    private boolean mTwoPane;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        getSupportActionBar().setElevation(0);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

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
}

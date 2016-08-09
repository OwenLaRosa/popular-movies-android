package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.owenlarosa.popularmovies.db.Review;
import com.owenlarosa.popularmovies.db.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String TRAILERS_KEY = "trailers";
    private static final String REVIEWS_KEY = "reviews";

    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.year_text_view) TextView yearTextView;
    @BindView(R.id.rating_text_view) TextView ratingTextView;
    @BindView(R.id.review_linear_layout) LinearLayout reviewLinearLayout;
    @BindView(R.id.trailer_linear_layout) LinearLayout trailerLinearLayout;

    TMDBClient client;
    RequestQueue requestQueue;

    private Unbinder unbinder;

    private Movie movie;
    private MovieDao movieDao;

    private ArrayList<Trailer> displayedTrailers = new ArrayList<Trailer>();
    private ArrayList<Review> displayedReviews = new ArrayList<Review>();

    public DetailActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TRAILERS_KEY, displayedTrailers);
        outState.putSerializable(REVIEWS_KEY, displayedReviews);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        if (getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            movie = (Movie) intent.getSerializableExtra(Movie.class.getSimpleName());
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = (Movie) arguments.getSerializable(Movie.class.getSimpleName());
        }

        titleTextView.setText(movie.getTitle());
        Picasso.with(getContext()).load(movie.getFullPosterPath()).into(posterImageView);
        // get the release year, should be the first 4 characters
        String releaseYear = movie.getReleaseDate().substring(0, 4);
        yearTextView.setText(releaseYear);
        ratingTextView.setText("★ " + movie.getRating().toString() + "/10");

        int colorId;
        // change rating color based on value
        if (movie.getRating() < 2.5) {
            colorId = R.color.rating_bad;
        } else if (movie.getRating() < 5.0) {
            colorId = R.color.rating_alright;
        } else if (movie.getRating() <= 7.5) {
            colorId = R.color.rating_good;
        } else {
            colorId = R.color.rating_great;
        }
        ratingTextView.setTextColor(getResources().getColor(colorId));

        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_text_view);
        overviewTextView.setText(movie.getOverview());

        // set the title
        getActivity().setTitle("Movie Details");

        client = new TMDBClient(getContext());
        requestQueue = Volley.newRequestQueue(getActivity());

        if (savedInstanceState == null || !savedInstanceState.containsKey(TRAILERS_KEY)) {
            getTrailers();
        } else {
            displayedTrailers = (ArrayList<Trailer>) savedInstanceState.getSerializable(TRAILERS_KEY);
            displayTrailers(displayedTrailers);
        }
        if (savedInstanceState == null || !savedInstanceState.containsKey(REVIEWS_KEY)) {
            getReviews();
        } else {
            displayedReviews = (ArrayList<Review>) savedInstanceState.getSerializable(REVIEWS_KEY);
            displayReviews(displayedReviews);
        }

        // get access to database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "movies-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        DaoSession daoSession = daoMaster.newSession();
        movieDao = daoSession.getMovieDao();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.mark_favorite_button) void favoriteButtonTapped() {
        //Toast.makeText(getContext(), "Favorites has not been implemented!", Toast.LENGTH_SHORT).show();
        movieDao.insert(movie);
    }

    private void getTrailers() {
        String url = client.buildTrailerURL(movie.getIdentifier());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Trailer[] trailers = client.getTrailersFromJSON(response);
                for (int i = 0; i < trailers.length; i++) {
                    Trailer trailer = trailers[i];
                    displayedTrailers.add(i, trailer);
                }
                displayTrailers(displayedTrailers);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void getReviews() {
        Log.d("", "get reviews");
        String url = client.buildReviewURL(movie.getIdentifier());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Review[] reviews = client.getReviewsFromJSON(response);
                Log.d("", String.format("Number of reviews: %d", reviews.length));
                //movie.getReviews().clear();
                for (int i = 0; i < reviews.length; i++) {
                    Review review = reviews[i];
                    displayedReviews.add(review);
                }
                displayReviews(displayedReviews);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void displayTrailers(ArrayList<Trailer> trailers) {
        for (int i = 0; i < trailers.size(); i++) {
            Trailer trailer = trailers.get(i);
            TrailerView trailerView = new TrailerView(getContext());
            trailerView.nameTextView.setText(trailer.getName());
            trailerView.setTrailer(trailer);
            trailerLinearLayout.addView(trailerView);
        }
    }

    private void displayReviews(ArrayList<Review> reviews) {
        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            ReviewView reviewView = new ReviewView(getContext());
            reviewView.authorTextView.setText(review.getAuthor());
            // ReviewView requires setting the content with this method instead
            reviewView.setContent(review.getContent());
            reviewLinearLayout.addView(reviewView);
        }
    }

}

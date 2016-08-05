package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.owenlarosa.popularmovies.db.Movie;
import com.owenlarosa.popularmovies.db.Review;
import com.owenlarosa.popularmovies.db.Trailer;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.year_text_view) TextView yearTextView;
    @BindView(R.id.rating_text_view) TextView ratingTextView;
    @BindView(R.id.review_linear_layout) LinearLayout reviewLinearLayout;

    TMDBClient client;
    RequestQueue requestQueue;

    private Unbinder unbinder;

    private Movie movie;

    public DetailActivityFragment() {
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

        getReviews();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.mark_favorite_button) void favoriteButtonTapped() {
        Toast.makeText(getContext(), "Favorites has not been implemented!", Toast.LENGTH_SHORT).show();
    }

    private void getTrailers() {
        String url = client.buildTrailerURL(movie.getIdentifier());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Trailer[] trailers = client.getTrailersFromJSON(response);
                movie.getTrailers().clear();
                for (int i = 0; i < trailers.length; i++) {
                    movie.getTrailers().add(trailers[i]);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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
                    //movie.getReviews().add(reviews[i]);
                    ReviewView reviewView = new ReviewView(getContext());
                    reviewView.authorTextView.setText(reviews[i].getAuthor());
                    reviewView.contentTextView.setText(reviews[i].getContent());
                    reviewLinearLayout.addView(reviewView);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

}

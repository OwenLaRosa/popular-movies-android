package com.owenlarosa.popularmovies;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
            movie = (Movie) intent.getParcelableExtra(Movie.class.getSimpleName());
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(Movie.class.getSimpleName());
        }

        titleTextView.setText(movie.title);
        Picasso.with(getContext()).load(movie.getFullPosterPath()).into(posterImageView);
        // get the release year, should be the first 4 characters
        String releaseYear = movie.release_date.substring(0, 4);
        yearTextView.setText(releaseYear);
        ratingTextView.setText("★ " + movie.rating.toString() + "/10");

        int colorId;
        // change rating color based on value
        if (movie.rating < 2.5) {
            colorId = R.color.rating_bad;
        } else if (movie.rating < 5.0) {
            colorId = R.color.rating_alright;
        } else if (movie.rating <= 7.5) {
            colorId = R.color.rating_good;
        } else {
            colorId = R.color.rating_great;
        }
        ratingTextView.setTextColor(getResources().getColor(colorId));

        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_text_view);
        overviewTextView.setText(movie.overview);

        // set the title
        getActivity().setTitle("Movie Details");

        client = new TMDBClient(getContext());
        requestQueue = Volley.newRequestQueue(getActivity());

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
        String url = client.buildTrailerURL(movie.id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Trailer[] trailers = client.getTrailersFromJSON(response);
                movie.trailers.clear();
                for (int i = 0; i < trailers.length; i++) {
                    movie.trailers.add(trailers[i]);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
    }

    private void getReviews() {
        String url = client.buildReviewURL(movie.id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Review[] reviews = client.getReviewsFromJSON(response);
                movie.reviews.clear();
                for (int i = 0; i < reviews.length; i++) {
                    movie.reviews.add(reviews[i]);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
    }

}

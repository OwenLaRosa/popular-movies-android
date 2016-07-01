package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Movie movie;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        movie = (Movie) intent.getParcelableExtra(Movie.class.getSimpleName());

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
        titleTextView.setText(movie.title);
        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_image_view);
        Picasso.with(getContext()).load(movie.getFullPosterPath()).into(posterImageView);
        TextView yearTextView = (TextView) rootView.findViewById(R.id.year_text_view);
        // get the release year, should be the first 4 characters
        String releaseYear = movie.release_date.substring(0, 4);
        yearTextView.setText(releaseYear);
        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating_text_view);
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

        Button favoritesButton = (Button) rootView.findViewById(R.id.mark_favorite_button);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: add/remove the item from the favorites list
                Toast.makeText(getContext(), "Favorites has not been implemented!", Toast.LENGTH_SHORT).show();
            }
        });

        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_text_view);
        overviewTextView.setText(movie.overview);

        // set the title
        getActivity().setTitle("Movie Details");

        return rootView;
    }

}

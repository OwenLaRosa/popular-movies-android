package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        movie = (Movie) intent.getSerializableExtra(Movie.class.getSimpleName());

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
        titleTextView.setText(movie.title);
        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_image_view);
        Picasso.with(getContext()).load(movie.getFullPosterPath()).into(posterImageView);
        TextView yearTextView = (TextView) rootView.findViewById(R.id.year_text_view);
        yearTextView.setText(movie.release_date);
        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating_text_view);
        ratingTextView.setText(movie.rating.toString());
        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_text_view);
        overviewTextView.setText(movie.overview);

        return rootView;
    }
}

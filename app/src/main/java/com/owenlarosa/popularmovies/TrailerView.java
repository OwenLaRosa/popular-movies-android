package com.owenlarosa.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.owenlarosa.popularmovies.db.Trailer;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Owen LaRosa on 8/6/16.
 */

public class TrailerView extends FrameLayout {

    @BindView(R.id.trailer_thumbnail_imageview) ImageView thumbnailImageView;
    @BindView(R.id.trailer_name_textview) TextView nameTextView;

    private Trailer trailer;

    Unbinder unbinder;

    public TrailerView(Context context) {
        super(context);
        setupViews(context);
    }

    public TrailerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupViews(context);
    }

    public TrailerView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);
        setupViews(context);
    }

    private void setupViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.trailer_view, this);

        unbinder = ButterKnife.bind(this, rootView);
    }

    @OnClick(R.id.trailer_play_button)
    public void playTrailer(View view) {
        // Let the user view the video in browser or YouTube app
        // http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        String videoUrl = "https://www.youtube.com/watch?v=" + trailer.getKey();
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        getContext().startActivity(videoIntent);
    }

    /**
     * Sets the trailer associated with the view and downloads the thumbnail
     * @param trailer
     */
    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
        // get trailer thumbnail from TMDB key
        // https://discussions.udacity.com/t/getting-trailers/44997/4
        String trailerUrl = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
        Picasso.with(getContext()).load(trailerUrl).into(thumbnailImageView);
    }
}

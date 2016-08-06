package com.owenlarosa.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
        // TODO: show movie trailer intent
    }

}

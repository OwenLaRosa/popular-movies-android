package com.owenlarosa.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Owen LaRosa on 8/5/16.
 */

/**
 * Custom component to display review data (author and content)
 */
public class ReviewView extends LinearLayout {

    // referenced http://code.tutsplus.com/tutorials/creating-compound-views-on-android--cms-22889

    @BindView(R.id.review_author_textview) TextView authorTextView;
    @BindView(R.id.review_content_textview) TextView contentTextView;
    @BindView(R.id.review_expand_button) Button expandReviewButton;

    Unbinder unbinder;

    public ReviewView(Context context) {
        super(context);
        setupViews(context);
    }

    public ReviewView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupViews(context);
    }

    public ReviewView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);
        setupViews(context);
    }

    private void setupViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.review_view, this);

        unbinder = ButterKnife.bind(this, rootView);

        expandReviewButton.setText("EXPAND");
    }

    @OnClick(R.id.review_expand_button)
    public void toggleExpandedState(View view) {
        if (expandReviewButton.getText() == "EXPAND") {
            // max_value will allow the text field to expand infinitely, as needed
            contentTextView.setMaxLines(Integer.MAX_VALUE);
            expandReviewButton.setText("COLLAPSE");
        } else {
            // 4 is the default maximum number of lines of text
            contentTextView.setMaxLines(4);
            expandReviewButton.setText("EXPAND");
        }
    }

}

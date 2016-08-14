package com.owenlarosa.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
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
    /**
     * Displays text of movie review
     * WARNING: Do not set the text of this view directly. Use the setContent() method instead.
     */
    @BindView(R.id.review_content_textview) TextView contentTextView;
    @BindView(R.id.review_expand_button) ImageButton expandReviewButton;

    private static final String SUPER_STATE_KEY = "superState";
    private static final String EXPANDED_KEY = "expanded";

    boolean expanded = false;

    /**
     * The number of lines of text to be displayed when text is collapsed.
     * If the actual lines of text is less than this number, the smaller number is used
     * and the view is not expandable.
     */
    public int numLines = 4;

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

        expandReviewButton.setImageResource(R.drawable.chevron_down);
    }

    /**
     * Sets the text content of the movie review
     * @param content The full text of the review
     */
    public void setContent(String content) {
        contentTextView.setText(content);

        // getLineCount() will only work after the view renders
        // The logic must be moved here to work around this problem
        // see this SO post for details: http://stackoverflow.com/questions/12037377/how-to-get-number-of-lines-of-textview
        contentTextView.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = contentTextView.getLineCount();
                if (lineCount > numLines) {
                    // show the view and expand to appropriate size
                    expandReviewButton.setVisibility(View.VISIBLE);
                } else {
                    // hide the view and shrink it out of sight
                    expandReviewButton.setVisibility(View.GONE);
                }
                contentTextView.setMaxLines(numLines);
            }
        });
    }

    @OnClick(R.id.review_expand_button)
    public void toggleExpandedState(View view) {
        if (!expanded) {
            // max_value will allow the text field to expand infinitely, as needed
            contentTextView.setMaxLines(Integer.MAX_VALUE);
            expandReviewButton.setImageResource(R.drawable.chevron_up);
            expanded = true;
        } else {
            // 4 is the default maximum number of lines of text
            contentTextView.setMaxLines(numLines);
            expandReviewButton.setImageResource(R.drawable.chevron_down);
            expanded = false;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState());
        bundle.putBoolean(EXPANDED_KEY, expanded);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        // opposite is used because we're about to simulate state toggle
        expanded = !((Bundle) state).getBoolean(EXPANDED_KEY);
        contentTextView.post(new Runnable() {
            @Override
            public void run() {
                if (contentTextView.getLineCount() > numLines) {
                    // only relevant if the total lines exceeds the line count
                    // state will still be saved when screen returns to smaller width
                    toggleExpandedState(null);
                }
            }
        });
    }

}

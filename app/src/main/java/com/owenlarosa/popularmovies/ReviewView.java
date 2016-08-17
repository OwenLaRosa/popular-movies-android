package com.owenlarosa.popularmovies;

import android.content.Context;
import android.os.Parcel;
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

        authorTextView.generateViewId();
        contentTextView.generateViewId();
        expandReviewButton.getNextFocusRightId();
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

        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.expandedState = expanded;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        expanded = savedState.expandedState;

        contentTextView.post(new Runnable() {
            @Override
            public void run() {
                if (contentTextView.getLineCount() > numLines) {
                    // only relevant if the total lines exceeds the line count
                    // state will still be saved when screen returns to smaller width\
                    // opposite is used because we're about to simulate state toggle
                    expanded = !expanded;
                    toggleExpandedState(null);
                }
            }
        });
    }

    // Custom saved state inner class
    // referenced from: http://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes/3542895#3542895

    static class SavedState extends BaseSavedState {

        boolean expandedState = false;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.expandedState = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (expandedState ? 1 : 0));
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}

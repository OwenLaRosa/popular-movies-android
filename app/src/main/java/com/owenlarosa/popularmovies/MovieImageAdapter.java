package com.owenlarosa.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.owenlarosa.popularmovies.db.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Owen LaRosa on 6/24/16.
 */

// based off the sample from https://developer.android.com/guide/topics/ui/layout/gridview.html
// also referenced http://stacktips.com/tutorials/android/android-gridview-example-building-image-gallery-in-android

public class MovieImageAdapter extends BaseAdapter {

    static final String LOG_TAG = MovieImageAdapter.class.getSimpleName();

    private Context mContext;

    private int layoutId;

    public ArrayList<Movie> movies = new ArrayList<Movie>();

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        // when movies are assigned, inform that data has been updated
        notifyDataSetChanged();
    }

    public MovieImageAdapter(Context c, int layoutId) {
        mContext = c;
        this.layoutId = layoutId;
    }

    public int getCount() {
        return movies.size();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    public void addAll(Movie[] items) {
        movies.addAll(Arrays.asList(items));
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        ViewHolder holder = null;
        if (cell == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            cell = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(cell);
            cell.setTag(holder);
        } else {
            holder = (ViewHolder) cell.getTag();
        }
        Picasso.with(mContext).load(movies.get(position).getFullPosterPath()).into(holder.imageView);
        holder.textView.setText(movies.get(position).getTitle());
        return cell;
    }

    static class ViewHolder {
        @BindView(R.id.grid_image_view) ImageView imageView;
        @BindView(R.id.grid_text_view) TextView textView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}

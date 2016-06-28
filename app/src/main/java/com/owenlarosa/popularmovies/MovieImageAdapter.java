package com.owenlarosa.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Owen LaRosa on 6/24/16.
 */

// based off the sample from https://developer.android.com/guide/topics/ui/layout/gridview.html
// also referenced http://stacktips.com/tutorials/android/android-gridview-example-building-image-gallery-in-android

public class MovieImageAdapter extends BaseAdapter {

    static final String LOG_TAG = MovieImageAdapter.class.getSimpleName();

    private Context mContext;

    private int layoutId;

    private ArrayList<Movie> movies = new ArrayList<Movie>();

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
            Log.d("", "cell == null");
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            cell = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) cell.findViewById(R.id.grid_image_view);
            holder.textView = (TextView) cell.findViewById(R.id.grid_text_view);
            cell.setTag(holder);
        } else {
            Log.d("", "cell != null");
            holder = (ViewHolder) cell.getTag();
        }
        Picasso.with(mContext).load(movies.get(position).getFullPosterPath()).into(holder.imageView);
        holder.textView.setText(movies.get(position).title);
        return cell;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

}

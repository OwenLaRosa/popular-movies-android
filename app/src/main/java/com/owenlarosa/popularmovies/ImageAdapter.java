package com.owenlarosa.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Owen LaRosa on 6/24/16.
 */

// based off the sample from https://developer.android.com/guide/topics/ui/layout/gridview.html

public class ImageAdapter extends BaseAdapter {

    static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;

    private Integer[] mThumbIds = {};

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

}

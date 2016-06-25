package com.owenlarosa.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieActivityFragment extends Fragment {

    TMDBClient client = new TMDBClient();

    public MovieActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        final Context context = getContext();

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        ImageAdapter imageAdapter = new ImageAdapter(context);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: display the detail view.
                // for now, just display a toast
                Toast.makeText(context, "This will launch the detail view.", Toast.LENGTH_SHORT);
            }
        });
        client.getPopularMovies();

        return rootView;
    }
}

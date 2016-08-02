package com.owenlarosa.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Created by Owen LaRosa on 6/24/16.
 */

/**
 * Abstract representation for Movie data returned from TMDB
 */
public class Movie implements Parcelable {

    public Integer id;
    public String title;
    public String poster_path;
    public String release_date;
    public Double rating;
    public String overview;

    public ArrayList<Trailer> trailers = new ArrayList<Trailer>();
    public ArrayList<Review> reviews = new ArrayList<Review>();

    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";

    public Movie(Dictionary<String, Object> dictionary) {
        id = (Integer) dictionary.get(TMDBClient.KEY_ID);
        title = (String) dictionary.get(TMDBClient.KEY_TITLE);
        poster_path = (String) dictionary.get(TMDBClient.KEY_POSTER_PATH);
        release_date = (String) dictionary.get(TMDBClient.KEY_RELEASE_DATE);
        rating = (Double) dictionary.get(TMDBClient.KEY_RATING);
        overview = (String) dictionary.get(TMDBClient.KEY_OVERVIEW);
    }

    /*
    Returns the full path of the movie's poster
     */
    public String getFullPosterPath() {
        return new StringBuilder(POSTER_BASE_PATH + IMAGE_SIZE + "/")
                .append(poster_path).toString();
    }

    // Parcelable

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        rating = in.readDouble();
        overview = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(release_date);
        dest.writeDouble(rating);
        dest.writeString(overview);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}

package com.owenlarosa.popularmovies;

import java.util.Dictionary;

/**
 * Created by Owen LaRosa on 6/24/16.
 */

/**
 * Abstract representation for Movie data returned from TMDB
 */
public class Movie {

    public Integer id;
    public String title;
    public String poster_path;
    public String release_date;
    public Double rating;
    public String overview;

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
        return new StringBuilder(POSTER_BASE_PATH + "/" + IMAGE_SIZE + "/")
                .append(poster_path).toString();
    }

}

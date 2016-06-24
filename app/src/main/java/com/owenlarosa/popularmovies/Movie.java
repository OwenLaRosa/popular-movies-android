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
    public Integer rating;
    public String overview;

    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";

    public Movie(Dictionary<String, Object> dictionary) {
        id = (Integer) dictionary.get("");
        title = (String) dictionary.get("");
        poster_path = (String) dictionary.get("");
        release_date = (String) dictionary.get("");
        rating = (Integer) dictionary.get("");
        overview = (String) dictionary.get("");
    }

    /*
    Returns the full path of the movie's poster
     */
    public String getFullPosterPath() {
        return new StringBuilder(POSTER_BASE_PATH + "/" + IMAGE_SIZE + "/")
                .append(poster_path).toString();
    }

}

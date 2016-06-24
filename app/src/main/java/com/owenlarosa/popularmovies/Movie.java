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

    public Movie(Dictionary<String, Object> dictionary) {
        id = (Integer) dictionary.get("");
        title = (String) dictionary.get("");
        poster_path = (String) dictionary.get("");
        release_date = (String) dictionary.get("");
        rating = (Integer) dictionary.get("");
        overview = (String) dictionary.get("");
    }

}

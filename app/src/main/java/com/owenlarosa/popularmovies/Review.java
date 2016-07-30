package com.owenlarosa.popularmovies;

import java.util.Dictionary;

/**
 * Created by Owen LaRosa on 7/30/16.
 */

public class Review {

    public String author;
    public String content;

    public Review(Dictionary<String, Object> dictionary) {
        author = (String) dictionary.get(TMDBClient.KEY_REVIEW_AUTHOR);
        content = (String) dictionary.get(TMDBClient.KEY_REVIEW_CONTENT);
    }

}

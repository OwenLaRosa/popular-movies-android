package com.owenlarosa.popularmovies;

import java.util.Dictionary;

/**
 * Created by Owen LaRosa on 7/30/16.
 */

public class Trailer {

    public String id;
    public String key;
    public String name;

    public Trailer(Dictionary<String, Object> dictionary) {
        id = (String) dictionary.get(TMDBClient.KEY_TRAILER_ID);
        key = (String) dictionary.get(TMDBClient.KEY_TRAILER_KEY);
        name = (String) dictionary.get(TMDBClient.KEY_TRAILER_NAME);
    }

}

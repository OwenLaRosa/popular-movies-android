package com.owenlarosa.popularmovies;

import android.content.Context;
import android.util.Log;

import com.owenlarosa.popularmovies.db.Movie;
import com.owenlarosa.popularmovies.db.Review;
import com.owenlarosa.popularmovies.db.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by Owen LaRosa on 6/24/16.
 */
public class TMDBClient {

    private static final String LOG_TAG = TMDBClient.class.getSimpleName();

    // response keys for JSON parsing
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_RATING = "vote_average";
    public static final String KEY_OVERVIEW = "overview";

    public static final String KEY_TRAILER_ID = "id";
    public static final String KEY_TRAILER_KEY = "key";
    public static final String KEY_TRAILER_NAME = "name";

    public static final String KEY_REVIEW_AUTHOR = "author";
    public static final String KEY_REVIEW_CONTENT = "content";

    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    private Context mContext;

    TMDBClient(Context c) {
        mContext = c;
    }

    /**
     * Build a URL to fetch movies
     * @param method API method to be included in the URL
     * @return The complete URL
     */
    public String buildMovieURL(String method, String parameters) {
        // use a string builder because concatenated string is determined at runtime
        return new StringBuilder()
                .append(BASE_URL)
                .append(method)
                .append("?api_key=")
                .append(getApiKey())
                .append(parameters).toString();
    }

    /**
     * Build a URL to fetch movie trailers
     * @param id A TMDB movie id
     * @return The complete URL
     */
    public String buildTrailerURL(Integer id) {
        return new StringBuilder()
                .append(BASE_URL)
                .append("/movie/")
                .append(id.toString())
                .append("/videos")
                .append("?api_key=")
                .append(getApiKey()).toString();
    }

    /**
     * Build a URL to fetch movie reviews
     * @param id A TMDB movie id
     * @return The complete URL
     */
    public String buildReviewURL(Integer id) {
        return new StringBuilder()
                .append(BASE_URL)
                .append("/movie/")
                .append(id.toString())
                .append("/reviews")
                .append("?api_key=")
                .append(getApiKey()).toString();
    }

    /**
     * Create Movie objects from JSON
     * @param jsonString JSON array of movie search results
     * @return Movie instances from the JSON data
     */
    public Movie[] getMoviesFromJSON(String jsonString) {
        Movie[] movies;
        try {
            JSONObject rootObject = new JSONObject(jsonString);
            JSONArray results = rootObject.getJSONArray("results");
            movies = new Movie[results.length()];

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieData = (JSONObject) results.get(i);

                Movie movie = new Movie();
                movie.setIdentifier(movieData.getInt(KEY_ID));
                movie.setTitle(movieData.getString(KEY_TITLE));
                movie.setPosterPath(movieData.getString(KEY_POSTER_PATH));
                movie.setReleaseDate(movieData.getString(KEY_RELEASE_DATE));
                movie.setRating(movieData.getDouble(KEY_RATING));
                movie.setOverview(movieData.getString(KEY_OVERVIEW));
                movies[i] = movie;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON", e);
            return null;
        }
        return movies;
    }

    /**
     * Create Trailer objects from JSON
     * @param jsonString JSON array of trailer search results
     * @return Trailer instances from the JSON data
     */
    public Trailer[] getTrailersFromJSON(String jsonString) {
        Trailer[] trailers;
        try {
            JSONObject rootObject = new JSONObject(jsonString);
            JSONArray results = rootObject.getJSONArray("results");
            trailers = new Trailer[results.length()];
            for (int i = 0; i < results.length(); i++) {
                Dictionary properties = new Hashtable();
                JSONObject trailerData = (JSONObject) results.get(i);
                // TODO: Create trailer entity
            }
        } catch (JSONException e) {
            return null;
        }
        return trailers;
    }

    /**
     * Create Review objects from JSON
     * @param jsonString JSON array of review search results
     * @return Review instances from the JSON data
     */
    public Review[] getReviewsFromJSON(String jsonString) {
        Review[] reviews;
        try {
            JSONObject rootObject = new JSONObject(jsonString);
            JSONArray results = rootObject.getJSONArray("results");
            reviews = new Review[results.length()];
            for (int i = 0; i < results.length(); i++) {
                Dictionary properties = new Hashtable();
                JSONObject reviewData = (JSONObject) results.get(i);
                // TODO: Create review entity
            }
        } catch (JSONException e) {
            return null;
        }
        return reviews;
    }

    private String getApiKey() {
        return mContext.getResources().getString(R.string.tmdb_api_key);
    }

}

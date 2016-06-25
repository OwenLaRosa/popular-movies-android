package com.owenlarosa.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by owen on 6/24/16.
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

    private class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

        private String buildURL(String method) {
            // use a string builder because concatenated string is determined at runtime
            return new StringBuilder()
                    .append(BASE_URL)
                    .append(method)
                    .append("?api_key=")
                    .append("").toString();
        }

        private Movie[] getMoviesFromJSON(String jsonString) {
            Movie[] movies;
            try {
                JSONObject rootObject = new JSONObject(jsonString);
                JSONArray results = rootObject.getJSONArray("results");
                movies = new Movie[results.length()];

                for (int i = 0; i < results.length(); i++) {
                    Dictionary properties = new Hashtable();
                    JSONObject movieData = (JSONObject) results.get(i);
                    properties.put(KEY_ID, movieData.getInt(KEY_ID));
                    properties.put(KEY_TITLE, movieData.getString(KEY_TITLE));
                    properties.put(KEY_POSTER_PATH, movieData.getString(KEY_POSTER_PATH));
                    properties.put(KEY_RELEASE_DATE, movieData.getString(KEY_RELEASE_DATE));
                    properties.put(KEY_RATING, movieData.getDouble(KEY_RATING));
                    properties.put(KEY_OVERVIEW, movieData.getString(KEY_OVERVIEW));
                    Movie movie = new Movie(properties);
                    movies[i] = movie;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing JSON", e);
                return null;
            }
            return movies;
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            // check to see if a method name was not supplied
            if (params.length == 0) return null;

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String jsonString = null;

            try {

                // specific method to be called on the API
                String method = params[0];

                // connect to the server
                URL url = new URL(buildURL(method));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) return null;

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) stringBuffer.append(line + "\n");
                if (stringBuffer.length() == 0) return null;

                jsonString = stringBuffer.toString();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error: ", e);
                return null;
            } finally {
                if (urlConnection != null) urlConnection.disconnect();

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream: ", e);
                    }
                }
                return getMoviesFromJSON(jsonString);
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);

            Log.v(LOG_TAG, "ON POST EXECUTE");
            for (int i = 0; i < movies.length; i++) {
                Log.v(LOG_TAG, movies[i].title);
            }
        }
    }

    /*
    Initiate a movie search task based on the method name.
    Movies will be updated in the background when download is complete.
     */
    private void taskForMovieSearch(String method) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(method);
    }

    /*
    Initiate the download for movies that are "popular"
     */
    public void getPopularMovies() {
        taskForMovieSearch("popular");
    }

    /*
    Initiate the download for the "top rated" movies
     */
    public void getTopRatedMovies() {
        taskForMovieSearch("top_rated");
    }

}

package com.owenlarosa.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private class FetchMovieTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

        private String buildURL(String method) {
            // use a string builder because concatenated string is determined at runtime
            return new StringBuilder()
                    .append(BASE_URL)
                    .append("?api_key=")
                    .append("").toString();
        }

        @Override
        protected String doInBackground(String... params) {
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
                return jsonString;
            }
        }
    }

    public Movie[] getPopularMovies() {
        return null;
    }

    public Movie[] getTopRatedMovies() {
        return null;
    }

}

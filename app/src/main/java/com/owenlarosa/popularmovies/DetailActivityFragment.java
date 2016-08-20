package com.owenlarosa.popularmovies;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.owenlarosa.popularmovies.db.DaoMaster;
import com.owenlarosa.popularmovies.db.DaoSession;
import com.owenlarosa.popularmovies.db.Movie;
import com.owenlarosa.popularmovies.db.MovieDao;
import com.owenlarosa.popularmovies.db.Review;
import com.owenlarosa.popularmovies.db.ReviewDao;
import com.owenlarosa.popularmovies.db.Trailer;
import com.owenlarosa.popularmovies.db.TrailerDao;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String SHARE_HASHTAG = "#PopularMoviesApp";

    private static final String ADD_FAVORITE = "mark as favorite";
    private static final String REMOVE_FAVORITE = "remove favorite";

    private static final String TRAILERS_KEY = "trailers";
    private static final String REVIEWS_KEY = "reviews";

    @BindView(R.id.title_text_view) TextView titleTextView;
    @BindView(R.id.poster_image_view) ImageView posterImageView;
    @BindView(R.id.year_text_view) TextView yearTextView;
    @BindView(R.id.rating_text_view) TextView ratingTextView;
    @BindView(R.id.review_linear_layout) LinearLayout reviewLinearLayout;
    @BindView(R.id.trailer_linear_layout) LinearLayout trailerLinearLayout;
    @BindView(R.id.mark_favorite_button) Button markFavoriteButton;
    @BindView(R.id.no_videos_text_view) TextView noVideosTextView;
    @BindView(R.id.no_reviews_text_view) TextView noReviewsTextView;
    @BindView(R.id.videos_progress_bar) ProgressBar videosProgressBar;
    @BindView(R.id.reviews_progress_bar) ProgressBar reviewsProgressBar;

    TMDBClient client;
    RequestQueue requestQueue;

    private Unbinder unbinder;

    public Movie movie;
    private MovieDao movieDao;
    private TrailerDao trailerDao;
    private ReviewDao reviewDao;

    boolean isFavorite = false;

    private ArrayList<Trailer> displayedTrailers = new ArrayList<Trailer>();
    private ArrayList<Review> displayedReviews = new ArrayList<Review>();

    public DetailActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TRAILERS_KEY, displayedTrailers);
        outState.putSerializable(REVIEWS_KEY, displayedReviews);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);

        // get access to database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "movies-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        DaoSession daoSession = daoMaster.newSession();
        movieDao = daoSession.getMovieDao();
        trailerDao = daoSession.getTrailerDao();
        reviewDao = daoSession.getReviewDao();

        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable(Movie.class.getSimpleName());
        }
        // movie already saved, get it from the database
        Movie existingMovie = movieForIdentifier(movie.getIdentifier());
        if (existingMovie != null) {
            movie = existingMovie;
        }

        titleTextView.setText(movie.getTitle());
        Picasso.with(getContext()).load(movie.getFullPosterPath()).into(posterImageView);
        // get the release year, should be the first 4 characters
        String releaseYear = movie.getReleaseDate().substring(0, 4);
        yearTextView.setText(releaseYear);
        ratingTextView.setText("★ " + movie.getRating().toString() + "/10");

        int colorId;
        // change rating color based on value
        if (movie.getRating() < 2.5) {
            colorId = R.color.rating_bad;
        } else if (movie.getRating() < 5.0) {
            colorId = R.color.rating_alright;
        } else if (movie.getRating() <= 7.5) {
            colorId = R.color.rating_good;
        } else {
            colorId = R.color.rating_great;
        }
        ratingTextView.setTextColor(getResources().getColor(colorId));

        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_text_view);
        overviewTextView.setText(movie.getOverview());

        // set the title
        getActivity().setTitle(R.string.app_name);

        client = new TMDBClient(getContext());
        requestQueue = Volley.newRequestQueue(getActivity());

        isFavorite = isFavorite(movie);
        restoreTrailersAndReviews(savedInstanceState);

        // determine whether the favorite button should add or remove
        if (isFavorite(movie)) {
            markFavoriteButton.setText(REMOVE_FAVORITE);
        } else {
            markFavoriteButton.setText(ADD_FAVORITE);
        }
        
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            if (displayedTrailers.size() > 0) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.setType("text/plain");
                String shareString = new StringBuilder()
                        .append("https://www.youtube.com/watch?v=")
                        .append(displayedTrailers.get(0).getKey())
                        .append(" ")
                        .append(SHARE_HASHTAG).toString();
                intent.putExtra(Intent.EXTRA_TEXT, shareString);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getContext(), getString(R.string.no_videos_to_share), Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.mark_favorite_button) void favoriteButtonTapped() {
        if (markFavoriteButton.getText() == ADD_FAVORITE) {
            movieDao.insert(movie);
            // only mark as favorite when movie is in the database
            isFavorite = true;
            addTrailers(displayedTrailers);
            addReviews(displayedReviews);
            markFavoriteButton.setText(REMOVE_FAVORITE);
            markFavoriteButton.setBackgroundColor(getResources().getColor(R.color.remove_favorite_color));
        } else {
            isFavorite = false;
            movieDao.delete(movie);
            // when deleting a movie, also delete associated trailers and reviews
            for (int i = 0; i < movie.getTrailers().size(); i++) {
                Trailer trailer = movie.getTrailers().get(i);
                trailerDao.delete(trailer);
            }
            for (int i = 0; i < movie.getReviews().size(); i++) {
                Review review = movie.getReviews().get(i);
                reviewDao.delete(review);
            }
            markFavoriteButton.setText(ADD_FAVORITE);
            markFavoriteButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }
        // let the activity know that the favorites list has been updated
        if (getActivity() instanceof FavoritesDelegate) {
            FavoritesDelegate favoritesDelegate = (FavoritesDelegate) getActivity();
            favoritesDelegate.favoritesDidChange();
        }
    }

    private void getTrailers() {
        String url = client.buildTrailerURL(movie.getIdentifier());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (videosProgressBar != null) videosProgressBar.setVisibility(View.GONE);
                Trailer[] trailers = client.getTrailersFromJSON(response);
                // display message if no trailers are available
                if (trailers.length == 0) {
                    noVideosTextView.setVisibility(View.VISIBLE);
                    return;
                }
                for (int i = 0; i < trailers.length; i++) {
                    Trailer trailer = trailers[i];
                    displayedTrailers.add(i, trailer);
                }
                displayTrailers(displayedTrailers);
                movie.setHasVideos(true);
                if (isFavorite) {
                    addTrailers(displayedTrailers);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void getReviews() {
        Log.d("", "get reviews");
        String url = client.buildReviewURL(movie.getIdentifier());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (reviewsProgressBar != null) reviewsProgressBar.setVisibility(View.GONE);
                Review[] reviews = client.getReviewsFromJSON(response);
                Log.d("", String.format("Number of reviews: %d", reviews.length));
                if (reviews.length == 0) {
                    noReviewsTextView.setVisibility(View.VISIBLE);
                    return;
                }
                for (int i = 0; i < reviews.length; i++) {
                    Review review = reviews[i];
                    displayedReviews.add(review);
                }
                displayReviews(displayedReviews);
                movie.setHasReviews(true);
                if (isFavorite) {
                    addReviews(displayedReviews);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void displayTrailers(ArrayList<Trailer> trailers) {
        if (trailers.size() == 0) noVideosTextView.setVisibility(View.VISIBLE);
        for (int i = 0; i < trailers.size(); i++) {
            Trailer trailer = trailers.get(i);
            TrailerView trailerView = new TrailerView(getContext());
            trailerView.nameTextView.setText(trailer.getName());
            trailerView.setTrailer(trailer);
            trailerLinearLayout.addView(trailerView);
        }
    }

    private void displayReviews(ArrayList<Review> reviews) {
        if (reviews.size() == 0) noReviewsTextView.setVisibility(View.VISIBLE);
        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            ReviewView reviewView = new ReviewView(getContext());
            reviewView.setId(i);
            reviewView.authorTextView.setText(review.getAuthor());
            // ReviewView requires setting the content with this method instead
            reviewView.setContent(review.getContent());
            reviewLinearLayout.addView(reviewView);
        }
    }

    /**
     * Determine whether or not the movie is marked as a favorite
     * @param movie The movie to check
     * @return True if the movie is in the database, otherwise false
     */
    private boolean isFavorite(Movie movie) {
        QueryBuilder qb = movieDao.queryBuilder();
        qb.where(MovieDao.Properties.Identifier.eq(movie.getIdentifier()));
        List results = qb.list();
        return results.size() > 0;
    }

    /**
     * Get movie from the database with the given identifier
     * @param identifier Movie's TMDB identifier
     * @return Movie with corresponding identifer or null if it doesn't exist
     */
    private Movie movieForIdentifier(int identifier) {
        QueryBuilder qb = movieDao.queryBuilder();
        qb.where(MovieDao.Properties.Identifier.eq(identifier));
        List results = qb.list();
        if (results.size() > 0) {
            return (Movie) results.get(0);
        } else {
            return null;
        }
    }

    // Helper functions to add trailers and reviews to the movie

    private void addTrailers(ArrayList<Trailer> trailers) {
        for (int i = 0; i < displayedTrailers.size(); i++) {
            Trailer trailer = displayedTrailers.get(i);
            trailer.setMovieId(movie.getId());
            trailerDao.insert(trailer);
            movie.getTrailers().add(trailer);
        }
    }

    private void addReviews(ArrayList<Review> reviews) {
        for (int i = 0; i < displayedReviews.size(); i++) {
            Review review = displayedReviews.get(i);
            review.setMovieId(movie.getId());
            reviewDao.insert(review);
            movie.getReviews().add(review);
        }
    }

    private void restoreTrailersAndReviews(Bundle bundle) {
        // first get the previously displayed videos and reviews
        if (isFavorite) {
            if (movie.getHasVideos()) displayedTrailers = new ArrayList<Trailer>(movie.getTrailers());
            if (movie.getHasReviews()) displayedReviews = new ArrayList<Review>(movie.getReviews());
        }
        if (bundle != null) {
            if (bundle.containsKey(TRAILERS_KEY)) {
                displayedTrailers = (ArrayList<Trailer>) bundle.getSerializable(TRAILERS_KEY);
            }
            if (bundle.containsKey(REVIEWS_KEY)) {
                displayedReviews = (ArrayList<Review>) bundle.getSerializable(REVIEWS_KEY);
            }
        }
        // check if the movie is loaded from the database
        if (isFavorite) {
            markFavoriteButton.setBackgroundColor(getResources().getColor(R.color.remove_favorite_color));
            // download trailers and/or reviews if missing
            if (movie.getHasVideos()) {
                // hide progress bar if loading existing trailers
                videosProgressBar.setVisibility(View.GONE);
                displayTrailers(new ArrayList<Trailer>(movie.getTrailers()));
            } else {
                getTrailers();
            }
            if (movie.getHasReviews()) {
                reviewsProgressBar.setVisibility(View.GONE);
                displayReviews(new ArrayList<Review>(movie.getReviews()));
            } else {
                getReviews();
            }
        } else {
            // if it's not, see if videos and trailers are present
            markFavoriteButton.setBackgroundColor(getResources().getColor(R.color.accent));
            if (bundle != null) {
                videosProgressBar.setVisibility(View.GONE);
                reviewsProgressBar.setVisibility(View.GONE);
                displayTrailers(displayedTrailers);
                displayReviews(displayedReviews);
            } else {
                // if not, download a new set
                getTrailers();
                getReviews();
            }
        }
    }

}

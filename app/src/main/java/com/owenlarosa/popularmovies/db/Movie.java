package com.owenlarosa.popularmovies.db;

import java.io.Serializable;
import java.util.List;
import com.owenlarosa.popularmovies.db.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "MOVIE".
 */
public class Movie implements Serializable {

    private Long id;
    private Integer identifier;
    private String title;
    private String posterPath;
    private String releaseDate;
    private Double rating;
    private String overview;
    private long reviewId;
    private long trailerId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MovieDao myDao;

    private List<Review> reviews;
    private List<Trailer> trailers;

    // KEEP FIELDS - put your custom fields here

    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    // KEEP FIELDS END

    public Movie() {
    }

    public Movie(Long id) {
        this.id = id;
    }

    public Movie(Long id, Integer identifier, String title, String posterPath, String releaseDate, Double rating, String overview, long reviewId, long trailerId) {
        this.id = id;
        this.identifier = identifier;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.overview = overview;
        this.reviewId = reviewId;
        this.trailerId = trailerId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }

    public long getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(long trailerId) {
        this.trailerId = trailerId;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Review> getReviews() {
        if (reviews == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReviewDao targetDao = daoSession.getReviewDao();
            List<Review> reviewsNew = targetDao._queryMovie_Reviews(id);
            synchronized (this) {
                if(reviews == null) {
                    reviews = reviewsNew;
                }
            }
        }
        return reviews;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetReviews() {
        reviews = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Trailer> getTrailers() {
        if (trailers == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TrailerDao targetDao = daoSession.getTrailerDao();
            List<Trailer> trailersNew = targetDao._queryMovie_Trailers(id);
            synchronized (this) {
                if(trailers == null) {
                    trailers = trailersNew;
                }
            }
        }
        return trailers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTrailers() {
        trailers = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    /*
    Returns the full path of the movie's poster
     */
    public String getFullPosterPath() {
        return new StringBuilder(POSTER_BASE_PATH + IMAGE_SIZE + "/")
                .append(posterPath).toString();
    }
    // KEEP METHODS END

}
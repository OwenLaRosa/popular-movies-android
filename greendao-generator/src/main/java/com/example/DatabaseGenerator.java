package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class DatabaseGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.owenlarosa.popularmovies.db");
        schema.enableKeepSectionsByDefault();

        Entity movie = schema.addEntity("Movie");
        movie.addIdProperty().autoincrement();
        // TMDB movie id, not SQL id property
        movie.addIntProperty("identifier");
        movie.addStringProperty("title");
        movie.addStringProperty("posterPath");
        movie.addStringProperty("releaseDate");
        movie.addDoubleProperty("rating");
        movie.addStringProperty("overview");

        Entity review = schema.addEntity("Review");
        review.addIdProperty().autoincrement();
        review.addStringProperty("author");
        review.addStringProperty("content");

        Entity trailer = schema.addEntity("Trailer");
        trailer.addIdProperty().autoincrement();
        trailer.addStringProperty("identifier");
        trailer.addStringProperty("key");
        trailer.addStringProperty("name");

        Property reviewId = review.addLongProperty("movieId").notNull().getProperty();
        ToMany movieToReviews = movie.addToMany(review, reviewId);
        movieToReviews.setName("reviews");

        Property trailerId = trailer.addLongProperty("movieId").notNull().getProperty();
        ToMany movieToTrailers = movie.addToMany(trailer, trailerId);
        movieToTrailers.setName("trailers");

        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

}

package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class DatabaseGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.owenlarosa.popularmovies.db");

        Entity movie = schema.addEntity("Movie");
        movie.addIdProperty();
        // TMDB movie id, not SQL id property
        movie.addIntProperty("identifier");
        movie.addStringProperty("title");
        movie.addStringProperty("posterPath");
        movie.addStringProperty("releaseDate");
        movie.addDoubleProperty("rating");
        movie.addStringProperty("overview");

        Entity review = schema.addEntity("Review");
        review.addIdProperty();
        review.addStringProperty("author");
        review.addStringProperty("content");

        Entity trailer = schema.addEntity("Trailer");
        trailer.addIdProperty();
        trailer.addStringProperty("identifier");
        trailer.addStringProperty("key");
        trailer.addStringProperty("name");

        Property reviewId = movie.addLongProperty("reviewId").notNull().getProperty();
        ToMany movieToReviews = movie.addToMany(review, reviewId);
        movieToReviews.setName("reviews");

        Property trailerId = movie.addLongProperty("trailerId").notNull().getProperty();
        ToMany movieToTrailers = movie.addToMany(trailer, trailerId);
        movieToTrailers.setName("trailers");

        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

}

package pl.codegarden.popularmovies.api.results;

import java.util.ArrayList;

import pl.codegarden.popularmovies.models.Review;

public class ReviewResult {
    public int id;
    public int page;
    public ArrayList<Review> results;
    public int total_pages;
    public int total_results;
}

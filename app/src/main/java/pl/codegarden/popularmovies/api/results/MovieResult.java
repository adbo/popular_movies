package pl.codegarden.popularmovies.api.results;

import java.util.ArrayList;

import pl.codegarden.popularmovies.models.Movie;

public class MovieResult {
    public int page;
    public ArrayList<Movie> results;
    public int total_pages;
    public int total_results;
}

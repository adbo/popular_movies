package pl.codegarden.popularmovies.api;

import pl.codegarden.popularmovies.BuildConfig;
import pl.codegarden.popularmovies.api.results.MovieResult;
import pl.codegarden.popularmovies.api.results.ReviewResult;
import pl.codegarden.popularmovies.api.results.TrailerResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface TmdbApi {
    String BASE_URL = "http://api.themoviedb.org/3/";
    String API_KEY = BuildConfig.MovieDBAPIKey;

    @GET("movie/{sort_by}")
    Call<MovieResult> getMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResult> getTrailers(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResult> getReviews(@Path("id") String id, @Query("api_key") String apiKey);
}

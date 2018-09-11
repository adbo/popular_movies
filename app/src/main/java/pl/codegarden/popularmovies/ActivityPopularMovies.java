package pl.codegarden.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pl.codegarden.popularmovies.models.Movie;

public class ActivityPopularMovies extends AppCompatActivity implements ActivityPopularMoviesFragment.Listener {

    private static final String MOVIES_FRAGMENT_TAG = "fragment_movies";
    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "fragment_movie_details";

    public static final String MOVIE = "movie";
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);

        mTwoPane = findViewById(R.id.movie_details_container) != null;
    }

    public void onMovieSelected(Movie movie) {
        if (mTwoPane) {
            ActivityMovieDetailsFragment fragment = ActivityMovieDetailsFragment.newInstance(movie);
            replaceMovieDetailsFragment(fragment);
        } else {
            Intent detailsIntent = new Intent(this, ActivityMovieDetails.class);
            Bundle args = new Bundle();
            args.putParcelable(MOVIE, movie);
            detailsIntent.putExtras(args);
            startActivity(detailsIntent);
        }
    }

    private void replaceMovieDetailsFragment(ActivityMovieDetailsFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_details_container, fragment, MOVIE_DETAILS_FRAGMENT_TAG)
                .commit();
    }
}

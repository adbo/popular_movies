package pl.codegarden.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActivityMovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {
            Bundle args = getIntent().getExtras();
            ActivityMovieDetailsFragment df = new ActivityMovieDetailsFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, df)
                    .commit();
        }
    }
}

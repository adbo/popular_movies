package pl.codegarden.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import pl.codegarden.popularmovies.api.TmdbApi;
import pl.codegarden.popularmovies.api.results.ReviewResult;
import pl.codegarden.popularmovies.api.results.TrailerResult;
import pl.codegarden.popularmovies.db.MoviesContract;
import pl.codegarden.popularmovies.models.Movie;
import pl.codegarden.popularmovies.models.Review;
import pl.codegarden.popularmovies.models.Trailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityMovieDetailsFragment extends Fragment {

    private static final String TMDB_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String MOVIE = "movie";

    private Movie mMovie;
    private ImageView mPoster;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mRatings;
    private LinearLayout mTrailersView;
    private TextView mTrailerLabel;
    private LinearLayout mReviewsView;
    private TextView mReviewLabel;
    private ImageButton mFavorite;
    private Trailer mFirstTrailer;

    public static ActivityMovieDetailsFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);

        ActivityMovieDetailsFragment fragment = new ActivityMovieDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public ActivityMovieDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_share) {
            if(mFirstTrailer != null) {
               ShareCompat.IntentBuilder shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(mFirstTrailer.getTrailerURL().toString());
                startActivity(Intent.createChooser(
                        shareIntent.getIntent(), getString(R.string.share_title)
                ));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        //Intent intent = getActivity().getIntent();
        Bundle args = getArguments();

        if (args != null) {
            mMovie = args.getParcelable(MOVIE);
            mPoster = (ImageView) rootView.findViewById(R.id.movie_poster);
            mTitle = (TextView) rootView.findViewById(R.id.movie_title);
            mOverview = (TextView) rootView.findViewById(R.id.movie_overview);
            mReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
            mRatings = (TextView) rootView.findViewById(R.id.movie_ratings);
            mTrailersView = (LinearLayout) rootView.findViewById(R.id.movie_trailers);
            mTrailerLabel = (TextView) rootView.findViewById(R.id.movie_trailers_label);
            mReviewsView = (LinearLayout) rootView.findViewById(R.id.movie_reviews);
            mReviewLabel = (TextView) rootView.findViewById(R.id.movie_reviews_label);
            mFavorite = (ImageButton) rootView.findViewById(R.id.movie_favorite_button);

            boolean favorite = isFavorite();
            mFavorite.setSelected(favorite);
            mMovie.setFavorite(favorite);

            setupView(rootView.getContext());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TmdbApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TmdbApi service = retrofit.create(TmdbApi.class);

            getTrailers(inflater, rootView, service);
            getReviews(inflater, rootView, service);
        }

        return rootView;
    }

    private boolean isFavorite() {
        Cursor movieCursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry._ID},
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{mMovie.getId()},
                null
        );

        return movieCursor.getCount() == 1;
    }

    private void getReviews(final LayoutInflater inflater, final View rootView, TmdbApi service) {
        Call<ReviewResult> callReview = service.getReviews(mMovie.getId(), TmdbApi.API_KEY);
        callReview.enqueue(new Callback<ReviewResult>() {
            @Override
            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                ReviewResult reviewResult = response.body();
                List<Review> reviewList = reviewResult.results;

                if (reviewList.size() > 0) {
                    mReviewLabel.setVisibility(View.VISIBLE);
                }
                int i = 0;
                for (Review review : reviewList) {
                    mReviewsView.addView(getReviewView(review, inflater, i++));
                }
            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable t) {
                Log.e("TmdbApi", "" + t.getMessage());
                Toast.makeText(rootView.getContext(), "Cannot load reviews...",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTrailers(final LayoutInflater inflater, final View rootView, TmdbApi service) {
        Call<TrailerResult> call = service.getTrailers(mMovie.getId(), TmdbApi.API_KEY);
        call.enqueue(new Callback<TrailerResult>() {
            @Override
            public void onResponse(Call<TrailerResult> call, Response<TrailerResult> response) {
                TrailerResult trailerResult = response.body();
                List<Trailer> trailerList = trailerResult.results;

                if (trailerList.size() > 0) {
                    mTrailerLabel.setVisibility(View.VISIBLE);
                }
                for (Trailer trailer : trailerList) {
                    if(mFirstTrailer == null) {
                        mFirstTrailer = trailer;
                    }
                    mTrailersView.addView(getTrailerView(trailer, inflater));
                }
            }

            @Override
            public void onFailure(Call<TrailerResult> call, Throwable t) {
                Log.e("TmdbApi", "" + t.getMessage());
                Toast.makeText(rootView.getContext(), "Cannot load trailers...",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupView(Context context) {
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon().
                appendEncodedPath(mMovie.getPosterPath()).build();
        Picasso.get().load(builtUri.toString()).into(mPoster);
        mTitle.setText(mMovie.getOriginalTitle());
        mOverview.setText(mMovie.getOverview());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mRatings.setText(mMovie.getVoteAverage().toString());

        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean favorited;
                if (mMovie.isFavorite()) {
                    getActivity().getContentResolver().delete(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{mMovie.getId()}
                    );
                    favorited = false;
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
                    values.put(MoviesContract.MovieEntry.COLUMN_TITLE, mMovie.getOriginalTitle());
                    values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
                            mMovie.getPosterPath());
                    values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                            mMovie.getReleaseDate());
                    values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                            mMovie.getVoteAverage());
                    values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());

                    getActivity().getContentResolver().insert(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            values
                    );
                    favorited = true;
                }
                mMovie.setFavorite(favorited);
                mFavorite.setSelected(favorited);
            }
        });
    }

    private View getTrailerView(final Trailer trailer, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.list_item_trailer, null);
        ImageView trailerView = (ImageView) view.findViewById(R.id.trailer_imgage);
        //Picasso.with(view.getContext()).load(trailer.getTrailerImagePath()).into(trailerView);

        view.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, trailer.getTrailerURL());
                startActivity(youtubeIntent);
            }
        });

        return view;
    }

    private View getReviewView(final Review review, LayoutInflater inflater, int index) {
        final String[] stringColor = {"#ef5350", "#42a5f5", "#66bb6a"};

        View view = inflater.inflate(R.layout.list_item_review, null);
        ImageView reviewImage = (ImageView) view.findViewById(R.id.review_image);
        TextView reviewAuthor = (TextView) view.findViewById(R.id.review_author);
        TextView reviewContent = (TextView) view.findViewById(R.id.review_content);

        int color = Color.parseColor(stringColor[index % 3]);
        reviewImage.setColorFilter(color, PorterDuff.Mode.OVERLAY);
        reviewAuthor.setText(review.getAuthor());
        reviewContent.setText(review.getContent());

        return view;
    }
}

package pl.codegarden.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.codegarden.popularmovies.adapters.MoviesAdapter;
import pl.codegarden.popularmovies.api.TmdbApi;
import pl.codegarden.popularmovies.api.results.MovieResult;
import pl.codegarden.popularmovies.db.MoviesContract;
import pl.codegarden.popularmovies.models.Movie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityPopularMoviesFragment extends Fragment {

    public static final String MOVIES = "movies";

    protected MoviesAdapter moviesAdapter;
    protected GridView gridView;
    protected Listener listener;

    public interface Listener {
        void onMovieSelected(Movie movie);
    }

    public ActivityPopularMoviesFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity){
            activity=(Activity) context;
            if (!(activity instanceof Listener)) {
                throw new IllegalStateException("Activity must implement MoviesFragment.Listener.");
            }
            listener = (Listener) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES, moviesAdapter.get_list());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_popular_movies, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SubMenu submenu = menu.findItem(R.id.sort_by).getSubMenu();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortValue = sharedPref.getString(getString(R.string.sort_key), getString(R.string.sort_popular));

        if (sortValue.equals(getString(R.string.sort_popular))) {
            submenu.findItem(R.id.sort_popular).setChecked(true);
        } else if (sortValue.equals(getString(R.string.sort_top_rated))) {
            submenu.findItem(R.id.sort_top_rated).setChecked(true);
        } else if (sortValue.equals(getString(R.string.sort_now_playing))) {
            submenu.findItem(R.id.sort_now_playing).setChecked(true);
        } else if (sortValue.equals(getString(R.string.sort_upcoming))) {
            submenu.findItem(R.id.sort_upcoming).setChecked(true);
        } else if (sortValue.equals(getString(R.string.sort_favorites))) {
            submenu.findItem(R.id.sort_favorites).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortValue = sharedPref.getString(getString(R.string.sort_key), getString(R.string.sort_popular));
        String newSortValue = sortValue;

        SharedPreferences.Editor editor = sharedPref.edit();

        if (id == R.id.sort_popular) {
            newSortValue = getString(R.string.sort_popular);
            editor.putString(getString(R.string.sort_key), newSortValue);
        } else if (id == R.id.sort_top_rated) {
            newSortValue = getString(R.string.sort_top_rated);
            editor.putString(getString(R.string.sort_key), newSortValue);
        } else if (id == R.id.sort_now_playing) {
            newSortValue = getString(R.string.sort_now_playing);
            editor.putString(getString(R.string.sort_key), newSortValue);
        } else if (id == R.id.sort_upcoming) {
            newSortValue = getString(R.string.sort_upcoming);
            editor.putString(getString(R.string.sort_key), newSortValue);
        } else if (id == R.id.sort_favorites) {
            newSortValue = getString(R.string.sort_favorites);
            editor.putString(getString(R.string.sort_key), newSortValue);
        }
        editor.apply();

        if (!sortValue.equals(newSortValue)) {
            updateMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        moviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());

        gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);
        gridView.setAdapter(moviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                listener.onMovieSelected(moviesAdapter.getItem(position));
            }
        });

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES)) {
            updateMovies();
        } else {
            ArrayList<Movie> moviesList = savedInstanceState.getParcelableArrayList(MOVIES);
            moviesAdapter.clear();
            moviesAdapter.addAll(moviesList);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sorting = sharedPref.getString(getString(R.string.sort_key),
                getString(R.string.sort_popular));

        if (sorting.equals(getString(R.string.sort_favorites))) {
            updateMovies();
        }
    }

    private void updateMovies() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sorting = sharedPref.getString(getString(R.string.sort_key),
                getString(R.string.sort_popular));

        if (sorting.equals(getString(R.string.sort_favorites))) {
            fetchMoviesFromDB();
        } else {
            fetchMovies(sorting);
        }
    }

    private void fetchMoviesFromDB() {
        Cursor movieCursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        ArrayList<Movie> listMovie = new ArrayList<>();
        if (movieCursor.moveToFirst()) {
            do {
                Movie movie = new Movie(
                        movieCursor.getString(1),
                        movieCursor.getString(2),
                        movieCursor.getString(3),
                        movieCursor.getString(4),
                        movieCursor.getDouble(5),
                        movieCursor.getString(6)
                );
                listMovie.add(movie);
            } while (movieCursor.moveToNext());
        }
        moviesAdapter.clear();
        moviesAdapter.addAll(listMovie);
    }

    private void fetchMovies(String sorting) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TmdbApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TmdbApi service = retrofit.create(TmdbApi.class);

        Call<MovieResult> call = service.getMovies(sorting, TmdbApi.API_KEY);
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult movieResult = response.body();
                moviesAdapter.clear();
                moviesAdapter.addAll(movieResult.results);
                gridView.smoothScrollToPosition(0);
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Log.e("TmdbApi", t.getMessage());
                Toast.makeText(getActivity(), "Cannot load movies...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

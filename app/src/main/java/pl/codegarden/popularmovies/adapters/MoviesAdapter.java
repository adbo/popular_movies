package pl.codegarden.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pl.codegarden.popularmovies.R;
import pl.codegarden.popularmovies.models.Movie;

public class MoviesAdapter extends ArrayAdapter<Movie>{

    Context mContext;
    ArrayList<Movie> mMovies;

    public MoviesAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
        mContext = context;
        mMovies = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String TMDB_BASE_URL = "http://image.tmdb.org/t/p/w185/";

        Movie movie = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView movie_poster = (ImageView) convertView.findViewById(R.id.movie_poster);
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon().appendEncodedPath(movie.getPosterPath()).build();
        Picasso.get().load(builtUri.toString()).into(movie_poster);

        return convertView;
    }

    public ArrayList<Movie> get_list() {
        return this.mMovies;
    }
}

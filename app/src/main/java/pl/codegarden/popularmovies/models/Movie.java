package pl.codegarden.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Movie implements Parcelable {
    private String id;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("vote_average")
    private Double voteAverage;
    private String overview;
    private boolean favorite;

    public Movie(String vId, String vOriginalTitle, String vPosterPath, String vReleaseDate,
                 Double vVoteAverage, String vOverview) {
        id = vId;
        originalTitle = vOriginalTitle;
        posterPath = vPosterPath;
        releaseDate = vReleaseDate;
        voteAverage = vVoteAverage;
        overview = vOverview;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        originalTitle = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
        favorite = in.readByte() != 0;
    }

    public void setOriginalTitle(String vOriginalTitle) {
        this.originalTitle = vOriginalTitle;
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public void setPosterPath(String vPosterPath) {
        this.posterPath = vPosterPath;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOverview(String vOverview) {
        this.overview = vOverview;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String vReleaseDate) {
        this.releaseDate = vReleaseDate;
    }

    public Double getVoteAverage() {
        return this.voteAverage;
    }

    public void setVoteAverage(Double vRatings) {
        this.voteAverage = vRatings;
    }

    public String getOverview() {
        return this.overview;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(releaseDate);
        parcel.writeDouble(voteAverage);
        parcel.writeString(overview);
        parcel.writeByte(favorite ? (byte) 1 : (byte) 0);
    }
}

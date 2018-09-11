package pl.codegarden.popularmovies.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    private static final String YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_URL_SUFFIX = "0.jpg";
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch";

    private String id;
    private String key;
    private String name;

    public Trailer(String id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public Trailer(Parcel in) {
        this.id = in.readString();
        this.key = in.readString();
        this.name = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrailerImagePath() {
        Uri builtUri = Uri.parse(YOUTUBE_IMAGE_URL_PREFIX).buildUpon()
                .appendEncodedPath(this.key)
                .appendEncodedPath(YOUTUBE_IMAGE_URL_SUFFIX)
                .build();

        return builtUri.toString();
    }

    public Uri getTrailerURL() {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter("v", this.key)
                .build();

        return builtUri;
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(key);
        parcel.writeString(name);
    }
}

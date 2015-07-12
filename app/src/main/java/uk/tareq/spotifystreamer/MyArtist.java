package uk.tareq.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Tareq Fadel on 06/07/15.
 * Class defining artist details with the artist image and name.
 * Usage of Parcelable plugin in Android Studio
 * https://github.com/mcharmas/android-parcelable-intellij-plugin
 */
public class MyArtist implements Parcelable {
    public static final Parcelable.Creator<MyArtist> CREATOR = new Parcelable.Creator<MyArtist>() {
        public MyArtist createFromParcel(Parcel source) {
            return new MyArtist(source);
        }

        public MyArtist[] newArray(int size) {
            return new MyArtist[size];
        }
    };
    public String artistName;
    public String artistUrl;
    public String artistId;

    /**
     * Alternate constructor that constructs an artist instance
     */
    public MyArtist(Artist artist) {
        super();
        this.artistName = artist.name;
        this.artistUrl = "";
        this.artistId = artist.id;
        if (!artist.images.isEmpty()) {
            // Getting 2nd from last image as it is the most appropriate size of ~300x300px
            // http://stackoverflow.com/questions/687833/how-to-get-the-last-value-of-an-arraylist
            this.artistUrl = artist.images.get(artist.images.size() - 2).url;
        }
    }

    public MyArtist() {
        super();
    }

    protected MyArtist(Parcel in) {
        this.artistName = in.readString();
        this.artistUrl = in.readString();
        this.artistId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeString(this.artistUrl);
        dest.writeString(this.artistId);
    }
}

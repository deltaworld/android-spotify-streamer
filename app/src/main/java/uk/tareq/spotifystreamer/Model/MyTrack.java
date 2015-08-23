package uk.tareq.spotifystreamer.Model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Tareq Fadel on 06/07/15.
 * Class defining artist details with the artist imageUrl and name.
 */
public class MyTrack extends Track implements Parcelable {
    public static final Parcelable.Creator<MyTrack> CREATOR = new Parcelable.Creator<MyTrack>() {
        public MyTrack createFromParcel(Parcel source) {
            return new MyTrack(source);
        }

        public MyTrack[] newArray(int size) {
            return new MyTrack[size];
        }
    };
    public String trackId;
    public String trackName;
    public String trackUrl;
    public String albumName;
    public String imageUrl;


    /**
     * Alternate constructor that constructs an artist instance
     */
    public MyTrack(Track track) {
        super();
        this.trackId = track.id;
        this.trackName = track.name;
        this.trackUrl = track.preview_url;
        this.albumName = track.album.name;
        this.imageUrl = "";
        if (!track.album.images.isEmpty()) {
            this.imageUrl = track.album.images.get(track.album.images.size() - 2).url;

        }
    }


    protected MyTrack(Parcel in) {
        this.trackId = in.readString();
        this.trackName = in.readString();
        this.trackUrl = in.readString();
        this.albumName = in.readString();
        this.imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trackId);
        dest.writeString(this.trackName);
        dest.writeString(this.trackUrl);
        dest.writeString(this.albumName);
        dest.writeString(this.imageUrl);
    }
}

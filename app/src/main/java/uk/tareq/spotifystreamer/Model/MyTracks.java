package uk.tareq.spotifystreamer.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Tareq Fadel on 25/08/15.
 * Parcelable MyTracks Data Model for storing List of <MyTrack>
 */


public class MyTracks implements Parcelable {
    public static final Parcelable.Creator<MyTracks> CREATOR = new Parcelable.Creator<MyTracks>() {
        public MyTracks createFromParcel(Parcel source) {
            return new MyTracks(source);
        }

        public MyTracks[] newArray(int size) {
            return new MyTracks[size];
        }
    };
    public List<MyTrack> myTracks;

    public MyTracks() {
    }

    protected MyTracks(Parcel in) {
        this.myTracks = in.createTypedArrayList(MyTrack.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(myTracks);
    }
}
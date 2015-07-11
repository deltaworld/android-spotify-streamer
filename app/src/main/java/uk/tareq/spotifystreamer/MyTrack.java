package uk.tareq.spotifystreamer;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Tareq Fadel on 06/07/15.
 * Class defining artist details with the artist image and name.
 */
public class MyTrack extends Track {
    public String trackName;
    public String albumName;
    public String image;

    /**
     * Alternate constructor that constructs an artist instance
     */
    public MyTrack(Track track) {
        super();
        this.trackName = track.name;
        this.albumName = track.album.name;
        this.image = "";
        if (!track.album.images.isEmpty()) {
            this.image = track.album.images.get(track.album.images.size() - 2).url;
        }
    }
}

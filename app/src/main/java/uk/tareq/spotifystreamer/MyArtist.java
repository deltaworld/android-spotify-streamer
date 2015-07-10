package uk.tareq.spotifystreamer;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Tareq Fadel on 06/07/15.
 * Class defining artist details with the artist image and name.
 */
public class MyArtist extends Artist {
    public int artistImageIndex;
    public String artistName;
    public String artistImageUrl;

    /**
     * Default constructor
     */
    public MyArtist() {
        super();
    }

    /**
     * Alternate constructor that constructs an artist instance.
     *
     * @param artistImageIndex holds the name of the drawable image
     * @param artistName       Artists name
     */
    public MyArtist(int artistImageIndex, String artistName, String artistImageUrl) {
        super();
        this.artistImageIndex = artistImageIndex;
        this.artistName = artistName;
        this.artistImageUrl = artistImageUrl;
    }

}

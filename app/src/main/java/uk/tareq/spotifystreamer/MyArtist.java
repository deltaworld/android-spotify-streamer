package uk.tareq.spotifystreamer;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Tareq Fadel on 06/07/15.
 * Class defining artist details with the artist image and name.
 */
public class MyArtist extends Artist {
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
            this.artistUrl = artist.images.get(artist.images.size() - 2).url;
        }
    }
}

package uk.tareq.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.RetrofitError;

/**
 * Fragment for the Artist search and display results
 */
public class ArtistFragment extends Fragment {

    // Holds the ListView instance that derives the data from the adapter.
    private ListView mArtistListView;

    /**
     * Default constructor
     */
    public ArtistFragment() {
    }

    @Override
    /**
     * When the fragment View is created inflation required.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Dummy data
        MyArtist artist_data[] = {
                new MyArtist(R.drawable.coldplay064, "Coldplay"),
                new MyArtist(R.drawable.karaokeelite064, "Coldplay & Lele"),
                new MyArtist(R.drawable.mailbox064, "Coldplay & Rihanna"),
                new MyArtist(R.drawable.princessofchina064, "Various Artists - Coldplay Tribute")
        };

        // Create an ArtistAdapter instance
        ArtistAdapter artistAdapter = new ArtistAdapter(
                getActivity(), // uses current activity
                R.layout.list_item_artist, // Use the layout for each artist showing image and name
                artist_data); // load the data into the adapter.

        // Inflate the rootView for the fragment, which includes the ListView element.
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        // Create a View to hold the inflated artist search header
        View artistSearchView = getActivity().getLayoutInflater().inflate(R.layout.header_artist_search, null);

        // Variable to hold the inflated ListView
        mArtistListView = (ListView) rootView.findViewById(R.id.list_view_artist);

        // Add Header to the ListView
        mArtistListView.addHeaderView(artistSearchView);

        // Add Adapter (containing data) to the ListView
        mArtistListView.setAdapter(artistAdapter);

        // Start ASync Task Thread
        SearchSpotifyTask task = new SearchSpotifyTask();

        // TODO: Change Async Task to accept String queries
        task.execute();

        return rootView;
    }

    /**
     * Class definition for fetching the Artist data asynchronously on a separate thread.
     */
    public class SearchSpotifyTask extends AsyncTask<Void, Void, Void> {

        // Constant for debugging class definition
        private final String LOG_TAG = SearchSpotifyTask.class.getSimpleName();

        /**
         * The method that performs a separate network call to retrieve the artist data.
         *
         * @param strings the parameters here are set to Void as no required paramters at this stage.
         * @return will return the network result
         */
        @Override
        protected Void doInBackground(Void... strings) {

            // These two need to be declared outside the try/catch so that they can be closed
            // in the final block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the artist response in string format
            String ArtistStr;


            try {
                // Spotify Web Api Integration

                // Instance class of Spotify Api to use the service
                SpotifyApi spotifyApi = new SpotifyApi();

                // From the SpotifyApi run the getService method
                SpotifyService spotifyService = spotifyApi.getService();

                /**
                 * Get Spotify catalog information about artists that match a keyword string.
                 * String: The search query's keywords (and optional field filters and operators), for example "roadhouse+blues"
                 * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
                 */
                String artistName = "Coldplay";

                // results of search as a <Artist> objects
                ArtistsPager results = spotifyService.searchArtists(artistName);

                // Store <Artist> objects in List by .artists.items
                List<Artist> artists = results.artists.items;

                // Loop through all artists to display name and image url
                for (int i = 0; i < artists.size(); i++) {
                    String artistImageUrl;

                    Artist artist = artists.get(i);
                    // List all artist names
                    Log.i(LOG_TAG, i + " " + artist.name);
                    // TODO: use Picasso to cache images


                    try {
                        // Initialise variables
                        int smallestImage;
                        Image artistImage;

                        // Artists may have more than one image, store images in list to extract
                        List<Image> artistImages = artist.images;

                        // Some artists do not have images, use if/else to exclude image url
                        // extraction from the artists that do not have images.
                        if (artist.images.size() != 0) {
                            // get smallest size image from last image in the index
                            smallestImage = artistImages.size() - 1;

                            artistImage = artistImages.get(smallestImage);
                            artistImageUrl = artistImage.url;

                            Log.i(LOG_TAG, smallestImage + " " + artistImageUrl + " " +
                                    artistImage.width + " " + artistImage.height);
                        } else {
                            // If image is not found use an image placeholder
                            // TODO: localise image resource.
                            artistImageUrl = "http://www.londonnights.com/gfx/default/search_no_photo.png";

                            Log.i(LOG_TAG, "Artist has no image");
                            Log.i(LOG_TAG, " " + artistImageUrl + " " + 64 + " " + 64);
                        }

                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e(LOG_TAG, e.getClass().getName());
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, e.getClass().getName());
                Log.e(LOG_TAG, e.getMessage());

                // TODO: Add Toast Message to user that the search request is invalid.

            }


            return null;
        }
    }
}



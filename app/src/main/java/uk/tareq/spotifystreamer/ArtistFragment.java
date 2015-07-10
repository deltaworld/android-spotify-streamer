package uk.tareq.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ArtistAdapter mArtistAdapter;


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
                new MyArtist(R.drawable.coldplay064, "ZZZColdplay", ""),
                new MyArtist(R.drawable.karaokeelite064, "ZZZColdplay & Lele", ""),
                new MyArtist(R.drawable.mailbox064, "ZZZColdplay & Rihanna", ""),
                new MyArtist(R.drawable.princessofchina064, "ZZZVarious Artists - Coldplay Tribute", "")
        };

        ArrayList<MyArtist> artistDummyDataList = new ArrayList<>(Arrays.asList(artist_data));
        mArtistAdapter = new ArtistAdapter(getActivity(), R.layout.list_item_artist, artistDummyDataList);


        // Inflate the rootView for the fragment, which includes the ListView element.
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        // Add search Facility to EditText
        final EditText editText = (EditText) rootView.findViewById(R.id.edit_text_search_artist);

        // Create a View to hold the inflated artist search header
        View artistSearchView = getActivity().getLayoutInflater().inflate(
                R.layout.header_artist_search, null);

        // Variable to hold the inflated ListView
        mArtistListView = (ListView) rootView.findViewById(R.id.list_view_artist);

        // Add Header to the ListView
        mArtistListView.addHeaderView(artistSearchView);

        // Add Adapter (containing data) to the ListView
        mArtistListView.setAdapter(mArtistAdapter);

        // Start ASync Task Thread
        final SearchSpotifyTask task = new SearchSpotifyTask();

        // Causing BUG: #1: Error inflating class fragment, .onCreate(ArtistActivity.java:13)
/*
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = editText.getText().toString();

                    // Pass search query to AsyncTask
                    task.execute(searchQuery);

                    return true;
                }
                return false;
            }
        });
*/

        task.execute("coldplay");
        return rootView;
    }

    /**
     * Class definition for fetching the Artist data asynchronously on a separate thread.
     */
    public class SearchSpotifyTask extends AsyncTask<String, Void, List<MyArtist>> {

        // Constant for debugging class definition
        private final String LOG_TAG = SearchSpotifyTask.class.getSimpleName();

        /**
         * The method that performs a separate network call to retrieve the artist data.
         *
         * @param searchQuery the parameters here are set to Void as no required paramters at this stage.
         * @return will return the network result
         */
        @Override
        protected List<MyArtist> doInBackground(String... searchQuery) {

            // data to be returned as result
            List<MyArtist> artistData = new ArrayList<>();

            if (searchQuery == null || searchQuery[0].equals("")) {
                return null;
            } else {

                try {
                    // Spotify Web Api Integration

                    // Instance class of Spotify Api to use the service
                    SpotifyApi spotifyApi = new SpotifyApi();

                    // From the SpotifyApi run the getService method
                    SpotifyService spotifyService = spotifyApi.getService();

                    /**
                     * Get Spotify catalog information about artists that match a keyword string.
                     * String: The search query's keywords (and optional field filters and operators),
                     * for example "roadhouse+blues"
                     * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
                     */
                    String artistName = searchQuery[0];


                    // results of search as a <Artist> objects
                    ArtistsPager results = spotifyService.searchArtists(artistName);

                    // Store <Artist> objects in List by .artists.items
                    List<Artist> artists = results.artists.items;


                    // Loop through all artists to display name and image url
                    for (int i = 0; i < artists.size(); i++) {
                        String artistImageUrl = null;

                        Artist artist = artists.get(i);
                        // List all artist names
                        Log.i(LOG_TAG, i + " " + artist.name);
                        // TODO: use Picasso to cache images

                        try {
                            // Initialise variables
                            int smallestImage;
                            Image artistImage;
                            Picasso p = Picasso.with(getActivity());
                            ImageView imageView = null;

                            // Artists may have more than one image, store images in list to extract
                            List<Image> artistImages = artist.images;

                            // Some artists do not have images, use if/else to exclude image url
                            // extraction from the artists that do not have images.
                            if (artist.images.size() != 0) {

                                // get smallest size image from last image in the index
                                smallestImage = artistImages.size() - 1;

                                artistImage = artistImages.get(smallestImage);
                                artistImageUrl = artistImage.url;

                                //p.load(artistImageUrl).into(imageView);


                                Log.i(LOG_TAG, smallestImage + " " + artistImageUrl + " " +
                                        artistImage.width + " " + artistImage.height);
                            } else {
                                // If image is not found use an image placeholder
                                // TODO: localise image resource.
                                artistImageUrl = "http://www.londonnights.com/gfx/default/search_no_photo.png";

                                //p.load(artistImageUrl).into(imageView);

                                Log.i(LOG_TAG, "Artist has no image");
                                Log.i(LOG_TAG, " " + artistImageUrl + " " + 64 + " " + 64);
                            }

                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.e(LOG_TAG, e.getClass().getName());
                            Log.e(LOG_TAG, e.getMessage());
                        }
                        artistData.add(new MyArtist(0, artist.name, artistImageUrl));
                    }
                    return artistData;
                } catch (RetrofitError e) {
                    // If search Query not found then display toast
                    Log.e(LOG_TAG, e.getClass().getName());
                    Log.e(LOG_TAG, e.getMessage());
                    return null;
                    // TODO: Add Toast Message to user that the search request is invalid.

/*                  -- Toast Message --
                    Context context = getActivity();
                    CharSequence text = "Could not find artist";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
*/
                }
            }
        }

        @Override
        protected void onPostExecute(List<MyArtist> myArtists) {
            super.onPostExecute(myArtists);
            if (myArtists != null) {
                for (MyArtist a : myArtists) {
                    Log.i(LOG_TAG, "FROM POST: " + a.artistName + " " + a.artistImageUrl);
                }

                MyArtist[] array = myArtists.toArray(new MyArtist[myArtists.size()]);

                // Empty the Adapter from the dummy data
                mArtistAdapter.clear();

                // Add new data
                mArtistAdapter.addAll(array);

            }
        }
    }
}



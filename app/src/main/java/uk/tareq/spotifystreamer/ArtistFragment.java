package uk.tareq.spotifystreamer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import retrofit.RetrofitError;

/**
 * Fragment for the Artist search and display results
 */
public class ArtistFragment extends Fragment {

    private static final String LOG_TAG = ArtistFragment.class.getSimpleName();
    // Holds the ListView instance that derives the data from the adapter.
    private ListView mArtistListView;
    private ArtistAdapter mArtistAdapter;

    /**
     * Default constructor
     */
    public ArtistFragment() {
    }

    /**
     * Hides the soft keyboard when invoked
     *
     * @param activity the activity where the view has the keyboard.
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    /**
     * When the fragment View is created inflation required.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        //Initialise variables
        List<Artist> artistList = new ArrayList<>();

        // Inflate the rootView for the fragment, which includes the ListView element.
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        // Construct a new customArrayAdapter
        mArtistAdapter = new ArtistAdapter(getActivity(), R.layout.list_item_artist,
                artistList);

        // Create a View to hold the inflated artist search header
        View artistSearchView = getActivity().getLayoutInflater().inflate(
                R.layout.header_artist_search, null);

        // EditText for Artist Search
        final EditText editText = (EditText) artistSearchView.findViewById(R.id.edit_text_search_artist);

        // Variable to hold the inflated ListView
        mArtistListView = (ListView) rootView.findViewById(R.id.list_view_artist);

        // Add Header to the ListView
        mArtistListView.addHeaderView(artistSearchView);

        // Add Adapter (containing data) to the ListView
        mArtistListView.setAdapter(mArtistAdapter);

        // ActionListener for the search button on the EditText keyboard
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            String searchQuery = editText.getText().toString();

                            // Pass search query to AsyncTask
                            try {
                                if (!searchQuery.equals("")) { // make sure text is not empty

                                    editText.setText(""); // clear previous search query
                                    new SearchSpotifyTask().execute(searchQuery); // run AsyncTask
                                    hideSoftKeyboard(getActivity()); // hide keyboard
                                    return true;
                                }

                            } catch (IllegalStateException e) {
                                Log.e(LOG_TAG, e.getMessage());
                            }
                        }
                        return false;
                    }
                }
        );
        return rootView;
    }

    /**
     * Class definition for fetching the Artist data asynchronously on a separate thread.
     */
    public class SearchSpotifyTask extends AsyncTask<String, Void, List<Artist>> {

        // Constant for debugging class definition
        private final String LOG_TAG = SearchSpotifyTask.class.getSimpleName();

        /**
         * The method that performs a separate network call to retrieve the artist data.
         *
         * @param searchQuery the parameters here are set to Void as no required paramters at this stage.
         * @return will return the network result
         */
        @Override
        protected List<Artist> doInBackground(String... searchQuery) {


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

                    // results of search as  List<Artist> objects
                    return spotifyService.searchArtists(artistName).artists.items;
                } catch (RetrofitError e) {
                    // If search Query not found then display toast
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
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);
            if (artists != null) {
                // Empty the Adapter from previous search
                mArtistAdapter.clear();

                // Add new data
                mArtistAdapter.addAll(artists);
            }
        }
    }
}



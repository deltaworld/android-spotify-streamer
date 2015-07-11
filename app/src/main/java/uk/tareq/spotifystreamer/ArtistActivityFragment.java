package uk.tareq.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import retrofit.RetrofitError;

/**
 * Fragment for the Artist search and display results
 */
public class ArtistActivityFragment extends Fragment {

    private static final String LOG_TAG = ArtistActivityFragment.class.getSimpleName();

    private ArrayList<MyArtist> mArtistList = new ArrayList<>();
    // Holds the ListView instance that derives the data from the adapter.
    private ListView mArtistListView;
    private ArtistAdapter mArtistAdapter;

    /**
     * Default constructor
     */
    public ArtistActivityFragment() {
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

    /**
     * custom onSaveInstanceState, checks if the adapter is populated. If it is, creates a
     * Parcelable Array and copies all ListView items from the adapter into the Parcelable
     * @param outState Bundle that places the Parcelable array with the key
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mArtistAdapter.getCount() > 0) { // check if adapter contains items
            // Construct a new Parcelable Array the same size as Custom ArrayAdapter
            Parcelable[] parcelables = new Parcelable[mArtistAdapter.getCount()];

            // Loop through the items in the array adapter and place each into the parcelables array
            for (int i = 0; i < mArtistAdapter.getCount(); i++) {
                parcelables[i] = mArtistAdapter.getItem(i);
            }
            // put the parcelable array with the key "artist" ready for retrieval tempStorage
            outState.putParcelableArray("artist", parcelables);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Checks to see if there is a network connection
     *
     * @return boolean response to a valid network connection.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Class definition for fetching the Artist data asynchronously on a separate thread.
     */

    /**
     * When the fragment View is created inflation required.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the rootView for the fragment, which includes the ListView element.
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        // Construct a new customArrayAdapter
        mArtistAdapter = new ArtistAdapter(getActivity(), R.layout.list_item_artist, mArtistList);

        // Create a View to hold the inflated artist search header
        View artistSearchView = getActivity().getLayoutInflater().inflate(
                R.layout.header_artist_search, null);

        // EditText for Artist Search
        final EditText editText = (EditText) artistSearchView
                .findViewById(R.id.edit_text_search_artist);

        // Variable to hold the inflated ListView
        mArtistListView = (ListView) rootView.findViewById(R.id.list_view_artist);

        // Add Header to the ListView
        mArtistListView.addHeaderView(artistSearchView);

        // Add Adapter (containing data) to the ListView
        mArtistListView.setAdapter(mArtistAdapter);

        // Parcelables section
        if (savedInstanceState != null) { // check that there is a savedInstanceState

            // Get the parcelables from the savedInstanceState with the key "artist" and store it in
            // a Parcelable Array
            Parcelable[] parcelables = savedInstanceState.getParcelableArray("artist");

            if (parcelables != null) { // run if parcelables contains items
                // for each item in parcelable add it back to the custom artist array adapter
                for (Parcelable parcelable : parcelables)
                    mArtistAdapter.add(((MyArtist) parcelable));
            }
        }

        mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistId = mArtistAdapter.getItem(position - 1).artistId;

                Intent intent = new Intent(getActivity(),
                        TrackActivity.class).putExtra(Intent.EXTRA_TEXT, artistId);
                startActivity(intent);
            }
        });

        // ActionListener for the search button on the EditText keyboard
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            String searchQuery = editText.getText().toString();

                            // Pass search query to AsyncTask
                            try {
                                if (isNetworkAvailable()) {
                                    if (!searchQuery.equals("")) { // make sure text is not empty

                                        editText.setText(""); // clear previous search query
                                        // run AsyncTask
                                        mArtistAdapter.clear();
                                        new SearchSpotifyTask().execute(searchQuery);

                                        hideSoftKeyboard(getActivity()); // hide keyboard
                                        editText.clearFocus();
                                        return true;
                                    } else {
                                        Toast toast = Toast.makeText(getActivity(),
                                                "Enter an Artist name",
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                        return true;
                                    }
                                } else {
                                    Toast toast = Toast.makeText(getActivity(),
                                            "Check you have a valid network connection",
                                            Toast.LENGTH_SHORT);
                                    toast.show();
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


    public class SearchSpotifyTask extends AsyncTask<String, Void, List<MyArtist>> {

        // Constant for debugging class definition
        private final String LOG_TAG = SearchSpotifyTask.class.getSimpleName();

        /**
         * The method that performs a separate network call to retrieve the artist data.
         *
         * @param searchQuery the parameters here are set to Void as no required parameters at this
         *                    stage.
         * @return will return the network result
         */
        @Override
        protected List<MyArtist> doInBackground(String... searchQuery) {

            if (searchQuery == null || searchQuery[0].equals("")) {
                return null;
            } else try {
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
                List<Artist> spArtist = spotifyService.searchArtists(artistName).artists.items;
                List<MyArtist> myArtists = new ArrayList<>();
                for (Artist art : spArtist) {
                    myArtists.add(new MyArtist(art));
                }

                // results of search as  List<MyArtist> objects
                return myArtists;
            } catch (RetrofitError e) {
                // If search Query not found then display toast
                Log.e(LOG_TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MyArtist> artists) {
            super.onPostExecute(artists);
            if (artists != null) {
                // Empty the Adapter from previous search
                mArtistAdapter.clear();

                // Add new data
                mArtistAdapter.addAll(artists);

                if (mArtistListView.getCount() < 2) {
                    Toast toast = Toast.makeText(getActivity(),
                            "Artist Not found. Try again",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }
}



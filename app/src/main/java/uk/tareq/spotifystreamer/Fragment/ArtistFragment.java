package uk.tareq.spotifystreamer.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import retrofit.RetrofitError;
import uk.tareq.spotifystreamer.Activity.TrackActivity;
import uk.tareq.spotifystreamer.Adapter.ArtistAdapter;
import uk.tareq.spotifystreamer.Model.MyArtist;
import uk.tareq.spotifystreamer.R;
import uk.tareq.spotifystreamer.Utils;

/**
 * Fragment for the Artist search and display results
 */
public class ArtistFragment extends Fragment {

    private static final String LOG_TAG = ArtistFragment.class.getSimpleName();
    private static final String TRACKFRAGMENT = "TFTAG";
    // Progress Bar implementation
    // http://developer.android.com/reference/android/widget/ProgressBar.html
    private static final int PROGRESS = 0x1;
    private ArrayList<MyArtist> mArtistList = new ArrayList<>();
    // Holds the ListView instance that derives the data from the adapter.
    // Usage of mVariable name for non-public, non-static start with m
    // http://stackoverflow.com/questions/2092098/why-most-of-android-tutorials-variables-start-with-m
    private ListView mArtistListView;
    private ArtistAdapter mArtistAdapter;

    //***
    private ProgressBar mProgress;
    private int mProgressStatus = 0;



    /**
     * Default constructor
     */
    public ArtistFragment() {
    }

    /**
     * Hides the soft keyboard when invoked
     * http://stackoverflow.com/questions/3858362/hide-soft-keyboard
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
        super.onSaveInstanceState(outState);
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
    }

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

        //***
        mProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mProgress.setVisibility(View.INVISIBLE);

        // Create a View to hold the inflated artist search header
        View artistSearchView = getActivity().getLayoutInflater().inflate(
                R.layout.header_artist_search, null);

        final android.support.v7.widget.SearchView searchText =
                (android.support.v7.widget.SearchView)
                        artistSearchView.findViewById(R.id.search_view_search_artist);

        // Variable to hold the inflated ListView
        mArtistListView = (ListView) rootView.findViewById(R.id.list_view_artist);

        // Add Header to the ListView
        mArtistListView.addHeaderView(artistSearchView);

        // Add Adapter (containing data) to the ListView
        mArtistListView.setAdapter(mArtistAdapter);
        hideSoftKeyboard(getActivity());

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

                String artistName = mArtistAdapter.getItem(position - 1).artistName;

                boolean dualPane = getResources().getBoolean(R.bool.dual_pane);
                if (!dualPane) {
                    Intent intent = new Intent(getActivity(), TrackActivity.class);
                    intent.putExtra(Intent.EXTRA_UID, artistId);
                    intent.putExtra(Intent.EXTRA_TEXT, artistName);
                    startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("ARTIST_ID", artistId);
                    bundle.putString("ARTIST_NAME", artistName);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    Fragment trackFragment = new TrackFragment();
                    trackFragment.setArguments(bundle);
                    transaction.replace(R.id.track_container, trackFragment, TRACKFRAGMENT).commit();
                }

            }
        });

        // SearchView onClickClistener implementation.
        // https://review.udacity.com/#!/reviews/28033
        searchText.setIconifiedByDefault(false);
        searchText.setQueryHint(getResources().getString(R.string.artist_search_hint));
        searchText.setOnQueryTextListener(new android.support.v7.widget.SearchView.
                OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    SearchSpotifyTask task = new SearchSpotifyTask();
                    searchText.clearFocus();
                    task.execute(searchText.getQuery().toString());
                    //***
                    mProgress.setVisibility(View.VISIBLE);

                } else {
                    Utils.giveToastMessage(getActivity(),
                            getResources().getString(R.string.no_internet));
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return rootView;
    }

    /**
     * Class definition for fetching the Artist data asynchronously on a separate thread.
     */
    public class SearchSpotifyTask extends AsyncTask<String, Integer, List<MyArtist>> {

        // Constant for debugging class definition
        private final String LOG_TAG = SearchSpotifyTask.class.getSimpleName();

        /**
         * The method that performs a separate network call to retrieve the artist data.
         *
         * @param searchQuery the inputted search term by the user
         * @return will return the network result
         */
        @Override
        protected List<MyArtist> doInBackground(String... searchQuery) {

            SpotifyService spotifyApi;
            if (searchQuery == null || searchQuery[0].equals("")) {
                return null;
                // Spotify Web Api Integration
                // Instance class of Spotify Api to use the service
                // https://github.com/kaaes/spotify-web-api-android

            } else try {
                spotifyApi = new SpotifyApi().getService();
                /**
                 * Get Spotify catalog information about artists that match a keyword string.
                 * String: The search query's keywords (and optional field filters and operators),
                 * for example "roadhouse+blues"
                 * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
                 */
                String artistName = searchQuery[0];
                List<Artist> spArtist = spotifyApi.searchArtists(artistName).artists.items;
                List<MyArtist> myArtists = new ArrayList<>();
                for (Artist art : spArtist) {
                    myArtists.add(new MyArtist(art));
                }

                // results of search as  List<MyArtist> objects
                return myArtists;
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, e.getMessage());
                return null;
            }
        }

        //***
        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressStatus = values[0];
            mProgress.setProgress(mProgressStatus);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<MyArtist> artists) {
            super.onPostExecute(artists);
            if (artists != null) {
                // Empty the Adapter from previous search
                mArtistAdapter.clear();

                // Add new data
                mArtistAdapter.addAll(artists);

                // mArtistListView.getCount() = 1 give 0 artists (1 for the EditText Header)
                if (mArtistListView.getCount() < 2) {
                    Utils.giveToastMessage(getActivity(),
                            getResources().getString(R.string.no_artist));
                }
                //***
                mProgress.setVisibility(View.INVISIBLE);
            }
        }
    }
}
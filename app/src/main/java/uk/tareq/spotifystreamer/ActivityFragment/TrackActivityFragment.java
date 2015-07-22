package uk.tareq.spotifystreamer.ActivityFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;
import uk.tareq.spotifystreamer.Adapter.TrackAdapter;
import uk.tareq.spotifystreamer.Model.MyTrack;
import uk.tareq.spotifystreamer.MyDialogFragment;
import uk.tareq.spotifystreamer.R;
import uk.tareq.spotifystreamer.TrackPlayerFragment;
import uk.tareq.spotifystreamer.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackActivityFragment extends Fragment {

    private static final String LOG_TAG = TrackActivityFragment.class.getSimpleName();
    // Progress Bar implementation
    // http://developer.android.com/reference/android/widget/ProgressBar.html
    private static final int PROGRESS = 0x1;
    private ListView mTrackListView;
    private TrackAdapter mTrackAdapter;
    private String mArtistId;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private FragmentManager mFragmentManager = getFragmentManager();


    public TrackActivityFragment() {
    }

    /**
     * custom onSaveInstanceState, checks if the adapter is populated. If it is, creates a
     * Parcelable Array and copies all ListView items from the adapter into the Parcelable
     *
     * @param outState Bundle that places the Parcelable array with the key
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTrackAdapter.getCount() > 0) { // check if adapter contains items
            // Construct a new Parcelable Array the same size as Custom ArrayAdapter
            Parcelable[] parcelablesT = new Parcelable[mTrackAdapter.getCount()];

            // Loop through the items in the array adapter and place each into the parcelables array
            for (int i = 0; i < mTrackAdapter.getCount(); i++) {
                parcelablesT[i] = mTrackAdapter.getItem(i);
            }
            // put the parcelable array with the key "track" ready for retrieval tempStorage
            outState.putParcelableArray("track", parcelablesT);
        }
        super.onSaveInstanceState(outState);
    }

    void showDialog() {
        // Create the fragment and show it as a dialog.
        MyDialogFragment newFragment = MyDialogFragment.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }

    void showFragment(String w1, String w2) {
        TrackPlayerFragment dialog = TrackPlayerFragment.newInstance(w1, w2);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.fragment, dialog)
                .addToBackStack(null).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Get intent from ArtistActivity
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_UID)) {
            mArtistId = intent.getStringExtra(Intent.EXTRA_UID);
        }

        List<MyTrack> trackList = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        // After view has been inflated handle the MVC
        mTrackAdapter = new TrackAdapter(getActivity(), R.layout.list_item_track, trackList);
        mTrackListView = (ListView) rootView.findViewById(R.id.list_view_tracks);
        mTrackListView.setAdapter(mTrackAdapter);

        mTrackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(LOG_TAG, "TRACK is Clicked");
                //showDialog();
                showFragment("Hello", "World");
            }
        });
        mProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mProgress.setVisibility(View.INVISIBLE);

        // Parcelables section
        if (savedInstanceState != null) { // check that there is a savedInstanceState

            // Get the parcelables from the savedInstanceState with the key "track" and store it in
            // a Parcelable Array
            Parcelable[] parcelablesT = savedInstanceState.getParcelableArray("track");

            if (parcelablesT != null) { // run if parcelables contains items
                // for each item in parcelable add it back to the custom artist array adapter
                for (Parcelable parcelable : parcelablesT)
                    mTrackAdapter.add(((MyTrack) parcelable));
            }
        }

        if (Utils.isNetworkAvailable(getActivity())) {
            new Top10TrackTask().execute(mArtistId); // run AsyncTask
            mProgress.setVisibility(View.VISIBLE);
        } else {
            Utils.giveToastMessage(getActivity(), getResources().getString(R.string.no_internet));
        }
        return rootView;
    }

    public class Top10TrackTask extends AsyncTask<String, Integer, List<MyTrack>> {

        @Override
        protected List<MyTrack> doInBackground(String... artistId) {

            if (artistId == null || artistId[0].equals("")) {
                return null;
            } else try {
                // Spotify Web Api implementation
                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotifyService = spotifyApi.getService();

                // Get default country locale
                Map<String, Object> options = new HashMap<>();
                options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());

                Tracks topTracks = spotifyService.getArtistTopTrack(artistId[0], options);

                List<MyTrack> myTracks = new ArrayList<>();
                for (Track track : topTracks.tracks) {
                    myTracks.add(new MyTrack(track));
                }
                return myTracks;
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "Error here: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressStatus = values[0];
            mProgress.setProgress(mProgressStatus);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<MyTrack> tracks) {
            super.onPostExecute(tracks);
            if (tracks != null) {
                mTrackAdapter.clear();
                mTrackAdapter.addAll(tracks);
                // 1 as SearchBox is not included so if less than 1 then display message.
                if (mTrackListView.getCount() < 1) {
                    Utils.giveToastMessage(getActivity(),
                            getResources().getString(R.string.no_track));
                }
                mProgress.setVisibility(View.INVISIBLE);

            }
        }
    }
}
package uk.tareq.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackActivityFragment extends Fragment {

    private static final String LOG_TAG = TrackActivityFragment.class.getSimpleName();

    private ListView mTrackListView;
    private TrackAdapter mTrackAdapter;
    private String mArtistId;

    public TrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        List<Track> trackList = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        mTrackAdapter = new TrackAdapter(getActivity(), R.layout.list_item_track,
                trackList);

        mTrackListView = (ListView) rootView.findViewById(R.id.list_view_tracks);

        mTrackListView.setAdapter(mTrackAdapter);
        // TODO: execute AsyncTask for TrackAdapter
        new Top10TrackTask().execute(mArtistId);

        return rootView;

    }

    public class Top10TrackTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... artistId) {

            if (artistId == null || artistId[0].equals("")) {
                return null;
            } else {
                try {
                    SpotifyApi spotifyApi = new SpotifyApi();
                    SpotifyService spotifyService = spotifyApi.getService();
                    Map<String, Object> options = new HashMap<>();
                    options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
                    Tracks topTracks = spotifyService.getArtistTopTrack(artistId[0], options);

                    List<Track> trackList = new ArrayList<>();
                    for (Track track : topTracks.tracks) {
                        String trackName = track.name;
                        System.out.println(trackName);
                        trackList.add(track);
                    }

                    return trackList;
                } catch (RetrofitError e) {
                    Log.e(LOG_TAG, "Error here: " + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            super.onPostExecute(tracks);
            if (tracks != null) {
                mTrackAdapter.clear();
                mTrackAdapter.addAll(tracks);
            }
        }
    }
}

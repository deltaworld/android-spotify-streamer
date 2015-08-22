package uk.tareq.spotifystreamer.ActivityFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.tareq.spotifystreamer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        Bundle extras = getActivity().getIntent().getExtras();

        String artistId = extras.getString("EXTRA_ARTIST_ID");
        String artistName = extras.getString("EXTRA_ARTIST_NAME");
        String trackId = extras.getString("EXTRA_TRACK_ID");
        String trackName = extras.getString("EXTRA_TRACK_NAME");
        String albumName = extras.getString("EXTRA_ALBUM_NAME");
        String albumArtUrl = extras.getString("EXTRA_ALBUM_ART_URL");

        Log.i(LOG_TAG, "artistId " + artistId);
        Log.i(LOG_TAG, "artistName " + artistName);
        Log.i(LOG_TAG, "trackId " + trackId);
        Log.i(LOG_TAG, "trackName " + trackName);
        Log.i(LOG_TAG, "albumName " + albumName);
        Log.i(LOG_TAG, "albumURL " + albumArtUrl);

        TextView albumNameTextView = (TextView) rootView.findViewById(R.id.album_name_player_text_view);
        TextView artistNameTextView = (TextView) rootView.findViewById(R.id.artist_name_player_text_view);
        TextView trackNameTextView = (TextView) rootView.findViewById(R.id.track_name_player_text_view);
        ImageView albumImageView = (ImageView) rootView.findViewById(R.id.album_artwork_image_view);
        albumNameTextView.setText(albumName);
        artistNameTextView.setText(artistName);
        trackNameTextView.setText(trackName);

        // Check if Track has image
        if (!albumArtUrl.equals("")) {
            Picasso.with(getActivity()).load(albumArtUrl).into(albumImageView);
        } else {
            // Add blank imagePlaceholder to TrackHolder
            albumImageView.setImageResource(R.drawable.nophoto);
        }

        return rootView;
    }
}

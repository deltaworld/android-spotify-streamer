package uk.tareq.spotifystreamer.ActivityFragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import uk.tareq.spotifystreamer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private boolean mPlaying = false;
    private MediaPlayer mMediaPlayer = new MediaPlayer();



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
        final String trackUrl = extras.getString("EXTRA_TRACK_URL");
        String albumName = extras.getString("EXTRA_ALBUM_NAME");
        String albumArtUrl = extras.getString("EXTRA_ALBUM_ART_URL");

        Log.i(LOG_TAG, "artistId " + artistId);
        Log.i(LOG_TAG, "artistName " + artistName);
        Log.i(LOG_TAG, "trackId " + trackId);
        Log.i(LOG_TAG, "trackName " + trackName);
        Log.i(LOG_TAG, "trackUrl " + trackUrl);
        Log.i(LOG_TAG, "albumName " + albumName);
        Log.i(LOG_TAG, "albumURL " + albumArtUrl);

        // UI: static display of track info
        TextView albumNameTextView = (TextView) rootView.findViewById(R.id.album_name_player_text_view);
        TextView artistNameTextView = (TextView) rootView.findViewById(R.id.artist_name_player_text_view);
        TextView trackNameTextView = (TextView) rootView.findViewById(R.id.track_name_player_text_view);
        ImageView albumImageView = (ImageView) rootView.findViewById(R.id.album_artwork_image_view);

        // UI: Player functionality
        final ImageButton playButton = (ImageButton) rootView.findViewById(R.id.play_pause_image_button);
        ImageButton previousButton = (ImageButton) rootView.findViewById(R.id.previous_track_image_button);
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next_track_image_button);

        // UI: Static fields
        albumNameTextView.setText(albumName);
        artistNameTextView.setText(artistName);
        trackNameTextView.setText(trackName);

        // Check if Track has imageUrl - Picasso caches image from Url
        if (!albumArtUrl.equals("")) {
            Picasso.with(getActivity()).load(albumArtUrl).into(albumImageView);
        } else {
            // Add blank imagePlaceholder to TrackHolder
            albumImageView.setImageResource(R.drawable.nophoto);
        }

        try {
            mMediaPlayer.setDataSource(trackUrl);
            mMediaPlayer.prepare();
            int duration = mMediaPlayer.getDuration();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Functionality of Player: PLAY
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(LOG_TAG, "PLAY ");
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mMediaPlayer.pause();
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                }


            }
        });

        // Functionality of Player: PREVIOUS
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "PREVIOUS ");
            }
        });

        // Functionality of Player: NEXT
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "NEXT ");
            }
        });



        return rootView;
    }


}

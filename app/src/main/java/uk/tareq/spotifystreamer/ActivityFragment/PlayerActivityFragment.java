package uk.tareq.spotifystreamer.ActivityFragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import uk.tareq.spotifystreamer.MediaPlayerRegistry;
import uk.tareq.spotifystreamer.Model.MyTrack;
import uk.tareq.spotifystreamer.Model.MyTracks;
import uk.tareq.spotifystreamer.MusicService;
import uk.tareq.spotifystreamer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String TAG = PlayerActivityFragment.class.getSimpleName();
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private int mTimeElapsed = 0;
    private int mDuration;
    private SeekBar mSeekBar;
    private Handler mDurationHandler = new Handler();

    // Music Service variables
    private MusicService mMusicService;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;
    private MyTracks myTracks;

    private TextView mStartTimer;
    private ImageButton mForward;
    private ImageButton mBackward;

    // Binding connection to the MusicService
    private ServiceConnection mMusicConnection = new ServiceConnection() {
        // When the Music Service is connected to the fragment callback
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // binder instance variable, typecasting the IBinder service to MusicBinder
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            // get the Music Service through the binder
            mMusicService = binder.getService();
            // pass the TrackList to the service
            mMusicService.setTracks((ArrayList<MyTrack>) myTracks.myTracks);
            // Flag for connection is ON
            mMusicBound = true;
        }

        // When the Music Service is disconnected to the fragment callback
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Flag for connection is OFF
            mMusicBound = false;
        }
    };

    // http://examples.javacodegeeks.com/android/android-mediaplayer-example/
    // handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            try {
                mTimeElapsed = mMediaPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }

            //set seekbar progress
            mSeekBar.setProgress(mTimeElapsed);

            mStartTimer.setText(String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) mTimeElapsed),
                    TimeUnit.MILLISECONDS.toSeconds((long) mTimeElapsed) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    mTimeElapsed))));

            //repeat yourself that again in 100 miliseconds
            mDurationHandler.postDelayed(this, 100);
        }
    };

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        Intent intent = getActivity().getIntent();
        // Get Extras from Intent
        if (intent != null && intent.hasExtra("EXTRA_MYTRACKS")) {
            Bundle extras = intent.getExtras();

            // Extras objects in vars
            String artistName = extras.getString("EXTRA_ARTIST_NAME");
            int position = extras.getInt("EXTRA_POSITION");
            myTracks = extras.getParcelable("EXTRA_MYTRACKS");

            String trackName = myTracks.myTracks.get(position).trackName;
            final String trackUrl = myTracks.myTracks.get(position).trackUrl;
            String albumName = myTracks.myTracks.get(position).albumName;
            String albumArtUrl = myTracks.myTracks.get(position).imageUrl;


            // UI: static display of track info
            TextView albumNameTextView = (TextView) rootView.findViewById(
                    R.id.album_name_player_text_view);
            TextView artistNameTextView = (TextView) rootView.findViewById(
                    R.id.artist_name_player_text_view);
            TextView trackNameTextView = (TextView) rootView.findViewById(
                    R.id.track_name_player_text_view);
            ImageView albumImageView = (ImageView) rootView.findViewById(
                    R.id.album_artwork_image_view);

            // UI: Player functionality
            final ImageButton playButton = (ImageButton) rootView.findViewById(
                    R.id.play_pause_image_button);
            final ImageButton previousButton = (ImageButton) rootView.findViewById(
                    R.id.previous_track_image_button);
            final ImageButton nextButton = (ImageButton) rootView.findViewById(
                    R.id.next_track_image_button);
            mSeekBar = (SeekBar) rootView.findViewById(R.id.scrub_seek_bar);
            mStartTimer = (TextView) rootView.findViewById(R.id.start_timer_text_view);

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
                mDuration = mMediaPlayer.getDuration();
                mSeekBar.setMax(mDuration);


            } catch (IOException e) {
                e.printStackTrace();
            }
            // Functionality of Player: PLAY
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "PLAY ");
                    if (!mMediaPlayer.isPlaying()) {

                        for (MediaPlayer player : MediaPlayerRegistry.mList) {
                            if (player != null && player.isPlaying()) {
                                player.release();
                            }
                        }
                        MediaPlayerRegistry.mList.add(mMediaPlayer);
                        mMediaPlayer.start();
                        playButton.setImageResource(android.R.drawable.ic_media_pause);
                        mDurationHandler.postDelayed(updateSeekBarTime, 100);
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
                    Log.i(TAG, "PREVIOUS ");
                }
            });

            // Functionality of Player: NEXT
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "NEXT ");
                }
            });

            // http://stackoverflow.com/questions/17168215/seekbar-and-media-player-in-android
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mMediaPlayer != null && fromUser) {
                        mMediaPlayer.seekTo(progress);
                    }
                }
            });
        }

        return rootView;
    }

    /**
     * Created on Fragment Lifecycle after onCreateView.
     * Binding the MusicService to the ActivityFragemnt
     *
     * @param savedInstanceState used for the saving of instance state on rotation or recreation of
     *                           fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if there is no intent then
        if (mPlayIntent == null) {
            Activity playerActivity = getActivity();
            // Create a new Intent on the base Activity and attach the MusicService Class
            mPlayIntent = new Intent(playerActivity, MusicService.class);
            // Bind the defined Intent with the MusicConnection for the TrackLIst
            playerActivity.bindService(mPlayIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
            // Start the service after binding it
            playerActivity.startService(mPlayIntent);
        }
    }
}

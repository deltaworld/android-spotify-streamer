package uk.tareq.spotifystreamer.Fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import uk.tareq.spotifystreamer.Model.MyTrack;
import uk.tareq.spotifystreamer.Model.MyTracks;
import uk.tareq.spotifystreamer.MusicService;
import uk.tareq.spotifystreamer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    private static final String TAG = PlayerFragment.class.getSimpleName();

    // UI: member var
    private View mRootView;
    private TrackPlayerHolder mTrackPlayerHolder = new TrackPlayerHolder();

    // SeekBar member var
    private SeekBar mSeekBar;
    private Handler mDurationHandler = new Handler();
    private TextView mStartTimer;
    private int mTimeElapsed = 0;
    private int mDuration = 30041;

    // Music Service variables
    private MusicService mMusicService;
    private boolean mMusicBound = false;
    private Intent mPlayIntent;
    private MyTracks myTracks;
    private MyTrack mCurrTrack;
    private int mPosition = 0;
    // http://examples.javacodegeeks.com/android/android-mediaplayer-example/
    // handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            try {
                mTimeElapsed = mMusicService.getCurrentPosition();
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMusicService != null || mSeekBar.getProgress() > 0) {

            outState.putInt("playPosition", mSeekBar.getProgress());
            Log.i(TAG, "onSaveInstanceState " + String.valueOf(mSeekBar.getProgress()));

        }
    }

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
            // TEST play TRACK
            playTrack(mRootView, mPosition);
            //Log.i(TAG, "onServiceConnected " + String.valueOf(mMusicService.getDuration()));
        }

        // When the Music Service is disconnected to the fragment callback
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Flag for connection is OFF
            mMusicBound = false;
        }
    };

    public PlayerFragment() {
        setRetainInstance(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_player, container, false);
        Intent intent = getActivity().getIntent();
        // Get Extras from Intent
        if (intent != null && intent.hasExtra("EXTRA_MYTRACKS")) {
            Bundle extras = intent.getExtras();

            // Extras objects in vars
            String artistName = extras.getString("EXTRA_ARTIST_NAME");
            mPosition = extras.getInt("EXTRA_POSITION");
            myTracks = extras.getParcelable("EXTRA_MYTRACKS");
            mCurrTrack = myTracks.myTracks.get(mPosition);

            // UI: static display of track info
            TextView artistNameTextView = (TextView) mRootView.findViewById(
                    R.id.artist_name_player_text_view);
            mTrackPlayerHolder.albumName = (TextView) mRootView.findViewById(
                    R.id.album_name_player_text_view);
            mTrackPlayerHolder.trackName = (TextView) mRootView.findViewById(
                    R.id.track_name_player_text_view);
            mTrackPlayerHolder.image = (ImageView) mRootView.findViewById(
                    R.id.album_artwork_image_view);

            // UI: Player functionality
            final ImageButton playButton = (ImageButton) mRootView.findViewById(
                    R.id.play_pause_image_button);
            final ImageButton previousButton = (ImageButton) mRootView.findViewById(
                    R.id.previous_track_image_button);
            final ImageButton nextButton = (ImageButton) mRootView.findViewById(
                    R.id.next_track_image_button);

            //UI: SeekBar
            mSeekBar = (SeekBar) mRootView.findViewById(R.id.scrub_seek_bar);
            mStartTimer = (TextView) mRootView.findViewById(R.id.start_timer_text_view);
            mSeekBar.setMax(mDuration);

            // Set Artist Name and update UI with track info
            artistNameTextView.setText(artistName);
            updateTrackUi(mRootView);

            // PlayButton
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mMusicService.isPlaying()) {
                        playButton.setImageResource(android.R.drawable.ic_media_play);
                        playTrack(v, mPosition);
                    } else {
                        playButton.setImageResource(android.R.drawable.ic_media_pause);
                        mMusicService.pauseTrack();
                    }
                }
            });

            // Functionality of Player: PREVIOUS
            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "PREVIOUS ");
                    playPreviousTrack(v);
                }
            });

            // Functionality of Player: NEXT
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "NEXT ");
                    playNextTrack(v);
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
                    if (mMusicService != null && fromUser) {
                        mMusicService.seekTo(progress);
                    }
                }
            });
        }
        return mRootView;
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
            // Start the service after binding it
            playerActivity.startService(mPlayIntent);
            // Bind the defined Intent with the MusicConnection for the TrackLIst
            if (!mMusicBound) {
                playerActivity.bindService(mPlayIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
                mMusicBound = true;
            }

        }

        if (savedInstanceState != null && savedInstanceState.containsKey("playPosition")) {
            int playPosition = savedInstanceState.getInt("playPosition");
            Log.i(TAG, "onCreate " + String.valueOf(playPosition));
            mSeekBar.setProgress(playPosition);
            mMusicService.seekTo(playPosition);
            playTrack(mRootView, playPosition);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicBound) {
            try {
                getActivity().unbindService(mMusicConnection);
                mMusicBound = false;
            } catch (java.lang.IllegalArgumentException e) {
                Log.e(TAG, "onDestroy IllegalArgumentException" + e.getMessage());
            }
        }


    }


    public void playTrack(View view, int position) {
        mMusicService.setTrack(position);
        mMusicService.playTrack();

        // Seekbar
        mTimeElapsed = mMusicService.getCurrentPosition();
        mSeekBar.setProgress(mTimeElapsed);
        mDurationHandler.postDelayed(updateSeekBarTime, 100);
    }

    public void playNextTrack(View v) {
        mPosition++;
        // Loop to beginning.
        if (mPosition >= myTracks.myTracks.size()) mPosition = 0;
        mCurrTrack = myTracks.myTracks.get(mPosition);
        mMusicService.playNext();
        updateTrackUi(v);

    }

    public void playPreviousTrack(View v) {
        mPosition--;
        // Loop to end.
        if (mPosition < 0) mPosition = myTracks.myTracks.size() - 1;
        mCurrTrack = myTracks.myTracks.get(mPosition);
        mMusicService.playPrevious();
        updateTrackUi(v);

    }

    public void updateTrackUi(View view) {
        String trackName = mCurrTrack.trackName;
        String albumName = mCurrTrack.albumName;
        String albumArtUrl = mCurrTrack.imageUrl;

        // UI: Static fields
        mTrackPlayerHolder.albumName.setText(albumName);
        mTrackPlayerHolder.trackName.setText(trackName);

        // Check if Track has imageUrl - Picasso caches image from Url
        if (!albumArtUrl.equals("")) {
            Picasso.with(getActivity()).load(albumArtUrl).into(mTrackPlayerHolder.image);
        } else {
            // Add blank imagePlaceholder to TrackHolder
            mTrackPlayerHolder.image.setImageResource(R.drawable.nophoto);
        }
    }

    static class TrackPlayerHolder {
        ImageView image;
        TextView trackName;
        TextView albumName;
    }
}

package uk.tareq.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import uk.tareq.spotifystreamer.Model.MyTrack;

/**
 * Created by Tareq Fadel on 23/08/15.
 * Service Worker for handling of the music player
 * http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private final String TAG = getClass().getSimpleName();

    // instance var for the inner MusicBinder Class
    private final IBinder musicBind = new MusicBinder();
    // Media Player
    private MediaPlayer mMediaPlayer;
    // Track List
    private ArrayList<MyTrack> mTracks;
    // current Position
    private int mTrackPosition;

    private int mDuration;

    private boolean mPaused = false;


    /**
     * Setter for mTracks a list, an ArrayList of <MyTrack> for storing the details of the track
     *
     * @param tracks is the list of tracks object
     */
    public void setTracks(ArrayList<MyTrack> tracks) {
        mTracks = tracks;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // initialise the position
        mTrackPosition = 0;
        // create the player
        mMediaPlayer = new MediaPlayer();

        // initialise the MusicPlayer with the appropriate settings
        initMusicPlayer();
    }


    /**
     * Initialise the music player
     */
    public void initMusicPlayer() {
        // set player properties
        // The wake lock will let playback continue when the device becomes idle
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        // Set the Media Player to type Stream Music
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // EventListener for when media is prepared  callback: onPrepared()
        mMediaPlayer.setOnPreparedListener(this);
        // EventListener for when music finished callback: onCompletion()
        mMediaPlayer.setOnCompletionListener(this);
        // EventListener for when music playback produces an error callback: onError()
        mMediaPlayer.setOnErrorListener(this);
    }

    /**
     * Callback when media has been prepared prior to playback
     * Once the media has finished preparing, playback commences
     *
     * @param mp MediaPlayer object
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // start playback
        mp.start();
        mDuration = mp.getDuration();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }
    /**
     * Play selected track using Music Service
     */
    public void playTrack() {
        if (!mPaused) {
            // Reset the media player prior to starting a new track
            mMediaPlayer.reset();

            // get selected track from the track list
            MyTrack track = mTracks.get(mTrackPosition);

            // URL of media stream of selected track
            String trackUrl = track.trackUrl;

            // pass the trackURL to the mediaPlayer and surround with try/catch in case of errors
            try {
                mMediaPlayer.setDataSource(trackUrl);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error setting data source", e);
            }
            // Prepare the media for Aysync playback for stream -> onPrepared() callBack
            mMediaPlayer.prepareAsync();
        } else {
            mMediaPlayer.start();
        }
    }

    public void playNext() {
        // Reset the media player prior to starting a new track
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mTrackPosition++;

        // Loop to beginning.
        if (mTrackPosition >= mTracks.size()) mTrackPosition = 0;

        // get selected track from the track list
        MyTrack track = mTracks.get(mTrackPosition);

        // URL of media stream of selected track
        String trackUrl = track.trackUrl;

        // pass the trackURL to the mediaPlayer and surround with try/catch in case of errors
        try {
            mMediaPlayer.setDataSource(trackUrl);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error setting data source", e);
        }
        // Prepare the media for Aysync playback for stream -> onPrepared() callBack
        mMediaPlayer.prepareAsync();
        //} else {
        //    mMediaPlayer.start();
        //}
    }

    public void playPrevious() {
        // Reset the media player prior to starting a new track
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mTrackPosition--;

        // Loop to beginning.
        if (mTrackPosition < 0) mTrackPosition = mTracks.size() - 1;

        // get selected track from the track list
        MyTrack track = mTracks.get(mTrackPosition);

        // URL of media stream of selected track
        String trackUrl = track.trackUrl;

        // pass the trackURL to the mediaPlayer and surround with try/catch in case of errors
        try {
            mMediaPlayer.setDataSource(trackUrl);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error setting data source", e);
        }
        // Prepare the media for Aysync playback for stream -> onPrepared() callBack
        mMediaPlayer.prepareAsync();
        //} else {
        //    mMediaPlayer.start();
        //}
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }


    public void pauseTrack() {
        mMediaPlayer.pause();
        mPaused = true;
    }

    /**
     * User defined method helper for track selection. Will be used for Next and Previous tracks
     *
     * @param trackIndex track index of the track to be played back by media player
     */
    public void setTrack(int trackIndex) {
        mTrackPosition = trackIndex;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // return the MusicBinder innerclass Instance on connection
        return musicBind;
    }

    /**
     * When Service is disconnected and unbound from activity the media player is stopped & released
     *
     * @param intent containing the Service connection
     * @return return false on completion of onUnbind
     */
    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        //mMediaPlayer.release();
        return false;
    }

    public void seekTo(int progress) {
        mMediaPlayer.seekTo(progress);
    }

    /**
     * Class for handling the binding of the Service to the Fragment. This forms the interaction
     * between the Fragment and the Service classes. Here is a Binder instance
     */
    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }
}

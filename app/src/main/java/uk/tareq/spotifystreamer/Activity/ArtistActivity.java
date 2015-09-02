package uk.tareq.spotifystreamer.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import uk.tareq.spotifystreamer.Fragment.TrackFragment;
import uk.tareq.spotifystreamer.R;

// Usage of AppCompatActivity for SDK 22.1.0 instead of ActionBarActivity
// http://stackoverflow.com/questions/29890530/actionbaractivity-is-deprecated-android-studio
public class ArtistActivity extends AppCompatActivity {

    private static final String TRACKFRAGMENT = "TFTAG";
    private boolean mTwoPane = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        boolean dualPane = getResources().getBoolean(R.bool.dual_pane);
        Log.i(TRACKFRAGMENT, "onCreateDualPane " + String.valueOf(dualPane));

        if (findViewById(R.id.track_container) != null) {
            // Two pane view for sw600dp layout
            mTwoPane = true;


            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.track_container, new TrackFragment(), TRACKFRAGMENT)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}

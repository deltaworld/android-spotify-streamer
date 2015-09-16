package uk.tareq.spotifystreamer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import uk.tareq.spotifystreamer.MusicService;
import uk.tareq.spotifystreamer.R;
import uk.tareq.spotifystreamer.ServiceTools;


public class TrackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        // Use getSupportActionBar().setSubtitle
        // https://discussions.udacity.com/t/getactionbar-returns-null/22885
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            Intent intent = this.getIntent();
            String artistName = intent.getStringExtra(Intent.EXTRA_TEXT);
            ab.setSubtitle(artistName);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle presses on the action bar items
        switch (id) {
            case R.id.now_playing:
                // Check to see if Service is running
                System.out.println(nowPlaying());
                // TODO: 04/09/15 If it is running then take it back to fragment in backstack 
                return true;
            default:
                //noinspection SimplifiableIfStatement
                return super.onOptionsItemSelected(item);
        }


    }

    private boolean nowPlaying() {
        return ServiceTools.isServiceRunning(MusicService.class.getName(), getApplicationContext());
    }
}

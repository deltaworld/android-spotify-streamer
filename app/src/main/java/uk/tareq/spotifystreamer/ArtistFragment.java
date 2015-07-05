package uk.tareq.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment {

    ArrayAdapter<String> artistAdapter;

    public ArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Dummy Data for the listView
        String[] data = {
                "Coldplay",
                "Coldplay & Lele",
                "Coldplay & Rihanna",
                "Various Artists - Coldplay Tribute"
        };

        List<String> artistsSearchResults = new ArrayList<>(Arrays.asList(data));

        // Create an ArrayAdapter
        artistAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_artist,
                R.id.list_item_artist_textview
        );


        return inflater.inflate(R.layout.fragment_artist, container, false);
    }


}

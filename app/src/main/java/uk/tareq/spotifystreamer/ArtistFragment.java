package uk.tareq.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Fragment for the Artist search and display results
 */
public class ArtistFragment extends Fragment {

    // Holds the ListView instance that derives the data from the adapter.
    private ListView mArtistListView;

    /**
     * Default constructor
     */
    public ArtistFragment() {
    }

    @Override
    /**
     * When the fragment View is created inflation required.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Dummy data
        MyArtist artist_data[] = {
                new MyArtist(R.drawable.coldplay064, "Coldplay"),
                new MyArtist(R.drawable.karaokeelite064, "Coldplay & Lele"),
                new MyArtist(R.drawable.mailbox064, "Coldplay & Rihanna"),
                new MyArtist(R.drawable.princessofchina064, "Various Artists - Coldplay Tribute")
        };

        // Create an ArtistAdapter instance
        ArtistAdapter artistAdapter = new ArtistAdapter(
                getActivity(), // uses current activity
                R.layout.list_item_artist, // Use the layout for each artist showing image and name
                artist_data); // load the data into the adapter.

        // Inflate the rootView for the fragment, which includes the ListView element.
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        // Create a View to hold the inflated artist search header
        View artistSearchView = getActivity().getLayoutInflater().inflate(R.layout.header_artist_search, null);

        // Variable to hold the inflated ListView
        mArtistListView = (ListView) rootView.findViewById(R.id.list_view_artist);

        // Add Header to the ListView
        mArtistListView.addHeaderView(artistSearchView);

        // Add Adapter (containing data) to the ListView
        mArtistListView.setAdapter(artistAdapter);

        return rootView;
    }
}

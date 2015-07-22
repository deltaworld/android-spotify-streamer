package uk.tareq.spotifystreamer;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyDialogFragment extends DialogFragment {
    public static MyDialogFragment newInstance() {
        return new MyDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_player, container, false);
        //View tv = v.findViewById(R.id.text_h);
        //((TextView)tv).setText("This is an instance of MyDialogFragment");
        return v;
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}

package uk.tareq.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created: by Tareq Fadel on 06/07/15.
 * Custom ArrayAdapter utilising the Track Class
 */
public class TrackAdapter extends ArrayAdapter<MyTrack> {

    private final String LOG_TAG = getClass().getSimpleName();
    Context mContext;
    int layoutResourceId;
    List<MyTrack> data = null;

    /**
     * Constructor for the Array adapter
     *
     * @param context          is for the mContext of the activity
     * @param layoutResourceId Layout ID that the adapter
     * @param data             to include the MyTrack data
     */
    public TrackAdapter(Context context, int layoutResourceId, List<MyTrack> data) {
        super(context, layoutResourceId, data);

        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    /**
     * custom getView method that checks if each list item row is empty, if it is it inflates the
     * necessary resources. It populates the data
     *
     * @param position    index of the data
     * @param convertView the individual row, of track View, Image and Name
     * @param parent      the parentViewGroup
     * @return the returned row back to the View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemTrackView = convertView;
        TrackHolder holder;

        if (listItemTrackView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            listItemTrackView = inflater.inflate(layoutResourceId, parent, false);

            holder = new TrackHolder();
            holder.image = (ImageView) listItemTrackView.findViewById(R.id.list_item_track_imageview);
            holder.trackName = (TextView) listItemTrackView.findViewById(R.id.list_item_track_textview);
            holder.albumName = (TextView) listItemTrackView.findViewById(R.id.list_item_album_textview);

            listItemTrackView.setTag(holder);
        } else {
            holder = (TrackHolder) listItemTrackView.getTag();
        }
        //Get the data at the associated position
        MyTrack track = getItem(position);

        holder.trackName.setText(track.trackName);
        holder.albumName.setText(track.albumName);

        // Check if Track has image
        if (!track.image.equals("")) {
            Picasso.with(mContext).load(track.image).into(holder.image);
        } else {
            // Add blank imagePlaceholder to TrackHolder
            holder.image = new ImageView(mContext);
            holder.image.setImageResource(R.drawable.nophoto);
        }
        return listItemTrackView;
    }

    static class TrackHolder {
        ImageView image;
        TextView trackName;
        TextView albumName;
    }
}
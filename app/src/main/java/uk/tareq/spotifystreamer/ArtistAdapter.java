package uk.tareq.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created: by Tareq Fadel on 06/07/15.
 * Custom ArrayAdapter utilising the MyArtist Class
 */
public class ArtistAdapter extends ArrayAdapter<MyArtist> {

    Context context;
    int layoutResourceId;
    MyArtist data[] = null;

    /**
     * Constructor for the Array adapter
     *
     * @param context          is for the context of the activity
     * @param layoutResourceId Layout ID that the adapter
     * @param data             to include the MyArtist data
     */
    public ArtistAdapter(Context context, int layoutResourceId, MyArtist data[]) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    /**
     * custom getView method that checks if each list item row is empty, if it is it inflates the
     * necessary resources. It populates the data
     *
     * @param position    index of the data
     * @param convertView the individual row, of artist View, Image and Name
     * @param parent      the parentViewGroup
     * @return the returned row back to the View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ArtistHolder holder;

        if (listItemView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            listItemView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ArtistHolder();
            holder.image = (ImageView) listItemView.findViewById(R.id.list_item_artist_imageview);
            holder.artistName = (TextView) listItemView.findViewById(R.id.list_item_artist_textview);

            listItemView.setTag(holder);
        } else {
            holder = (ArtistHolder) listItemView.getTag();
        }

        MyArtist artist = data[position];
        holder.artistName.setText(artist.artistName);
        holder.image.setImageResource(artist.artistImageIndex);

        return listItemView;

    }


    static class ArtistHolder {
        ImageView image;
        TextView artistName;
    }
}

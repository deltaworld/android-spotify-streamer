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

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created: by Tareq Fadel on 06/07/15.
 * Custom ArrayAdapter utilising the Artist Class
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    Context context;
    int layoutResourceId;
    List<Artist> data = null;

    /**
     * Constructor for the Array adapter
     *
     * @param context          is for the context of the activity
     * @param layoutResourceId Layout ID that the adapter
     * @param data             to include the Artist data
     */
    public ArtistAdapter(Context context, int layoutResourceId, List<Artist> data) {
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
        View listItemArtistView = convertView;
        ArtistHolder holder;

        if (listItemArtistView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            listItemArtistView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ArtistHolder();
            holder.image = (ImageView) listItemArtistView.findViewById(R.id.list_item_artist_imageview);
            holder.artistName = (TextView) listItemArtistView.findViewById(R.id.list_item_artist_textview);

            listItemArtistView.setTag(holder);
        } else {
            holder = (ArtistHolder) listItemArtistView.getTag();
        }
        //Get the data at the associated position
        Artist artist = getItem(position);

        holder.artistName.setText(artist.name);

        // Check if Artist has image
        if (!artist.images.isEmpty()) {
            // Get second from last image (smallest size) url
            String artistImageUrl = artist.images.get(artist.images.size() - 2).url;

            // Use Picasso to cache image
            // Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);
            // http://square.github.io/picasso/
            Picasso.with(context).load(artistImageUrl).into(holder.image);

        } else {
            // Add blank imagePlaceholder to ArtistHolder
            holder.image = new ImageView(context);
            holder.image.setImageResource(R.drawable.nophoto);
        }

        return listItemArtistView;
    }

    static class ArtistHolder {
        ImageView image;
        TextView artistName;
    }
}

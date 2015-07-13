package uk.tareq.spotifystreamer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by tareqfadel on 13/07/2015.
 * Utility Class for common methods used between fragments
 */
public class Utils {
    /**
     * Checks to see if there is a network connection
     * http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android/4239019#4239019
     *
     * @return boolean response to a valid network connection.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Shows a short toast message to the user with the provided message.
     *
     * @param context the context in which this is used
     * @param message the intended message to be displayed
     * @return void. no argument returned
     */
    public static Void giveToastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
        return null;
    }
}

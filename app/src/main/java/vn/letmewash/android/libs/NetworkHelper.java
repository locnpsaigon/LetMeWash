package vn.letmewash.android.libs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

/**
 * Created by camel on 11/25/17.
 */

public class NetworkHelper {

    Context mContext;

    public NetworkHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Check network connection
     *
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Check internet connection
     *
     * @return
     */
    public boolean isInternetAvailable() {
        boolean isAvailable = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                InetAddress ipAddr = InetAddress.getByName("google.com.vn");
                if (!ipAddr.equals("")) {
                    isAvailable = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAvailable;
    }
}

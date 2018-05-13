package vn.letmewash.android.controllers.Configuration;


import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class CheckVersionTask extends AsyncTask<Void, Void, APIResult> {

    private CheckVersionTaskDelegate delegate;

    public void setDelegate(CheckVersionTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(Void... voids) {
        try {
            int androidPlatform = 1;
            return APIService.getLatestAppVersion(androidPlatform);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.checkVersionCompleted(result);
        }
    }
}

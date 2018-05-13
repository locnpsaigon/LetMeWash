package vn.letmewash.android.controllers.Configuration;


import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class LoadSupportInfoTask extends AsyncTask<Void, Void, APIResult> {

    private LoadSupportInfoTaskDelegate delegate;

    public void setDelegate(LoadSupportInfoTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(Void... voids) {
        try {
            return APIService.getSupportInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.loadSupportInfoCompleted(result);
        }
    }

}

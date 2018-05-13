package vn.letmewash.android.controllers.Configuration;

import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class LoadConfigurationTask extends AsyncTask<Void, Void, APIResult> {

    private LoadConfigurationTaskDelegate delegate;

    public void setDelegate(LoadConfigurationTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(Void... voids) {
        try {
            return APIService.getContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.loadConfigurationCompleted(result);
        }
    }

}

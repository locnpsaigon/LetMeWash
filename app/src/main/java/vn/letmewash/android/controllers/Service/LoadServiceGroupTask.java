package vn.letmewash.android.controllers.Service;

import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class LoadServiceGroupTask extends AsyncTask<Void, Void, APIResult>  {

    private LoadServiceGroupTaskDelegate delegate;

    public void setDelegate(LoadServiceGroupTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(Void... voids) {
        try {
            return APIService.getServiceGroups();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.loadServiceGroupCompleted(result);
        }
    }
}

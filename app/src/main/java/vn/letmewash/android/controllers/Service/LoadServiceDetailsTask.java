package vn.letmewash.android.controllers.Service;

import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class LoadServiceDetailsTask extends AsyncTask<Integer, Void, APIResult> {

    private LoadServiceDetailsTaskDelegate delegate;

    public void setDelegate(LoadServiceDetailsTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(Integer... integers) {
        if (integers != null && integers.length > 0) {
            int serviceId = integers[0];
            return APIService.getServiceDetails(serviceId);
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.loadServiceDetailsCompleted(result);
        }
    }
}

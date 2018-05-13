package vn.letmewash.android.controllers.Service;

import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class LoadServiceTask extends AsyncTask<Integer, Void, APIResult> {

    private LoadServiceTaskDelegate delegate;

    public void setDelegate(LoadServiceTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(Integer... integers) {
        try {
            if (integers != null && integers.length > 0) {
                int groupId = integers[0];
                return APIService.getServices(groupId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.loadServiceCompleted(result);
        }
    }

}

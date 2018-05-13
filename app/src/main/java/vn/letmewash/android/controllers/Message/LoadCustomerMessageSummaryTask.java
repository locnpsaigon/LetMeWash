package vn.letmewash.android.controllers.Message;

import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class LoadCustomerMessageSummaryTask extends AsyncTask<String, Void, APIResult>  {

    private LoadCustomerMessageSummaryTaskDelegate delegate;

    public void setDelegate(LoadCustomerMessageSummaryTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(String... strings) {
        try {
            if (strings != null && strings.length > 0) {
                String phone = strings[0];
                return APIService.getCustomerMessageSummary(phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.loadCustomerMessageSummaryCompleted(result);
        }
    }
}

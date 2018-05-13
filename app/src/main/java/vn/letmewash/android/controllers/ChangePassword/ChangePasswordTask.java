package vn.letmewash.android.controllers.ChangePassword;

import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class ChangePasswordTask extends AsyncTask<String, Void, APIResult> {

    private ChangePasswordTaskDelegate delegate;

    public void setDelegate(ChangePasswordTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(String... strings) {
        if (strings != null && strings.length >= 3) {
            String phone = strings[0];
            String passwordCurrent = strings[1];
            String password = strings[2];
            return APIService.changePassword(phone, passwordCurrent, password);
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.changePasswordCompeleted(result);
        }
    }
}

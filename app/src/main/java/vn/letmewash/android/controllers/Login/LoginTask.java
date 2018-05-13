package vn.letmewash.android.controllers.Login;


import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class LoginTask extends AsyncTask<String, Void, APIResult> {

    private LoginTaskDelegate delegate;

    public void setDelegate(LoginTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(String... strings) {
        if (strings != null && strings.length >= 2) {
            String phone = strings[0];
            String password = strings[1];
            return  APIService.login(phone, password);
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.loginCompleted(result);
        }
    }
}

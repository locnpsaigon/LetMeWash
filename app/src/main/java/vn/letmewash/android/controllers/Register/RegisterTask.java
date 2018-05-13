package vn.letmewash.android.controllers.Register;

import android.os.AsyncTask;

import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class RegisterTask extends AsyncTask<String, Void, APIResult> {

    private RegisterTaskDelegate delegate;

    private String phone;
    private String fullname;
    private String email;
    private String password;

    public void setDelegate(RegisterTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(String... strings) {
        if (strings != null && strings.length >= 4) {
            fullname = strings[0];
            phone = strings[1];
            email = strings[2];
            password = strings[3];
            APIResult result = APIService.register(fullname, phone, email, password);
            return result;
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.registerComleted(result, phone, fullname, email);
        }
    }
}

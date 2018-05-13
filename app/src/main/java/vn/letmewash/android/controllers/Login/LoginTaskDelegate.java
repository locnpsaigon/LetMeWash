package vn.letmewash.android.controllers.Login;

import vn.letmewash.android.services.APIResult;

/**
 * Created by camel on 12/25/17.
 */

public interface LoginTaskDelegate {

    void loginCompleted(APIResult result);
}

package vn.letmewash.android.controllers.Register;

import vn.letmewash.android.services.APIResult;

/**
 * Created by camel on 12/25/17.
 */

public interface RegisterTaskDelegate {

    void registerComleted(APIResult result, String phone, String fullname, String email);
}

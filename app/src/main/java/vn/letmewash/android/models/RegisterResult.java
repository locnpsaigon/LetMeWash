package vn.letmewash.android.models;

import com.google.gson.Gson;

/**
 * Created by camel on 12/4/17.
 */

public class RegisterResult {

    private int ReturnCode;
    private String ReturnMessage;

    public static RegisterResult fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, RegisterResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getReturnCode() {
        return ReturnCode;
    }

    public void setReturnCode(int returnCode) {
        ReturnCode = returnCode;
    }

    public String getReturnMessage() {
        return ReturnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        ReturnMessage = returnMessage;
    }
}

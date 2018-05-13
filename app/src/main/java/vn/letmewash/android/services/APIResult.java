package vn.letmewash.android.services;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by camel on 12/4/17.
 */

public class APIResult implements Serializable {
    private boolean Success;
    private String Data;

    public APIResult() {
        this.Success = false;
        this.Data = "";
    }

    public APIResult(boolean success, String data) {
        Success = success;
        Data = data;
    }

    public static APIResult parse(String json) {
        try {
            if (json != null) {
                JSONObject root = new JSONObject(json);
                Boolean success = root.getBoolean("Success");
                String data = root.getString("Data");
                APIResult apiResult = new APIResult(success, data);
                return apiResult;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}

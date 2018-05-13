package vn.letmewash.android.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by camel on 12/7/17.
 */

public class AppVersion {
    private int id;
    private String date;
    private int versionCode;
    private String versionName;
    private int platform;
    private int deviceType;
    private String updateURL;
    private int forceUpdate;

    public static AppVersion fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, AppVersion.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<AppVersion> fromJsonArray(String jsonArray) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonArray, new TypeToken<List<AppVersion>>(){}.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}

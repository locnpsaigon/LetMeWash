package vn.letmewash.android.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by camel on 12/10/17.
 */

public class SupportInfo {
    private int supportId;
    private String name;
    private String description;

    public static SupportInfo fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, SupportInfo.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<SupportInfo> fromJsonArray(String jsonArray) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonArray, new TypeToken<List<SupportInfo>>(){}.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSupportId() {
        return supportId;
    }

    public void setSupportId(int supportId) {
        this.supportId = supportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

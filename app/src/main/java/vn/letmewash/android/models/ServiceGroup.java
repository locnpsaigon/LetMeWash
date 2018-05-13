package vn.letmewash.android.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by camel on 11/24/17.
 */

public class ServiceGroup implements Serializable {
    private int groupId;
    private String groupName;
    private String iconURL;
    private String description;
    private String fullDescription;
    private List<Service> services;

    public static ServiceGroup fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, ServiceGroup.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ServiceGroup> fromJsonArray(String jsonArray) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonArray, new TypeToken<List<ServiceGroup>>(){}.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceGroup() {
        this.services = new ArrayList<>();
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}

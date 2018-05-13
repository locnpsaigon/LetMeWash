package vn.letmewash.android.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by camel on 11/25/17.
 */

public class ServiceDetails implements Serializable {
    private int itemId;
    private String itemName;
    private int serviceId;
    private String serviceName;
    private int groupId;
    private String groupName;
    private String unit;
    private double price;
    private double priceOriginal;

    public static ServiceDetails fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, ServiceDetails.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ServiceDetails> fromJsonArray(String jsonArray) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonArray, new TypeToken<List<ServiceDetails>>(){}.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceDetails() {
        this.price = 0;
        this.priceOriginal = 0;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPriceOriginal() {
        return priceOriginal;
    }

    public void setPriceOriginal(double priceOriginal) {
        this.priceOriginal = priceOriginal;
    }
}

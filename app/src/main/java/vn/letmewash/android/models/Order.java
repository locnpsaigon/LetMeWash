package vn.letmewash.android.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by camel on 11/27/17.
 */

public class Order implements Serializable {

    public static final int STATUS_OPENED = 1;
    public static final int STATUS_PENDING = 2;
    public static final int STATUS_PROCESSING = 3;
    public static final int STATUS_FINSIHED = 4;
    public static final int STATUS_CLOSED = 5;

    private int orderId;
    private String phone;
    private String date;
    private String fullname;
    private String address;
    private String note;
    private String title;
    private double amount;
    private int status;

    public static Order fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, Order.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Order> fromJsonArray(String jsonArray) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonArray, new TypeToken<List<Order>>(){}.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ServiceBooking> serviceBookings;

    public Order() {
        this.serviceBookings = new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ServiceBooking> getServiceBookings() {
        return serviceBookings;
    }

    public void setServiceBookings(List<ServiceBooking> serviceBookings) {
        this.serviceBookings = serviceBookings;
    }
}

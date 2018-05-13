package vn.letmewash.android.models;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by camel on 12/4/17.
 */

public class Customer implements Serializable {

    private String phone;
    private String fullname;
    private String email;
    private String address;

    public static Customer fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, Customer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

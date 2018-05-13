package vn.letmewash.android.models;

import java.io.Serializable;

/**
 * Created by camel on 11/27/17.
 */

public class ShippingAddress implements Serializable {
    private String phoneNumber;
    private String addresses;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }
}

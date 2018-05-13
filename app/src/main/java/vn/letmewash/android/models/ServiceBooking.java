package vn.letmewash.android.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by camel on 11/27/17.
 */

public class ServiceBooking implements Serializable {

    public static final int BOOKING_TYPE_MANUAL = 0;
    public static final int BOOKING_TYPE_FULL = 1;

    private int groupId;
    private String groupName;
    private int serviceId;
    private String serviceName;
    private double totalAmount;
    private double discountAmount;
    private double paymentAmount;
    private int bookType;
    private List<ServiceDetails> details;

    public ServiceBooking() {
        this.bookType = BOOKING_TYPE_MANUAL;
        this.details = new ArrayList<>();
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public List<ServiceDetails> getDetails() {
        return details;
    }

    public void setDetails(List<ServiceDetails> details) {
        this.details = details;
    }
}

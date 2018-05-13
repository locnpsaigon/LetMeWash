package vn.letmewash.android.controllers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import vn.letmewash.android.models.Service;
import vn.letmewash.android.models.ServiceBooking;
import vn.letmewash.android.models.ServiceDetails;

/**
 * Created by camel on 11/28/17.
 */

public class Cart implements Serializable {

    private static Cart mInstance;

    List<ServiceBooking> mServiceBookings;

    private Cart() {
        mServiceBookings = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (mInstance == null) {
            mInstance = new Cart();
        }
        return mInstance;
    }

    public static Boolean backupCartInfo(Context context) {
        try {
            Preferences pm = new Preferences(context);
            Cart cart = getInstance();
            // Log
            Gson gson = new Gson();
            Log.d("DBG", "Backup cart: " + gson.toJson(cart));
            return pm.setCart(cart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean restoreCartInfo(Context context) {
        try {
            Preferences pm = new Preferences(context);
            Cart cart = pm.getCart();
            if (cart != null) {
                // Log
                Gson gson = new Gson();
                Log.d("DBG", "Restore cart: " + gson.toJson(cart));

                mInstance = cart;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count() {
        int count = 0;
        for (ServiceBooking csb : mServiceBookings) {
            count += csb.getDetails().size();
        }
        return count;
    }

    public double getAmount() {
        double amount = 0;
        for (ServiceBooking csb : mServiceBookings) {
            for (ServiceDetails details : csb.getDetails()) {
                amount += details.getPrice();
            }
        }
        return amount;
    }

    public Boolean wasServiceExisted(Service service) {
        for (ServiceBooking booking : mServiceBookings) {
            if (booking.getServiceId() == service.getServiceId()) {
                return true;
            }
        }
        return false;
    }

    public Boolean wasServiceDetailsExisted(ServiceDetails details) {
        for (ServiceBooking booking : mServiceBookings) {
            for (ServiceDetails item : booking.getDetails()) {
                if (item.getItemId() == details.getItemId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<ServiceBooking> getCarServiceBookings() {
        return mServiceBookings;
    }

    public Boolean remove(Context context, int position) {
        try {
            int idx = -1;
            for (ServiceBooking csb : mServiceBookings) {
                idx++;
                // Remove header
                if (idx == position) {
                    // remove service
                    mServiceBookings.remove(csb);
                    backupCartInfo(context);
                    return true;
                }
                // Remove details
                Boolean wasItemRemoved = false;
                for (ServiceDetails details : csb.getDetails()) {
                    idx++;
                    if (idx == position) {
                        // remove service details
                        csb.getDetails().remove(details);
                        backupCartInfo(context);
                        wasItemRemoved = true;
                    }
                }
                if (csb.getDetails().size() == 0) {
                    // remove service
                    mServiceBookings.remove(csb);
                    backupCartInfo(context);
                    wasItemRemoved = true;
                }

                // Return if the item was removed
                if (wasItemRemoved) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d("DBG", "Remove booking service index = " + position);
        }
        return false;
    }

    public Boolean remove(Context context, Service service) {
        if (mServiceBookings != null) {
            for (int i = mServiceBookings.size() - 1; i>=0; i--) {
                if (mServiceBookings.get(i).getServiceId() == service.getServiceId()) {
                    mServiceBookings.remove(mServiceBookings.get(i));
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public Boolean addServiceBooking(Context context, ServiceBooking booking) {
        try {
            if (booking != null) {
                // Log
                Gson gson = new Gson();
                Log.d("DBG", "Add booking: " + gson.toJson(booking));

                // Add booking service
                mServiceBookings.add(booking);
                backupCartInfo(context);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean reset(Context context) {
        try {
            Preferences pref = new Preferences(context);
            pref.setCart(null);
            if (mServiceBookings != null) {
                mServiceBookings.clear();
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

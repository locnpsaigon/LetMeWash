package vn.letmewash.android.controllers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import vn.letmewash.android.models.Customer;

/**
 * Created by camel on 11/29/17.
 */

/**
 * Controller to manage application authentication
 */
public class Authentication {

    Context mContext;

    public Authentication(Context context) {
        this.mContext = context;
    }

    /**
     * Save authen info
     *
     * @param customer
     * @return
     */
    public Boolean saveLoginCustomer(Customer customer) {
        // Log
        if (customer != null) {
            Gson gson = new Gson();
            Log.d("DBG", "Save authentication: " + gson.toJson(customer));
        }

        // Save customer info
        Preferences prefs = new Preferences(mContext);
        return prefs.setAutheInfo(customer);
    }

    /**
     * Get current logon customer info
     * @return
     */
    public Customer getLoginCustomer() {
        // Get current customer info
        Preferences prefs = new Preferences(mContext);
        Customer customer = prefs.getAuthenInfo();
        return customer;
    }

    /**
     * Signout
     *
     * @return
     */
    public Boolean signout() {
        // Log
        Log.d("DBG", "Signout");

        // Clear authentication info
        Preferences prefs = new Preferences(mContext);
        return prefs.setAutheInfo(null);
    }

    /**
     * Check authentication
     * @return
     */
    public Boolean isAuthenticated() {
        Preferences prefs = new Preferences(mContext);
        Customer customer = prefs.getAuthenInfo();
        return customer != null ? true : false;
    }

}

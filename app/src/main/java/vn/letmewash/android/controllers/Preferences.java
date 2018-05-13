package vn.letmewash.android.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import vn.letmewash.android.models.Customer;

/**
 * Created by camel on 11/21/17.
 */

public class Preferences {

    static final String SHARE_PREFERENCE_NAME = "LMWAppPreferences";
    static final String APP_PREF_AUTHEN_INFO = "AuthenInfo";
    static final String APP_PREF_CART_INFO = "CartInfo";

    private Context context;
    private SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save login info (customer info)
     *
     * @param customer
     * @return
     */
    public Boolean setAutheInfo(Customer customer) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(APP_PREF_AUTHEN_INFO);
            if (customer != null) {
                Gson gson = new Gson();
                String data = gson.toJson(customer);
                editor.putString(APP_PREF_AUTHEN_INFO, data);
            }
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Customer getAuthenInfo() {
        try {
            String data = sharedPreferences.getString(APP_PREF_AUTHEN_INFO, null);
            if (data != null) {
                return Customer.fromJson(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save cart info into application peferences
     *
     * @param cart
     * @return
     */
    public Boolean setCart(Cart cart) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(APP_PREF_CART_INFO);
            if (cart != null) {
                Gson gson = new Gson();
                String data = gson.toJson(cart);
                editor.putString(APP_PREF_CART_INFO, data);
            }
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get cart info from application preferences
     *
     * @return
     */
    public Cart getCart() {
        try {
            String data = sharedPreferences.getString(APP_PREF_CART_INFO, null);
            if (data != null) {
                Gson gson = new Gson();
                return gson.fromJson(data, Cart.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

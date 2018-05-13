package vn.letmewash.android.services;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import vn.letmewash.android.libs.HTTPHelper;
import vn.letmewash.android.models.Order;

/**
 * Created by camel on 11/29/17.
 */

public class APIService {

    private static final String API_URL = "http://locnp.ddns.net/api";
    private static final String API_KEY = "bGV0bWV3YXNoOmxldG1ld2FzaEBBcGkxMjM=";

    /**
     * Login API
     *
     * @param phone
     * @param password
     * @return
     */
    public static APIResult login(String phone, String password) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("phone", phone);
            params.accumulate("password", password);
            String response = HTTPHelper.makeHTTPRequest(
                    "POST", API_URL + "/Customer/verify", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Register customer account API
     * @param fullname
     * @param phone
     * @param email
     * @param password
     * @return
     */
    public static APIResult register(String fullname, String phone, String email, String password) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("fullname", fullname);
            params.accumulate("phone", phone);
            params.accumulate("email", email);
            params.accumulate("password", password);
            String response = HTTPHelper.makeHTTPRequest(
                    "POST", API_URL + "/Customer/register", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Change password
     * @param phone
     * @param passwordCurrent
     * @param password
     * @return
     */
    public static APIResult changePassword(String phone, String passwordCurrent, String password) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("phone", phone);
            params.accumulate("passwordCurrent", passwordCurrent);
            params.accumulate("password", password);
            String response = HTTPHelper.makeHTTPRequest(
                    "POST", API_URL + "/Customer/changePassword", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Email khôi phục mật khẩu
     * @param phone
     * @param email
     * @return
     */
    public static APIResult recoveryPassword(String phone, String email) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("phone", phone);
            params.accumulate("email", email);
            String response = HTTPHelper.makeHTTPRequest(
                    "POST", API_URL + "/Customer/RecoveryPassword", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all car service groups and include its child services and child service details
     * @return
     */
    public static APIResult getServiceGroups() {
        try {
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Service/getServiceGroups", null, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all car service and include its service details
     * @return
     */
    public static APIResult getServices(int groupId) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("groupId", groupId);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Service/getServices", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all car service details
     * @return
     */
    public static APIResult getServiceDetails(int serviceId) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("serviceId", serviceId);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Service/getServiceDetails", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save order
     * @param order
     * @return
     */
    public static APIResult saveOrder(Order order) {
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(order);
            JSONObject params = new JSONObject(jsonString);
            String response = HTTPHelper.makeHTTPRequest(
                    "POST", API_URL + "/Order/save", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get customer orders
     * @param phone
     * @return
     */
    public static APIResult getOrders(String phone, int skip, int take) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("phone", phone);
            params.accumulate("skip", skip);
            params.accumulate("take", take);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Order/getOrders", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get customer orders
     * @param orderId
     * @return
     */
    public static APIResult getOrderDetails(int orderId) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("orderId", orderId);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Order/getOrderDetails", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the update of order details from an existed order
     * @param orderId
     * @return
     */
    public static APIResult reuseOrderDetails(int orderId) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("orderId", orderId);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Order/reuseOrderDetails", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get contacts
     * @return
     */
    public static APIResult getContacts() {
        try {
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Config/getContacts", null, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get suport info
     * @return
     */
    public static APIResult getSupportInfo() {
        try {
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Config/getSupportInfo", null, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get latest app version
     * @param platform 1: android, 2: iOS
     * @return
     */
    public static APIResult getLatestAppVersion(int platform) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("platform", platform);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Config/getLatestVersion", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get customer's message
     * @param phone
     * @return
     */
    public static APIResult getMessages(String phone, int skip, int take) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("phone", phone);
            params.accumulate("skip", skip);
            params.accumulate("take", take);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Message/getMessages", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update message status
     * @param messageId
     *  0: unread
     *  1: read
     *  2: removed
     * @return
     */
    public static APIResult updateMessageStatus(int messageId, int status) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("messageId", messageId);
            params.accumulate("status", status);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Message/updateMessageStatus", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mark all customer's message as read status
     * @param phone
     * @return
     */
    public static APIResult setAllMessageAsRead(String phone) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("phone", phone);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Message/setAllMessagesAsRead", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get customer message status summary
     * @param phone
     * @return
     */
    public static APIResult getCustomerMessageSummary(String phone) {
        try {
            JSONObject params = new JSONObject();
            params.accumulate("phone", phone);
            String response = HTTPHelper.makeHTTPRequest(
                    "GET", API_URL + "/Message/getCustomerMessageSummary", params, API_KEY);
            return APIResult.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}

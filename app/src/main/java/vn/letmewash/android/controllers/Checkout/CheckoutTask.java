package vn.letmewash.android.controllers.Checkout;

import android.os.AsyncTask;

import vn.letmewash.android.models.Order;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * Created by camel on 12/25/17.
 */

public class CheckoutTask extends AsyncTask<Order, Void, APIResult> {

    private CheckoutTaskDelegate delegate;

    public void setDelegate(CheckoutTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected APIResult doInBackground(Order... orders) {
        try {
            if (orders != null && orders.length > 0) {
                return APIService.saveOrder(orders[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResult result) {
        if (delegate != null) {
            delegate.checkoutCompleted(result);
        }
    }
}

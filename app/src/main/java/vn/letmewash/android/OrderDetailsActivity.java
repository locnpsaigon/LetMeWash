package vn.letmewash.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.letmewash.android.adapters.CartAdapter;
import vn.letmewash.android.controllers.Cart;
import vn.letmewash.android.libs.Common;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.OrderDetails;
import vn.letmewash.android.models.ServiceBooking;
import vn.letmewash.android.models.Order;
import vn.letmewash.android.models.ServiceDetails;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

public class OrderDetailsActivity extends AppCompatActivity {

    RelativeLayout layoutData, layoutStatus;
    ProgressBar pgbStatus;
    ListView lvOrderDetails;
    TextView tvStatus;
    TextView tvOrderNo, tvTotalAmount, tvOrderDate, tvOrderStatus;
    ImageView imgIconStatus, imgIconRetry;
    Button btReuseOrder;

    Cart mCart;
    Order mOrder;
    Context mContext;
    List<ServiceBooking> mBookingDetails;
    CartAdapter mAdapter;

    private void showLoadingStatus() {
        // Hide data layout
        layoutData.setVisibility(View.INVISIBLE);

        // Show status layout
        layoutStatus.setVisibility(View.VISIBLE);
        pgbStatus.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(getString(R.string.action_loading));

        // Hide status & retry button
        imgIconStatus.setVisibility(View.INVISIBLE);
        imgIconRetry.setVisibility(View.INVISIBLE);
    }

    private void hideLoadingStatus() {
        // Show data layout
        layoutData.setVisibility(View.VISIBLE);

        // Hide status layout
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    private void showMessageStatus(String status, int iconResId) {
        // Show status layout
        layoutStatus.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(status);
        imgIconStatus.setVisibility(View.VISIBLE);
        imgIconStatus.setImageResource(iconResId);
        imgIconRetry.setVisibility(View.VISIBLE);


        // Hide data layout
        layoutData.setVisibility(View.INVISIBLE);

        // Hide progress bar
        pgbStatus.setVisibility(View.INVISIBLE);
    }

    private void showDataLayout() {
        layoutData.setVisibility(View.VISIBLE);
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    protected void checkNetworkStatus() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    NetworkHelper networkHelper = new NetworkHelper(mContext);
                    return networkHelper.isOnline() && networkHelper.isInternetAvailable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean isOnline) {
                if (isOnline == false) {
                    showMessageStatus(getString(R.string.text_network_error), R.drawable.ic_network_error);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void showOrderInfo(Order order) {
        if (order != null) {
            tvOrderNo.setText(getString(R.string.text_order_no) + ": #" + mOrder.getOrderId());
            tvTotalAmount.setText(getString(R.string.text_total) + ": " + Common.formatDecimal(mOrder.getAmount()) + "Đ");
            Date orderDate = Common.getDate(mOrder.getDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
            tvOrderDate.setText(getString(R.string.text_order_date) + ": " + Common.formatDate(orderDate, "dd/MM/yyyy HH:mm"));
            switch (mOrder.getStatus()) {
                case 0:
                    tvOrderStatus.setText(mContext.getString(R.string.status_pending));
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                    break;
                case 1:
                    tvOrderStatus.setText(mContext.getString(R.string.status_processing));
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                    break;
                case 2:
                    tvOrderStatus.setText(mContext.getString(R.string.status_finished));
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                    break;
                case 3:
                    tvOrderStatus.setText(mContext.getString(R.string.status_close));
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                    break;
                default:
                    tvOrderStatus.setText(mContext.getString(R.string.status_pending));
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                    break;
            }

            loadOrderDetails(order);

        } else {
            showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel);
        }
    }

    protected void loadOrderDetails(Order order) {
        if (order != null) {
            showLoadingStatus();
            LoadOrderDetailsTask task = new LoadOrderDetailsTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, order.getOrderId());
        }
    }

    protected void loadOrderDetailsCompleted(APIResult result) {
        hideLoadingStatus();
        if (result != null && result.isSuccess()) {
            mBookingDetails.clear();
            List<OrderDetails> details = OrderDetails.fromJsonArray(result.getData());
            if (details != null) {

                /** Get booking service */
                for (OrderDetails item : details) {
                    boolean wasExisted = false;
                    for(ServiceBooking booking : mBookingDetails) {
                        if (booking.getGroupId() == item.getGroupId() && booking.getServiceId() == item.getServiceId()) {
                            wasExisted = true;
                            break;
                        }
                    }
                    if (!wasExisted) {
                        ServiceBooking booking = new ServiceBooking();
                        booking.setGroupId(item.getGroupId());
                        booking.setGroupName(item.getGroupName());
                        booking.setServiceId(item.getServiceId());
                        booking.setServiceName(item.getServiceName());

                        mBookingDetails.add(booking);
                    }
                }

                /** Get booking service details */
                for (ServiceBooking booking : mBookingDetails) {
                    for (OrderDetails item : details) {
                        if (item.getGroupId() == booking.getGroupId() && item.getServiceId() == booking.getServiceId()) {
                            ServiceDetails serviceDetails = new ServiceDetails();
                            serviceDetails.setGroupId(item.getGroupId());
                            serviceDetails.setGroupName(item.getGroupName());
                            serviceDetails.setServiceId(item.getServiceId());
                            serviceDetails.setServiceName(item.getItemName());
                            serviceDetails.setItemId(item.getItemId());
                            serviceDetails.setItemName(item.getItemName());
                            serviceDetails.setUnit(item.getUnit());
                            serviceDetails.setPrice(item.getPrice());
                            serviceDetails.setPriceOriginal(item.getPrice());

                            booking.getDetails().add(serviceDetails);
                        }
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
            showDataLayout();

            if (mBookingDetails.size() == 0) {
                showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
            }
        } else {
            showMessageStatus(getString(R.string.text_load_data_fail), R.drawable.ic_cancel);
            checkNetworkStatus();
        }
    }

    protected void reuseOrderDetails(Order order) {
        if (order != null) {
            int orderId = order.getOrderId();
            ReuseOrderDetailsTask task = new ReuseOrderDetailsTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, orderId);
        }
    }

    protected void reuseOrderDetailsCompleted(APIResult result) {
        if (result != null && result.isSuccess()) {
            mCart.reset(mContext);
            List<OrderDetails> details = OrderDetails.fromJsonArray(result.getData());
            if (details != null) {

                /** Get booking service */
                for (OrderDetails item : details) {
                    boolean wasExisted = false;
                    for(ServiceBooking booking : mCart.getCarServiceBookings()) {
                        if (booking.getGroupId() == item.getGroupId() && booking.getServiceId() == item.getServiceId()) {
                            wasExisted = true;
                            break;
                        }
                    }
                    if (!wasExisted) {
                        ServiceBooking booking = new ServiceBooking();
                        booking.setGroupId(item.getGroupId());
                        booking.setGroupName(item.getGroupName());
                        booking.setServiceId(item.getServiceId());
                        booking.setServiceName(item.getServiceName());

                        mCart.addServiceBooking(mContext, booking);
                    }
                }

                /** Get booking service details */
                for (ServiceBooking booking : mCart.getCarServiceBookings()) {
                    for (OrderDetails item : details) {
                        if (item.getGroupId() == booking.getGroupId() && item.getServiceId() == booking.getServiceId()) {
                            ServiceDetails serviceDetails = new ServiceDetails();
                            serviceDetails.setGroupId(item.getGroupId());
                            serviceDetails.setGroupName(item.getGroupName());
                            serviceDetails.setServiceId(item.getServiceId());
                            serviceDetails.setServiceName(item.getItemName());
                            serviceDetails.setItemId(item.getItemId());
                            serviceDetails.setItemName(item.getItemName());
                            serviceDetails.setUnit(item.getUnit());
                            serviceDetails.setPrice(item.getPrice());
                            serviceDetails.setPriceOriginal(item.getPrice());

                            booking.getDetails().add(serviceDetails);
                        }
                    }
                }

                // Start cart activity
                Intent intent = new Intent(mContext, CartActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            showMessageStatus(getString(R.string.text_load_data_fail), R.drawable.ic_cancel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        mContext = this;
        mCart = Cart.getInstance();
        mOrder = (Order) getIntent().getExtras().getSerializable("Order");
        mBookingDetails = new ArrayList<>();
        mAdapter = new CartAdapter(this, mBookingDetails);
        mAdapter.setShowRemoveButton(false);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.title_activity_order_details));
        }

        layoutData = findViewById(R.id.layoutData);
        layoutStatus = findViewById(R.id.layoutStatus);
        pgbStatus = findViewById(R.id.pgbStatus);
        lvOrderDetails = findViewById(R.id.lvOrderDetails);
        lvOrderDetails.setAdapter(mAdapter);
        tvStatus = findViewById(R.id.tvStatus);
        tvOrderNo = findViewById(R.id.tvOrderNo);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        imgIconStatus = findViewById(R.id.imgIconStatus);
        imgIconRetry = findViewById(R.id.imgIconRetry);
        imgIconRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderInfo(mOrder);
            }
        });
        btReuseOrder = findViewById(R.id.btReuseOrder);
        btReuseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.text_confirm_reuse_order)
                        .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.d("DBG", "Reuse order");
                                reuseOrderDetails(mOrder);
                            }
                        })
                        .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showOrderInfo(mOrder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class LoadOrderDetailsTask extends AsyncTask<Integer, Void, APIResult> {

        @Override
        protected APIResult doInBackground(Integer... integers) {
            try {
                if (integers != null) {
                    int orderId = integers[0];
                    return APIService.getOrderDetails(orderId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(APIResult result) {
            loadOrderDetailsCompleted(result);
        }
    }

    class ReuseOrderDetailsTask extends AsyncTask<Integer, Void, APIResult> {
        @Override
        protected APIResult doInBackground(Integer... integers) {
            try {
                if (integers != null && integers.length > 0) {
                    int orderId = integers[0];
                    return APIService.reuseOrderDetails(orderId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(APIResult result) {
            reuseOrderDetailsCompleted(result);
        }
    }
}

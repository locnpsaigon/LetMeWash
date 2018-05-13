package vn.letmewash.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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
import java.util.HashMap;
import java.util.List;

import vn.letmewash.android.adapters.ServiceDetailsAdapter;
import vn.letmewash.android.controllers.Cart;
import vn.letmewash.android.controllers.Service.LoadServiceDetailsTask;
import vn.letmewash.android.controllers.Service.LoadServiceDetailsTaskDelegate;
import vn.letmewash.android.libs.Common;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.Service;
import vn.letmewash.android.models.ServiceBooking;
import vn.letmewash.android.models.ServiceDetails;
import vn.letmewash.android.models.ServiceGroup;
import vn.letmewash.android.services.APIResult;

public class ServiceDetailsActivity extends AppCompatActivity implements LoadServiceDetailsTaskDelegate {

    RelativeLayout layoutServiceDetails, layoutStatus;
    ListView lvCarServiceDetails;
    TextView tvBadge, tvStatus;
    TextView tvTotal, tvTotalAmount;
    TextView tvDiscount, tvDiscountAmount;
    TextView tvPayment, tvPaymentAmount;
    ProgressBar pgbStatus;
    Button btAddToCart;
    ImageView imgIconStatus, imgIconRetry;

    Context mContext;
    Service mService;
    ServiceGroup mServiceGroup;
    List<ServiceDetails> mServiceDetails;
    ServiceDetailsAdapter mServiceDetailsAdapter;

    LoadServiceDetailsTask mLoadServiceDetailsTask = null;

    Boolean mIsLoadingServiceDetails = false;
    Boolean mIsCheckingNetworkStatus = false;

    private void showLoadingStatus() {
        // Hide data layout
        layoutServiceDetails.setVisibility(View.INVISIBLE);

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
        layoutServiceDetails.setVisibility(View.VISIBLE);

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
        layoutServiceDetails.setVisibility(View.INVISIBLE);

        // Hide progress bar
        pgbStatus.setVisibility(View.INVISIBLE);
    }

    private void showDataLayout() {
        layoutServiceDetails.setVisibility(View.VISIBLE);
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    protected void checkNetworkStatus() {
        mIsCheckingNetworkStatus = true;
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
                mIsCheckingNetworkStatus = true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void loadServiceDetails(int serviceId) {
        showLoadingStatus();
        mLoadServiceDetailsTask = new LoadServiceDetailsTask();
        mLoadServiceDetailsTask.setDelegate(this);
        mLoadServiceDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serviceId);
        mIsLoadingServiceDetails = true;
    }

    @Override
    public void loadServiceDetailsCompleted(APIResult result) {
        try {
            hideLoadingStatus();
            if (result != null && result.isSuccess()) {
                /** Load service success */
                mServiceDetails.clear();
                List<ServiceDetails> details = ServiceDetails.fromJsonArray(result.getData());
                if (details != null) {
                    mServiceDetails.addAll(details);
                }
                mServiceDetailsAdapter.notifyDataSetChanged();
                showDataLayout();

                if (mServiceDetails.size() == 0) {
                    showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
                }

            } else {
                showMessageStatus(getString(R.string.text_load_data_fail), R.drawable.ic_loading_fail);
                checkNetworkStatus();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mIsLoadingServiceDetails = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);
        ActionBar ab = getSupportActionBar();
        mContext = this;
        mService = (Service) getIntent().getSerializableExtra("Service");
        mServiceGroup = (ServiceGroup) getIntent().getSerializableExtra("ServiceGroup");
        mServiceDetails = new ArrayList<>();
        mServiceDetailsAdapter = new ServiceDetailsAdapter(this, mServiceDetails);

        // Set display home
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.title_service_details).toUpperCase());
        }

        // Get widgets
        lvCarServiceDetails = findViewById(R.id.lvServiceDetails);
        lvCarServiceDetails.setAdapter(mServiceDetailsAdapter);
        lvCarServiceDetails.setItemsCanFocus(true);
        tvTotal = findViewById(R.id.tvTotal);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvPayment = findViewById(R.id.tvPayment);
        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);
        btAddToCart = findViewById(R.id.btAddToCart);
        btAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mServiceGroup != null && mService != null) {

                    /** Create a booking */
                    // Set booking header
                    final ServiceBooking booking = new ServiceBooking();
                    booking.setGroupId(mServiceGroup.getGroupId());
                    booking.setGroupName(mServiceGroup.getGroupName());
                    booking.setServiceId(mService.getServiceId());
                    booking.setServiceName(mService.getServiceName());
                    // Set booking details
                    double totalAmount = 0;
                    double discountAmount = 0;
                    double paymentAmount;
                    HashMap selectedItems = mServiceDetailsAdapter.getCheckedItems();
                    for (ServiceDetails details : mServiceDetails) {
                        int key = details.getItemId();
                        if (selectedItems.containsKey(key) && (Boolean) selectedItems.get(key) == true) {
                            booking.getDetails().add(details);
                            totalAmount += details.getPrice();
                            double discount = details.getPriceOriginal() - details.getPrice();
                            if (discount > 0) {
                                discountAmount += discount;
                            }
                        }
                    }
                    paymentAmount = totalAmount - discountAmount;
                    booking.setTotalAmount(totalAmount);
                    booking.setDiscountAmount(discountAmount);
                    booking.setPaymentAmount(paymentAmount);
                    if (booking.getDetails().size() == mService.getDetails().size()) {
                        booking.setBookType(ServiceBooking.BOOKING_TYPE_FULL);
                    } else {
                        booking.setBookType(ServiceBooking.BOOKING_TYPE_MANUAL);
                    }

                    /** Has any service details ? */
                    final Cart cart = Cart.getInstance();
                    if (booking.getDetails().size() > 0) {
                        Boolean wasDetailsExisted = false;
                        for (int i = booking.getDetails().size() - 1; i >= 0; i--) {
                            if (cart.wasServiceDetailsExisted(booking.getDetails().get(i))) {
                                wasDetailsExisted = true;
                                break;
                            }
                        }

                        /** Was service existed in cart? */
                        if (wasDetailsExisted) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(R.string.text_cart_item_existed)
                                    .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Add service booking service to cart
                                            cart.remove(mContext, mService);
                                            cart.addServiceBooking(mContext, booking);
                                            Toast.makeText(mContext, getString(R.string.text_add_cart_item_success), Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    })
                                    .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                            dialog.dismiss();
                                        }
                                    });
                            builder.show();
                        } else {
                            // Add service booking service to cart
                            cart.addServiceBooking(mContext, booking);
                            Toast.makeText(mContext, getString(R.string.text_add_cart_item_success), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } else {
                        Toast.makeText(mContext, getString(R.string.text_selected_service_required), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, getString(R.string.text_selected_service_required), Toast.LENGTH_SHORT).show();
                }
            }
        });

        layoutServiceDetails = findViewById(R.id.layoutServiceDetails);
        layoutStatus = findViewById(R.id.layoutStatus);

        tvStatus = findViewById(R.id.tvStatus);
        pgbStatus = findViewById(R.id.pgbStatus);

        imgIconStatus = findViewById(R.id.imgIconStatus);
        imgIconRetry = findViewById(R.id.imgIconRetry);
        imgIconRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service_details, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();
        tvBadge = actionView.findViewById(R.id.tvBadge);
        showCartBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DBG", "Select cart menu!!!");
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if(mIsCheckingNetworkStatus || mIsLoadingServiceDetails) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.text_please_wait), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    finish();
                }
                return true;

            case R.id.action_cart:
                Intent intent = new Intent(mContext, CartActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_check_all:
                // Toggle details item selection status
                mServiceDetailsAdapter.setAllChecked(!mServiceDetailsAdapter.isAllChecked());
                showCashAmount();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCartBadge();
        if (mService != null) {
            loadServiceDetails(mService.getServiceId());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mIsCheckingNetworkStatus || mIsLoadingServiceDetails) {
            Toast toast = Toast.makeText(mContext, getString(R.string.text_please_wait), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else {
            finish();
        }
    }

    public void showCashAmount() {
        double totalAmount = 0;
        double discountAmount = 0;
        double cashAmount = 0;
        if (mServiceDetails != null) {
            HashMap selectedItems = mServiceDetailsAdapter.getCheckedItems();
            for (ServiceDetails details : mServiceDetails) {
                if (selectedItems.containsKey(details.getItemId()) &&
                        (Boolean) selectedItems.get(details.getItemId()) == true) {
                    totalAmount += details.getPrice();
                    double discount = details.getPriceOriginal() - details.getPrice();
                    if (discount > 0) {
                        discountAmount += discount;
                    }
                }
            }
            cashAmount = totalAmount - discountAmount;
        }
        tvTotalAmount.setText(Common.formatDecimal(totalAmount) + "Đ");
        if (discountAmount == 0) {
            tvDiscountAmount.setText(Common.formatDecimal(discountAmount) + "Đ");
        } else {
            tvDiscountAmount.setText("-" + Common.formatDecimal(discountAmount) + "Đ");
        }
        tvPaymentAmount.setText(Common.formatDecimal(cashAmount) + "Đ");
    }

    public void showCartBadge() {
        int itemCount = Cart.getInstance().count();
        if (tvBadge != null) {
            tvBadge.setText(String.valueOf(itemCount));
            if (Cart.getInstance().count() > 0) {
                tvBadge.setVisibility(View.VISIBLE);
            } else {
                tvBadge.setVisibility(View.INVISIBLE);
            }
        }
    }

}

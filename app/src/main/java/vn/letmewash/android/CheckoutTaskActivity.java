package vn.letmewash.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vn.letmewash.android.controllers.Authentication;
import vn.letmewash.android.controllers.Cart;
import vn.letmewash.android.controllers.Checkout.CheckoutTaskDelegate;
import vn.letmewash.android.controllers.Checkout.CheckoutTask;
import vn.letmewash.android.controllers.Constants;
import vn.letmewash.android.libs.NotificationHelper;
import vn.letmewash.android.libs.UIHelper;
import vn.letmewash.android.models.Customer;
import vn.letmewash.android.models.Order;
import vn.letmewash.android.models.ServiceBooking;
import vn.letmewash.android.services.APIResult;

import static vn.letmewash.android.controllers.Constants.LOGIN_REQUEST_CODE;

public class CheckoutTaskActivity extends AppCompatActivity implements CheckoutTaskDelegate {

    ActionBar mActionBar;
    EditText etPhone, etFullName, etNote, etAddress;
    Button btLogin, btCheckout;
    ScrollView layoutCheckout;
    RelativeLayout layoutStatus;
    TextView tvStatus;

    Context mContext;
    CheckoutTask mCheckoutTask = null;

    Boolean mIsCheckingOut = false;

    private void initialize() {
        Authentication auth = new Authentication(mContext);
        Customer customer = auth.getLoginCustomer();
        if (customer != null) {
            etPhone.setText(customer.getPhone());
            etPhone.setEnabled(false);
            etFullName.setText(customer.getFullname());
            etAddress.setText(customer.getAddress());
            btLogin.setVisibility(View.INVISIBLE);
        } else {
            etPhone.setEnabled(true);
            btLogin.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadingStatus(String status) {
        layoutCheckout.setVisibility(View.INVISIBLE);
        layoutStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(status);
    }

    private void hideLoadingStatus() {
        layoutCheckout.setVisibility(View.VISIBLE);
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    protected void checkout() {
        try {
            final Order order = new Order();
            order.setPhone(etPhone.getText().toString().trim());
            order.setFullname(etFullName.getText().toString().trim());
            order.setAddress(etAddress.getText().toString().trim());
            order.setNote(etNote.getText().toString().trim());
            Cart cart = Cart.getInstance();
            List<ServiceBooking> bookings = cart.getCarServiceBookings();
            if (bookings != null) {
                order.getServiceBookings().addAll(bookings);
            }

            if (order.getPhone().equals("")) {
                Toast.makeText(mContext, getString(R.string.text_phone_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (order.getFullname().equals("")) {
                Toast.makeText(mContext, getString(R.string.text_fullname_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (order.getServiceBookings().size() == 0) {
                Toast.makeText(mContext, getString(R.string.text_cart_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            showLoadingStatus(getString(R.string.text_process_order));
            mCheckoutTask = new CheckoutTask();
            mCheckoutTask.setDelegate(this);
            mCheckoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, order);

            mIsCheckingOut = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkoutCompleted(APIResult result) {
        try {
            hideLoadingStatus();

            if (result != null && result.isSuccess()) {

                // Reset cart
                Cart cart = Cart.getInstance();
                cart.reset(mContext);

                // Show success message
                String title = getString(R.string.action_checkout);
                String message = result != null ? result.getData() : getString(R.string.text_save_order_success);
                String okButtonText = getString(R.string.action_close);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder
                        .setIcon(R.drawable.ic_checked)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(okButtonText,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        // show notification
                                        String title = getString(R.string.app_name);
                                        String message = getString(R.string.text_save_order_success);
                                        String summary = getString(R.string.text_thank_you);
                                        NotificationHelper notificationHelper = new NotificationHelper(mContext);
                                        notificationHelper.showNotificaiotn(title, message, summary);
                                        // finalize
                                        Intent intent = new Intent();
                                        setResult(Constants.CHECKOUT_SUCCESS, intent);
                                        finish();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                String title = getString(R.string.action_checkout);
                String message = result != null ? result.getData() : getString(R.string.text_save_order_fail);
                String okButtonText = getString(R.string.action_ok);
                UIHelper.showAlertDialog(mContext, title, message, okButtonText, R.drawable.ic_error);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mIsCheckingOut = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mContext = this;
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle(getString(R.string.text_confirm_payment));
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get widgets
        layoutCheckout = findViewById(R.id.layoutCheckout);
        layoutStatus = findViewById(R.id.layoutStatus);
        tvStatus = findViewById(R.id.tvStatus);
        etPhone = findViewById(R.id.etPhone);
        etFullName = findViewById(R.id.etFullName);
        etNote = findViewById(R.id.etNote);
        etAddress = findViewById(R.id.etAddress);
        etAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etAddress.setRawInputType(InputType.TYPE_CLASS_TEXT);
        btLogin = findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });
        btCheckout = findViewById(R.id.btCheckout);
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
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

}

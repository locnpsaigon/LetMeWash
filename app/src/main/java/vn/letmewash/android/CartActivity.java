package vn.letmewash.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import vn.letmewash.android.adapters.CartAdapter;
import vn.letmewash.android.controllers.Cart;
import vn.letmewash.android.controllers.Constants;
import vn.letmewash.android.libs.Common;

import static vn.letmewash.android.controllers.Constants.CHECKOUT_REQUEST_CODE;

public class CartActivity extends AppCompatActivity {

    Button btCheckout;
    RelativeLayout layoutCart, layoutCartEmpty;
    SwipeMenuListView lvCart;
    TextView tvPayment, tvPaymentAmount;

    Context mContext;
    ActionBar mActionBar;
    CartAdapter mAdapter;
    Cart mCart;

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void startCheckout() {
        Intent intent = new Intent(mContext, CheckoutTaskActivity.class);
        startActivityForResult(intent, CHECKOUT_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mContext = this;
        mActionBar = getSupportActionBar();
        mCart = Cart.getInstance();
        mAdapter = new CartAdapter(mContext, mCart.getCarServiceBookings());

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get widgets
        layoutCart = findViewById(R.id.layoutCart);
        layoutCartEmpty = findViewById(R.id.layoutCartEmpty);

        lvCart = findViewById(R.id.lvCart);
        lvCart.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        lvCart.setAdapter(mAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create deleted menu
                SwipeMenuItem menuDeleted = new SwipeMenuItem(mContext);
                menuDeleted.setWidth(dp2px(90));
                menuDeleted.setBackground(R.color.colorRed);
                menuDeleted.setIcon(R.drawable.ic_trash);
                menu.addMenuItem(menuDeleted);
            }
        };
        lvCart.setMenuCreator(creator);
        lvCart.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                try {
                    Log.d("DBG", "Delete cart item " + position);
                    mCart.remove(mContext, position);
                    mAdapter.notifyDataSetChanged();
                    updateCartStatus();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        tvPayment = findViewById(R.id.tvPayment);
        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);
        btCheckout = findViewById(R.id.btCheckout);
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DBG", "Checkout");
                startCheckout();
            }
        });

        updateCartStatus();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECKOUT_REQUEST_CODE) {
            if (resultCode == Constants.CHECKOUT_SUCCESS) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void updateCartStatus() {
        // Update cart items quantity
        if (mActionBar != null) {
            mActionBar.setTitle(getString(R.string.text_cart) + " (" + Cart.getInstance().count() + ")");
        }

        // Update payment amount
        tvPaymentAmount.setText(Common.formatDecimal(Cart.getInstance().getAmount()) + "Đ");

        // Disable checkout button if cart empty
        if (mCart.count() > 0) {
            layoutCart.setVisibility(View.VISIBLE);
            layoutCartEmpty.setVisibility(View.INVISIBLE);
        } else {
            layoutCart.setVisibility(View.INVISIBLE);
            layoutCartEmpty.setVisibility(View.VISIBLE);
        }
    }
}

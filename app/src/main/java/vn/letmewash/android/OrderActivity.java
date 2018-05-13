package vn.letmewash.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.letmewash.android.adapters.OrderAdapter;
import vn.letmewash.android.controllers.Authentication;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.Customer;
import vn.letmewash.android.models.Order;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

import static vn.letmewash.android.controllers.Constants.LOGIN_REQUEST_CODE;
import static vn.letmewash.android.controllers.Constants.PAGE_SIZE_DEFAULT;

public class OrderActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    ListView lvOrder;
    TextView tvStatus;
    RelativeLayout layoutData, layoutStatus;
    ProgressBar pgbStatus;
    Button btLogin;
    ImageView imgIconStatus, imgIconRetry;

    Context mContext;
    Authentication mAuthentication;
    Boolean isLoading = false;
    List<Order> mOrders;
    OrderAdapter mOrdersAdapter;
    Boolean userScrolled = false;

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
        btLogin.setVisibility(View.INVISIBLE);
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

        // Hide login button
        btLogin.setVisibility(View.INVISIBLE);
    }

    private void showRequireLogin() {
        // Show status layout
        layoutStatus.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(getString(R.string.text_login_required));
        imgIconStatus.setVisibility(View.VISIBLE);
        btLogin.setVisibility(View.VISIBLE);

        // Hide data layout
        layoutData.setVisibility(View.INVISIBLE);

        // Hide progress bar
        pgbStatus.setVisibility(View.INVISIBLE);

        // Hi retry icon
        imgIconRetry.setVisibility(View.INVISIBLE);
    }

    private void showDataLayout() {
        layoutData.setVisibility(View.VISIBLE);
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    protected void startLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
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

    protected void loadOrders(String phone, int skip, int take) {
        if (!isLoading) {
            isLoading = true;
            if (mOrders.size() == 0) {
                showLoadingStatus();
            }
            LoadCustomerOrdersTask task = new LoadCustomerOrdersTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phone, String.valueOf(skip), String.valueOf(take));
        }
    }

    protected void loadOrdersCompleted(APIResult result) {
        hideLoadingStatus();
        if (result != null && result.isSuccess()) {
            List<Order> orders = Order.fromJsonArray(result.getData());
            if (orders != null) {
                mOrders.addAll(orders);
                mOrdersAdapter.notifyDataSetChanged();
            }
            showDataLayout();
            if (mOrders.size() == 0) {
                showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
            }
        } else {
            showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
            checkNetworkStatus();
        }
        isLoading = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        mContext = this;
        mAuthentication = new Authentication(this);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.title_activity_order));
        }
        mOrders = new ArrayList<>();
        mOrdersAdapter = new OrderAdapter(this, mOrders);

        // Get widgets
        layoutData = findViewById(R.id.layoutData);
        layoutStatus = findViewById(R.id.layoutStatus);
        lvOrder = findViewById(R.id.lvOrder);
        lvOrder.setAdapter(mOrdersAdapter);
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = (Order) mOrdersAdapter.getItem(position);
                if (order != null) {
                    Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("Order", order);
                    intent.putExtras(data);
                    startActivity(intent);
                }
            }
        });
        lvOrder.setOnScrollListener(this);

        pgbStatus = findViewById(R.id.pgbStatus);
        tvStatus = findViewById(R.id.tvStatus);
        btLogin = findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
        imgIconStatus = findViewById(R.id.imgIconStatus);
        imgIconRetry = findViewById(R.id.imgIconRetry);
        imgIconRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuthentication.isAuthenticated()) {
                    Customer customer = mAuthentication.getLoginCustomer();
                    String phone = customer.getPhone();
                    loadOrders(phone, mOrders.size(), PAGE_SIZE_DEFAULT);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrders.clear();
        if (mAuthentication.isAuthenticated()) {
            Customer customer = mAuthentication.getLoginCustomer();
            String phone = customer.getPhone();
            loadOrders(phone, mOrders.size(), PAGE_SIZE_DEFAULT);
        } else {
            showRequireLogin();
        }
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

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (userScrolled && firstVisibleItem + visibleItemCount == mOrders.size()) {
            userScrolled = false;
            Customer customer = mAuthentication.getLoginCustomer();
            if (customer != null) {
                loadOrders(customer.getPhone(), mOrders.size(), PAGE_SIZE_DEFAULT);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            userScrolled = true;
        }
    }

    class LoadCustomerOrdersTask extends AsyncTask<String, Void, APIResult> {
        @Override
        protected APIResult doInBackground(String... strings) {
            try {
                if (strings != null && strings.length >= 3) {
                    String phone = strings[0];
                    int skip = Integer.parseInt(strings[1]);
                    int take = Integer.parseInt(strings[2]);
                    return APIService.getOrders(phone, skip, take);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(APIResult result) {
            loadOrdersCompleted(result);
        }
    }
}

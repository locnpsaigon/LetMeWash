package vn.letmewash.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.letmewash.android.adapters.ServiceAdapter;
import vn.letmewash.android.controllers.Cart;
import vn.letmewash.android.controllers.Service.LoadServiceTask;
import vn.letmewash.android.controllers.Service.LoadServiceTaskDelegate;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.Service;
import vn.letmewash.android.models.ServiceGroup;
import vn.letmewash.android.services.APIResult;

public class ServiceActivity extends AppCompatActivity implements LoadServiceTaskDelegate {

    RelativeLayout layoutService, layoutStatus;
    ListView lvCarServiceItems;
    TextView tvBadge, tvStatus;
    ProgressBar pgbStatus;
    ImageView imgIconStatus, imgIconRetry;

    Context mContext;
    ServiceGroup mServiceGroup;
    List<Service> mServices;
    ServiceAdapter mServiceAdapter;

    LoadServiceTask mLoadServiceTask = null;

    Boolean mIsLoadingService = false;

    private void showLoadingStatus() {
        // Hide data layout
        layoutService.setVisibility(View.INVISIBLE);

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
        layoutService.setVisibility(View.VISIBLE);

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
        layoutService.setVisibility(View.INVISIBLE);

        // Hide progress bar
        pgbStatus.setVisibility(View.INVISIBLE);
    }

    private void showDataLayout() {
        layoutService.setVisibility(View.VISIBLE);
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

    protected void loadServices(int groupId) {
        showLoadingStatus();
        mLoadServiceTask = new LoadServiceTask();
        mLoadServiceTask.setDelegate(this);
        mLoadServiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupId);
        mIsLoadingService = true;
    }

    @Override
    public void loadServiceCompleted(APIResult result) {
        try {
            hideLoadingStatus();
            if (result != null && result.isSuccess()) {
                /** Load service success */
                mServices.clear();
                List<Service> services = Service.fromJsonArray(result.getData());
                if (services != null) {
                    mServices.addAll(services);
                }
                mServiceAdapter.notifyDataSetChanged();
                showDataLayout();

                if (mServices.size() == 0) {
                    showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
                }

            } else {
                showMessageStatus(getString(R.string.text_load_data_fail), R.drawable.ic_loading_fail);
                checkNetworkStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mIsLoadingService = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ActionBar ab = getSupportActionBar();
        mContext = this;
        mServiceGroup = (ServiceGroup) getIntent().getExtras().getSerializable("ServiceMenu");
        mServices = new ArrayList<>();
        mServiceAdapter = new ServiceAdapter(this, mServices);

        // Set display home
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            if (mServiceGroup != null) {
                Log.d("DBG", "Selected service: " + mServiceGroup.getGroupName());
                ab.setTitle(mServiceGroup.getGroupName());
            }
        }

        // Get widget
        lvCarServiceItems = findViewById(R.id.lvCarService);
        lvCarServiceItems.setAdapter(mServiceAdapter);
        lvCarServiceItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DBG", "Selected service item = " + position);
                Service service = (Service) mServiceAdapter.getItem(position);
                if (service != null) {
                    Intent intent = new Intent(ServiceActivity.this, ServiceDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Service", service);
                    bundle.putSerializable("ServiceGroup", mServiceGroup);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        layoutService = findViewById(R.id.layoutService);
        layoutStatus = findViewById(R.id.layoutStatus);

        pgbStatus = findViewById(R.id.pgbStatus);
        tvStatus = findViewById(R.id.tvStatus);

        imgIconStatus = findViewById(R.id.imgIconStatus);
        imgIconRetry = findViewById(R.id.imgIconRetry);
        imgIconRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceGroup != null) {
                    loadServices(mServiceGroup.getGroupId());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service, menu);
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
            case R.id.action_cart:
                Intent intentCart = new Intent(mContext, CartActivity.class);
                startActivity(intentCart);
                return true;

            case R.id.action_info:
                if (mServiceGroup != null) {
                    Intent intentServiceGuide = new Intent(ServiceActivity.this,
                            ServiceDescriptionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ServiceMenu", mServiceGroup);
                    intentServiceGuide.putExtras(bundle);
                    startActivity(intentServiceGuide);
                }
                return true;

            case android.R.id.home:
                if (mIsLoadingService) {
                    Toast toast = Toast.makeText(mContext, getString(R.string.text_please_wait), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCartBadge();
        if (mServiceGroup != null) {
            loadServices(mServiceGroup.getGroupId());
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsLoadingService) {
            Toast toast = Toast.makeText(mContext, getString(R.string.text_please_wait), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            finish();
        }
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

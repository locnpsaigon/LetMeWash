package vn.letmewash.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import vn.letmewash.android.adapters.ServiceGroupAdapter;
import vn.letmewash.android.controllers.Authentication;
import vn.letmewash.android.controllers.Configuration.LoadConfigurationTask;
import vn.letmewash.android.controllers.Configuration.LoadConfigurationTaskDelegate;
import vn.letmewash.android.controllers.Message.LoadCustomerMessageSummaryTask;
import vn.letmewash.android.controllers.Message.LoadCustomerMessageSummaryTaskDelegate;
import vn.letmewash.android.controllers.Service.LoadServiceGroupTask;
import vn.letmewash.android.controllers.Service.LoadServiceGroupTaskDelegate;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.Contact;
import vn.letmewash.android.models.MessageStatus;
import vn.letmewash.android.models.ServiceGroup;
import vn.letmewash.android.models.Customer;
import vn.letmewash.android.services.APIResult;

import static vn.letmewash.android.controllers.Constants.LOGIN_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LoadServiceGroupTaskDelegate,
        LoadCustomerMessageSummaryTaskDelegate,
        LoadConfigurationTaskDelegate {

    static boolean active = false;

    RelativeLayout layoutServiceGroup, layoutStatus;
    NavigationView mNavigationView;
    ListView lvCarServiceGroup;
    TextView tvMessageBadge, tvStatus;
    ProgressBar pgbStatus;
    ImageView imgIconStatus, imgIconRetry;

    Context mContext;
    List<ServiceGroup> mServiceGroups;
    ServiceGroupAdapter mServiceGroupAdapter;
    Authentication mAuthentication;
    String mHotline = "0911915577";

    LoadServiceGroupTask mLoadServiceGroupTask = null;
    LoadCustomerMessageSummaryTask mLoadCustomerMessageSummaryTask = null;
    LoadConfigurationTask mLoadConfigurationTask = null;

    Boolean mIsLoadingServiceGroup = false;
    Boolean mIsLoadingCustomerMessageSummary = false;
    Boolean mIsLoadingConfiguration = false;

    private void navigateHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void navigateCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    private void navigateOrders() {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    private void navigateMessages() {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

    private void navigateSupport() {
        Intent intent = new Intent(this, SupportActivity.class);
        startActivity(intent);
    }

    private void navigateChangePass() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mHotline));
        startActivity(intent);
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

    protected void updateLoginStatus() {
        // Get navigation widgets
        final View viewHeader = mNavigationView.getHeaderView(0);
        final TextView tvTitle = viewHeader.findViewById(R.id.textView);
        final Menu menu = mNavigationView.getMenu();
        final MenuItem menuLogin = menu.findItem(R.id.nav_login);
        final MenuItem menuChangePassword = menu.findItem(R.id.nav_change_password);

        // Get logon user info
        Authentication auth = new Authentication(mContext);
        Customer customer = auth.getLoginCustomer();

        // Set login status
        if (customer != null) {

            // Show user info
            tvTitle.setText(getString(R.string.text_hello) + ", " + customer.getFullname());
            menuLogin.setTitle(getString(R.string.action_logout));
            menuLogin.setIcon(R.drawable.ic_login_left);

            // Show change password menu
            menuChangePassword.setVisible(true);

        } else {

            // Show require login
            tvTitle.setText(getString(R.string.text_login_required));
            menuLogin.setTitle(getString(R.string.action_login));
            menuLogin.setIcon(R.drawable.ic_login_right);

            // Hide message badge
            if (tvMessageBadge != null) {
                tvMessageBadge.setVisibility(View.INVISIBLE);
            }

            // Hide change password menu
            menuChangePassword.setVisible(false);
        }
    }

    protected void showLoadingStatus() {
        // Hide data layout
        layoutServiceGroup.setVisibility(View.INVISIBLE);

        // Show status layout
        layoutStatus.setVisibility(View.VISIBLE);
        pgbStatus.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(getString(R.string.action_loading));

        // Hide status & retry button
        imgIconStatus.setVisibility(View.INVISIBLE);
        imgIconRetry.setVisibility(View.INVISIBLE);
    }

    protected void hideLoadingStatus() {
        // Show data layout
        layoutServiceGroup.setVisibility(View.VISIBLE);

        // Hide status layout
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    protected void showMessageStatus(String status, int iconResId) {
        // Hide data layout
        layoutServiceGroup.setVisibility(View.INVISIBLE);

        // Show status layout
        layoutStatus.setVisibility(View.VISIBLE);

        // Hide progress bar
        pgbStatus.setVisibility(View.INVISIBLE);

        // Show status text and icons
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(status);
        imgIconStatus.setVisibility(View.VISIBLE);
        imgIconStatus.setImageResource(iconResId);
        imgIconRetry.setVisibility(View.VISIBLE);
    }

    protected void showDataLayout() {
        layoutServiceGroup.setVisibility(View.VISIBLE);
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    protected void loadServiceGroups() {
        showLoadingStatus();
        mLoadServiceGroupTask = new LoadServiceGroupTask();
        mLoadServiceGroupTask.setDelegate(this);
        mLoadServiceGroupTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mIsLoadingServiceGroup = true;
    }

    protected void loadCustomerMessageSummary(Customer customer) {
        if (customer != null) {
            String phone  = customer.getPhone();
            mLoadCustomerMessageSummaryTask = new LoadCustomerMessageSummaryTask();
            mLoadCustomerMessageSummaryTask.setDelegate(this);
            mLoadCustomerMessageSummaryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phone);
            mIsLoadingCustomerMessageSummary = true;
        }
    }

    protected void loadConfiguration() {
        mLoadConfigurationTask = new LoadConfigurationTask();
        mLoadConfigurationTask.setDelegate(this);
        mLoadConfigurationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mIsLoadingConfiguration = true;
    }

    @Override
    public void loadConfigurationCompleted(APIResult result) {
        try {
            if (result != null && result.isSuccess()) {
                List<Contact> contacts = Contact.fromJsonArray(result.getData());
                if (contacts != null && contacts.size() > 0) {
                    mHotline = contacts.get(0).getPhone();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mIsLoadingConfiguration = false;
        }
    }

    @Override
    public void loadCustomerMessageSummaryCompleted(APIResult result) {
        try {
            if (result != null && result.isSuccess()) {
                MessageStatus messageStatus = MessageStatus.fromJson(result.getData());
                if (tvMessageBadge != null) {
                    if (messageStatus.getUnread() > 0) {
                        tvMessageBadge.setText(String.valueOf(messageStatus.getUnread()));
                        tvMessageBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvMessageBadge.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvMessageBadge.setVisibility(View.INVISIBLE);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mIsLoadingCustomerMessageSummary = false;
        }
    }

    @Override
    public void loadServiceGroupCompleted(APIResult result) {
        try {
            hideLoadingStatus();
            if (result != null && result.isSuccess()) {
                /** Load service groups success */
                mServiceGroups.clear();
                List<ServiceGroup> groups = ServiceGroup.fromJsonArray(result.getData());
                if (groups != null) {
                    mServiceGroups.addAll(groups);
                }
                mServiceGroupAdapter.notifyDataSetChanged();
                showDataLayout();

                if (mServiceGroups.size() == 0) {
                    showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
                }

            } else {
                showMessageStatus(getString(R.string.text_load_data_fail), R.drawable.ic_loading_fail);
                checkNetworkStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mIsLoadingServiceGroup = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mServiceGroups = new ArrayList<>();
        mServiceGroupAdapter = new ServiceGroupAdapter(this, mServiceGroups);
        mAuthentication = new Authentication(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.title_service_group).toUpperCase());
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Đang gọi dịch vụ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                makeCall();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Get widgets
        lvCarServiceGroup = findViewById(R.id.lvServiceMennu);
        lvCarServiceGroup.setAdapter(mServiceGroupAdapter);
        lvCarServiceGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DBG", "Selected menu = " + position);
                ServiceGroup selectedMenu = (ServiceGroup) mServiceGroupAdapter.getItem(position);
                if (selectedMenu != null) {
                    Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ServiceMenu", selectedMenu);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        layoutServiceGroup = findViewById(R.id.layout_service_group);
        layoutStatus = findViewById(R.id.layoutStatus);
        pgbStatus = findViewById(R.id.pgbStatus);
        tvStatus = findViewById(R.id.tvStatus);
        imgIconStatus = findViewById(R.id.imgIconStatus);
        imgIconRetry = findViewById(R.id.imgIconRetry);
        imgIconRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadServiceGroups();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConfiguration();
        updateLoginStatus();
        loadServiceGroups();

        if (mAuthentication.isAuthenticated()) {
            Customer customer = mAuthentication.getLoginCustomer();
            loadCustomerMessageSummary(customer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Cart badge
        final MenuItem messageMenuItem = menu.findItem(R.id.action_message);
        View messageActionView = messageMenuItem.getActionView();
        ImageView imgBadgeIcon = messageActionView.findViewById(R.id.imgBadgeIcon);
        imgBadgeIcon.setImageResource(R.drawable.ic_alarm);
        tvMessageBadge = messageActionView.findViewById(R.id.tvBadge);
        messageActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(messageMenuItem);
            }
        });

        if (mAuthentication.isAuthenticated()) {
            Customer customer = mAuthentication.getLoginCustomer();
            loadCustomerMessageSummary(customer);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_message) {
            Log.d("DBG", "Show messages");
            navigateMessages();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_home:
                Log.d("DBG", "Open home");
                if (!active) {
                    navigateHome();
                }
                break;

            case R.id.nav_my_cart:
                Log.d("DBG", "Show orders");
                navigateCart();
                break;

            case R.id.nav_my_orders:
                Log.d("DBG", "Show orders");
                navigateOrders();
                break;

            case R.id.nav_my_message:
                Log.d("DBG", "Show messages");
                navigateMessages();
                break;

            case R.id.nav_support:
                Log.d("DBG", "Show support");
                navigateSupport();
                break;

            case R.id.nav_change_password:
                Log.d("DBG", "Change password");
                navigateChangePass();
                break;

            case R.id.nav_login:

                // Get logon user
                Authentication auth = new Authentication(mContext);
                if (auth.isAuthenticated()) {
                    Log.d("DBG", "Logout");
                    auth.signout();
                    Toast toast = Toast.makeText(mContext, getString(R.string.text_signout_success), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Log.d("DBG", "Login");
                    startLogin();
                }
                updateLoginStatus();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

}

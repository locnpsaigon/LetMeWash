package vn.letmewash.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import vn.letmewash.android.adapters.LoginFragmentPagerAdapter;
import vn.letmewash.android.controllers.Authentication;
import vn.letmewash.android.controllers.Constants;
import vn.letmewash.android.controllers.Login.LoginTaskDelegate;
import vn.letmewash.android.controllers.Login.LoginTask;
import vn.letmewash.android.controllers.Register.RegisterTaskDelegate;
import vn.letmewash.android.controllers.Register.RegisterTask;
import vn.letmewash.android.libs.UIHelper;
import vn.letmewash.android.models.Customer;
import vn.letmewash.android.services.APIResult;

public class LoginActivity extends AppCompatActivity implements
        LoginFragment.UserLogonListener,
        RegisterFragment.UserRegistrationListener,
        LoginTaskDelegate, RegisterTaskDelegate {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    LinearLayout layoutLogin;
    RelativeLayout layoutStatus;
    TextView tvStatus;

    Context mContext;
    LoginFragmentPagerAdapter mAdapter;
    LoginTask mLoginTask = null;
    RegisterTask mRegisterTask = null;

    Boolean mIsLoggingIn = false;
    Boolean mIsRegistering = false;

    private void showLoadingStatus(String status) {
        layoutLogin.setVisibility(View.INVISIBLE);
        layoutStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(status);
    }

    private void hideLoadingStatus() {
        layoutLogin.setVisibility(View.VISIBLE);
        layoutStatus.setVisibility(View.INVISIBLE);
    }

    protected void recoveryPassword() {
        Intent intent = new Intent(mContext, RecoveryPasswordActivity.class);
        startActivity(intent);
    }

    protected void login(String phone, String password) {
        // Validate login info
        if (phone == null || phone.trim().equals("")) {
            Toast.makeText(mContext, getResources().getString(R.string.text_phone_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password == null || password.length() < 6) {
            Toast.makeText(mContext, getResources().getString(R.string.text_password_rule_not_matched), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingStatus(getString(R.string.text_login_in_process));

        mLoginTask = new LoginTask();
        mLoginTask.setDelegate(this);
        mLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phone, password);

        mIsLoggingIn = true;

    }

    protected void register(String fullname, String phone, String email, String password, String password2) {
        // Validate register info
        if (fullname == null || fullname.trim().equals("")) {
            Toast.makeText(mContext, getString(R.string.text_fullname_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone == null || phone.trim().equals("")) {
            Toast.makeText(mContext, getString(R.string.text_phone_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password == null || password.length() < 6) {
            Toast.makeText(mContext, getString(R.string.text_password_rule_not_matched), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals(password2) == false) {
            Toast.makeText(mContext, getString(R.string.text_password_confirm_not_matched), Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user info
        showLoadingStatus(getString(R.string.text_register_in_progress));

        mRegisterTask = new RegisterTask();
        mRegisterTask.setDelegate(this);
        mRegisterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fullname, phone, email, password);

        mIsRegistering = true;
    }

    @Override
    public void loginCompleted(APIResult result) {
        try {
            hideLoadingStatus();

            if (result != null && result.isSuccess()) {
                // Login success => Save authentication info
                Customer customer = Customer.fromJson(result.getData());
                Authentication authentication = new Authentication(mContext);
                authentication.saveLoginCustomer(customer);

                Intent data = new Intent();
                setResult(Constants.LOGIN_OK, data);
                finish();
            } else {
                String title = getString(R.string.action_login);
                String message = (result != null) ? result.getData() : getResources().getString(R.string.text_login_fail);
                String okButtonText = getResources().getString(R.string.action_close);
                UIHelper.showAlertDialog(mContext, title, message, okButtonText, R.drawable.ic_error);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mIsLoggingIn = false;
        }
    }

    @Override
    public void registerComleted(APIResult result, String phone, String fullname, String email) {
        try {
            hideLoadingStatus();
            if (result != null && result.isSuccess()) {
                // Save authentication
                Customer customer = new Customer();
                customer.setPhone(phone);
                customer.setFullname(fullname);
                customer.setEmail(email);

                Authentication authentication = new Authentication(mContext);
                authentication.saveLoginCustomer(customer);

                // Show notification
                String message = getResources().getString(R.string.text_register_success);
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

                finish();
            } else {
                // Register fail
                String title = getResources().getString(R.string.action_register);
                String message = result != null ? result.getData() : getResources().getString(R.string.text_register_fail);
                String okButtonText = getResources().getString(R.string.action_close);
                UIHelper.showAlertDialog(mContext, title, message, okButtonText, R.drawable.ic_error);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mIsRegistering = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.action_login) + "/" + getString(R.string.action_register));
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        mAdapter = new LoginFragmentPagerAdapter(mContext, getSupportFragmentManager());

        // Get widgets
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdapter);

        mTabLayout = findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutStatus = findViewById(R.id.layoutStatus);
        tvStatus = findViewById(R.id.tvStatus);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsLoggingIn == false && mIsRegistering == false) {
                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mIsLoggingIn == false && mIsRegistering == false) {
            finish();
        }
    }

    @Override
    public void onLogin(String phone, String password) {
        login(phone, password);
    }

    @Override
    public void onRecoveryPassword() {
        Log.d("DBG", "User forgot password ==> recovery?");
        recoveryPassword();
    }

    @Override
    public void onRegister(final String fullname, final String phone, final String email, final String password, String password2) {
        register(fullname, phone, email, password, password2);
    }
}

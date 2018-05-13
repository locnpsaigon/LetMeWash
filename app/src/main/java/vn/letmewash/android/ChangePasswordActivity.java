package vn.letmewash.android;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vn.letmewash.android.controllers.Authentication;
import vn.letmewash.android.controllers.ChangePassword.ChangePasswordTaskDelegate;
import vn.letmewash.android.controllers.ChangePassword.ChangePasswordTask;
import vn.letmewash.android.libs.UIHelper;
import vn.letmewash.android.models.Customer;
import vn.letmewash.android.services.APIResult;

public class ChangePasswordActivity extends AppCompatActivity implements ChangePasswordTaskDelegate {

    Button btChangePass;
    EditText etPasswordCurrent;
    EditText etPassword;
    EditText etPassword2;

    Context mContext;
    ChangePasswordTask mChangePassTask = null;
    Boolean mIsChangingPassword = false;

    protected void changePassword(Customer customer) {
        if (customer != null) {
            // validate inputs data
            String passwordCurrent = etPasswordCurrent.getText().toString();
            String password = etPassword.getText().toString();
            String password2 = etPassword2.getText().toString();

            if (passwordCurrent == null && passwordCurrent.length() < 6) {
                Toast.makeText(mContext, getResources().getString(R.string.hint_password_rule), Toast.LENGTH_SHORT).show();
                return;
            }

            if (password == null && password.length() < 6) {
                Toast.makeText(mContext, getResources().getString(R.string.hint_password_rule), Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.equals(password2) == false) {
                Toast.makeText(mContext, getString(R.string.text_password_confirm_not_matched), Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable change password button
            btChangePass.setText(getText(R.string.status_processing));
            btChangePass.setEnabled(false);

            mIsChangingPassword = true;

            // Process change password
            String phone = customer.getPhone();
            mChangePassTask = new ChangePasswordTask();
            mChangePassTask.setDelegate(this);
            mChangePassTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phone, passwordCurrent, password);
        }
    }

    @Override
    public void changePasswordCompeleted(APIResult result) {
        try {
            // Validate change password result
            if (result != null && result.isSuccess()) {
                // Success
                String message = getResources().getString(R.string.text_change_password_success);
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                finish();
            } else {
                // Error or fail
                String title = getResources().getString(R.string.action_change_password);
                String message = result != null ? result.getData() : getResources().getString(R.string.text_change_password_fail);
                String okButtonText = getResources().getString(R.string.action_close);
                UIHelper.showAlertDialog(mContext, title, message, okButtonText, R.drawable.ic_error);
            }

            // Enable change password button
            btChangePass.setText(getText(R.string.action_change_password));
            btChangePass.setEnabled(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mIsChangingPassword = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.action_change_password));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get widgets
        etPasswordCurrent = findViewById(R.id.etPasswordCurrent);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        btChangePass = findViewById(R.id.btChangePass);
        btChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authentication authentication = new Authentication(mContext);
                Customer customer = authentication.getLoginCustomer();
                changePassword(customer);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsChangingPassword) {
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
    public void onBackPressed() {
        super.onBackPressed();
        if (mIsChangingPassword) {
            Toast toast = Toast.makeText(mContext, getString(R.string.text_please_wait), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            finish();
        }
    }

}


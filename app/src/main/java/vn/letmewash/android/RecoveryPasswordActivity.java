package vn.letmewash.android;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vn.letmewash.android.controllers.Authentication;
import vn.letmewash.android.libs.UIHelper;
import vn.letmewash.android.models.Customer;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

public class RecoveryPasswordActivity extends AppCompatActivity {

    Context mContext;
    EditText etPhone, etEmail;
    Button btRecovery;

    private void recoveryPassword() {
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (phone == null || phone.equals("")) {
            Toast.makeText(mContext, getResources().getString(R.string.text_phone_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (email == null || email.equals("")) {
            Toast.makeText(mContext, getResources().getString(R.string.text_email_required), Toast.LENGTH_SHORT).show();
            return;
        }

        RecoveryPasswordTask task = new RecoveryPasswordTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phone, email);

        // disable recovery button
        btRecovery.setEnabled(false);
        btRecovery.setText(getString(R.string.status_processing));

    }

    private void recoveryPasswordFinished(APIResult result) {
        // enable recovery button
        btRecovery.setEnabled(true);
        btRecovery.setText(getString(R.string.action_recovery_password));

        if (result != null && result.isSuccess()) {
            // Recovery password success
            String title = getResources().getString(R.string.action_recovery_password);
            String message = result != null ? result.getData() : getResources().getString(R.string.text_recovery_password_fail);
            String okButtonText = getResources().getString(R.string.action_close);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            alertDialogBuilder
                    .setIcon(R.drawable.ic_ok)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(okButtonText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    finish();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else {
            // Recovery password fail
            String title = getResources().getString(R.string.action_recovery_password);
            String message = result != null ? result.getData() : getResources().getString(R.string.text_recovery_password_fail);
            String okButtonText = getResources().getString(R.string.action_close);
            UIHelper.showAlertDialog(mContext, title, message, okButtonText, R.drawable.ic_error);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.action_recovery_password));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get widgets
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btRecovery = findViewById(R.id.btRecovery);
        btRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoveryPassword();
            }
        });
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

    class RecoveryPasswordTask extends AsyncTask<String, Void, APIResult> {
        @Override
        protected APIResult doInBackground(String... strings) {
            if (strings != null && strings.length >= 2) {
                String phone = strings[0];
                String email = strings[1];
                return  APIService.recoveryPassword(phone, email);
            }
            return null;
        }

        @Override
        protected void onPostExecute(APIResult result) {
            recoveryPasswordFinished(result);
        }
    }
}

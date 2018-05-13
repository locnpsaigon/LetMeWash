package vn.letmewash.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

import vn.letmewash.android.controllers.Cart;
import vn.letmewash.android.controllers.Configuration.CheckVersionTask;
import vn.letmewash.android.controllers.Configuration.CheckVersionTaskDelegate;
import vn.letmewash.android.libs.Common;
import vn.letmewash.android.models.AppVersion;
import vn.letmewash.android.models.Contact;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

/**
 * A screen which appear when starting application
 */
public class SplashActivity extends AppCompatActivity implements CheckVersionTaskDelegate{


    CheckVersionTask mCheckVersionTask = null;

    Runnable mHideSplashScreen = new Runnable() {
        @Override
        public void run() {
            goMain();
            finish();
        }
    };

    private void initialize() {

        // Restore cart
        Cart.restoreCartInfo(this);

        Handler handler = new Handler();
        handler.postDelayed(mHideSplashScreen, 500);
    }

    private void openUpdateUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected void checkVersion() {
        mCheckVersionTask = new CheckVersionTask();
        mCheckVersionTask.setDelegate(this);
        mCheckVersionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void checkVersionCompleted(APIResult result) {
        if (result != null && result.isSuccess()) {
            AppVersion version = AppVersion.fromJson(result.getData());
            if (version.getVersionCode() > Common.getVersionCode(this)) {
                if (version.getForceUpdate() == 1) {
                    // Force update
                    String title = getString(R.string.title_update);
                    String message = getString(R.string.text_force_update).replace("{0}", version.getVersionName());
                    String okButtonText = getString(R.string.action_update);
                    final String updateUrl = version.getUpdateURL();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder
                            .setIcon(R.drawable.ic_error)
                            .setTitle(title)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(okButtonText,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            openUpdateUrl(updateUrl);
                                            System.exit(0);
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    // Show update message
                    String title = getString(R.string.title_update);
                    String message = getString(R.string.text_update_remind).replace("{0}", version.getVersionName());
                    String okButtonText = getString(R.string.action_update);
                    String cancelButtonText = getString(R.string.action_cancel);
                    final String updateUrl = version.getUpdateURL();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder
                            .setIcon(R.drawable.ic_info_2)
                            .setTitle(title)
                            .setMessage(message)
                            .setCancelable(false)
                            .setNegativeButton(cancelButtonText,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            initialize();
                                        }
                                    })
                            .setPositiveButton(okButtonText,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            openUpdateUrl(updateUrl);
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } else {
                initialize();
            }
        } else {
            initialize();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide action bar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        // Make activity full screen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
    }

}

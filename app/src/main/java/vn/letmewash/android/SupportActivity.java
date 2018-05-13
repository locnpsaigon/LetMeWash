package vn.letmewash.android;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vn.letmewash.android.controllers.Configuration.LoadSupportInfoTask;
import vn.letmewash.android.controllers.Configuration.LoadSupportInfoTaskDelegate;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.Service;
import vn.letmewash.android.models.SupportInfo;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

public class SupportActivity extends AppCompatActivity implements LoadSupportInfoTaskDelegate {

    Context mContext;

    RelativeLayout layoutData, layoutStatus;
    TextView tvStatus;
    WebView wvSupportInfo;
    ProgressBar pgbStatus;
    ImageView imgIconStatus, imgIconRetry;

    LoadSupportInfoTask mLoadSupportInfoTask = null;

    Boolean mIsLoadingSupportInfo = false;

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
    }

    private void showDataLayout() {
        layoutData.setVisibility(View.VISIBLE);
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

    protected void loadSupportInfo() {
        showLoadingStatus();
        mLoadSupportInfoTask = new LoadSupportInfoTask();
        mLoadSupportInfoTask.setDelegate(this);
        mLoadSupportInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mIsLoadingSupportInfo = true;
    }

    @Override
    public void loadSupportInfoCompleted(APIResult result) {
        try {
            hideLoadingStatus();
            if (result != null && result.isSuccess()) {
                /** Load support info success */
                List<SupportInfo> supportInfos = SupportInfo.fromJsonArray(result.getData());
                if (supportInfos != null && supportInfos.size() > 0) {
                    SupportInfo supportInfo = supportInfos.get(0);
                    wvSupportInfo.loadData(supportInfo.getDescription(), "text/html; charset=utf-8", "UTF-8");
                    showDataLayout();
                } else {
                    showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
                }
            } else {
                showMessageStatus(getString(R.string.text_load_data_fail), R.drawable.ic_loading_fail);
                checkNetworkStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mIsLoadingSupportInfo = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.title_support));
        }

        layoutData = findViewById(R.id.layoutData);
        layoutStatus = findViewById(R.id.layoutStatus);

        wvSupportInfo = findViewById(R.id.wvSupportInfo);
        pgbStatus = findViewById(R.id.pgbStatus);
        tvStatus  = findViewById(R.id.tvStatus);

        imgIconStatus = findViewById(R.id.imgIconStatus);
        imgIconRetry = findViewById(R.id.imgIconRetry);
        imgIconRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSupportInfo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSupportInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsLoadingSupportInfo) {
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
        if (mIsLoadingSupportInfo) {
            Toast toast = Toast.makeText(mContext, getString(R.string.text_please_wait), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            finish();
        }
    }

}

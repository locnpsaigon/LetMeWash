package vn.letmewash.android;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import vn.letmewash.android.libs.Common;
import vn.letmewash.android.models.ServiceGroup;

public class ServiceDescriptionActivity extends AppCompatActivity {

    Button btBack;
    WebView wvServiceInfo;
    ServiceGroup mServiceGroup;
    String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_info);
        ActionBar ab = getSupportActionBar();
        mServiceGroup = (ServiceGroup) getIntent().getExtras().getSerializable("ServiceMenu");

        // Set display home
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            if (mServiceGroup != null) {
                Log.d("DBG", "Selected service: " + mServiceGroup.getGroupName());
                ab.setTitle("Dịch vụ ".toUpperCase() + mServiceGroup.getGroupName());
            }
        }

        if (mServiceGroup != null) {
            mDescription = mServiceGroup.getFullDescription();
        } else {
            mDescription =  "<font color='red'>Dữ liệu đang được cập nhật</font>";
        }

        // Get widgets
        wvServiceInfo = findViewById(R.id.wvServiceInfo);
        wvServiceInfo.loadData(mDescription,"text/html; charset=utf-8", "UTF-8");
        btBack = findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}

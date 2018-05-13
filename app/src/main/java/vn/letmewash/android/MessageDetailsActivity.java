package vn.letmewash.android;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import vn.letmewash.android.libs.Common;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.Message;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

public class MessageDetailsActivity extends AppCompatActivity {


    TextView tvTitle, tvDate;
    WebView wvMessage;

    Context mContext;
    Message mMessage;

    private void showMessageInfo(Message message) {
        if (message != null) {
            tvTitle.setText(message.getTitle());
            Date orderDate = Common.getDate(message.getDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
            tvDate.setText(Common.formatDate(orderDate, "dd/MM/yyyy HH:mm"));
            wvMessage.loadData(message.getMessage(), "text/html; charset=utf-8", "UTF-8");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        mContext = this;
        mMessage = (Message) getIntent().getExtras().getSerializable("Message");
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.title_message_details));
        }

        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        wvMessage = findViewById(R.id.wvMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMessage != null) {
            showMessageInfo(mMessage);
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
}

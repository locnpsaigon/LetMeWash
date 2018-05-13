package vn.letmewash.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import vn.letmewash.android.adapters.MessageAdapter;
import vn.letmewash.android.controllers.Authentication;
import vn.letmewash.android.libs.NetworkHelper;
import vn.letmewash.android.models.Customer;
import vn.letmewash.android.models.Message;
import vn.letmewash.android.services.APIResult;
import vn.letmewash.android.services.APIService;

import static vn.letmewash.android.controllers.Constants.LOGIN_REQUEST_CODE;
import static vn.letmewash.android.controllers.Constants.PAGE_SIZE_DEFAULT;

public class MessageActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    SwipeMenuListView lvMessage;
    RelativeLayout layoutData, layoutStatus;
    ProgressBar pgbStatus;
    TextView tvStatus;
    Button btLogin;
    ImageView imgIconStatus, imgIconRetry;

    Context mContext;
    List<Message> mMessages;
    MessageAdapter mAdapter;
    Authentication mAuthentication;
    Boolean isLoading = false;
    Boolean userScrolled = false;

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

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

    protected void startLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
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

    protected void loadMessages(Customer customer, int skip, int take) {
        if (isLoading == false && customer != null) {
            isLoading = true;
            if (mMessages.size() == 0) {
                showLoadingStatus();
            }
            String phone = customer.getPhone();
            LoadMessagesTask task = new LoadMessagesTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phone, String.valueOf(skip), String.valueOf(take));
        }
    }

    protected void loadMessagesCompleted(APIResult result) {
        hideLoadingStatus();
        if (result != null && result.isSuccess()) {
            List<Message> messages = Message.fromJsonArray(result.getData());
            if (mMessages != null) {
                mMessages.addAll(messages);
                mAdapter.notifyDataSetChanged();
            }
            showDataLayout();
            if (mMessages.size() == 0) {
                showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
            }
        } else {
            showMessageStatus(getString(R.string.text_no_data), R.drawable.ic_cancel_2);
            checkNetworkStatus();
        }
        isLoading = false;
    }

    protected void updateMessageStatus(Message message, int status) {
        if (message != null) {
            int messageId = message.getMessageId();
            UpdateMessageStatusTask task = new UpdateMessageStatusTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, messageId, status);
        }
    }

    protected void updateMessageStatusCompleted(APIResult result) {
        // do nothing...
    }

    protected void markAllAsRead() {

        // Update message status from list
        for (Message message : mMessages) {
            message.setStatus(Message.STATUS_READ);
        }
        mAdapter.notifyDataSetChanged();

        // Update message status to serve
        Customer customer = mAuthentication.getLoginCustomer();
        if (customer != null) {
            String phone = customer.getPhone();
            MarkAllAsReadTask task = new MarkAllAsReadTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phone);
        }
    }

    protected void markAllAsReadCompleted(APIResult result) {
        Log.d("DGB", "Update message status completed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mContext = this;
        mAuthentication = new Authentication(this);
        mMessages = new ArrayList<>();
        mAdapter = new MessageAdapter(this, mMessages);

        // Set display home
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.title_message));
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // get widgets
        lvMessage = findViewById(R.id.lvMessage);
        lvMessage.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        lvMessage.setAdapter(mAdapter);
        lvMessage.setOnScrollListener(this);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create deleted menu
                SwipeMenuItem menuDeleted = new SwipeMenuItem(mContext);
                menuDeleted.setWidth(dp2px(90));
                menuDeleted.setBackground(R.color.colorPrimary);
                menuDeleted.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(menuDeleted);
            }
        };
        lvMessage.setMenuCreator(creator);
        lvMessage.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (mMessages.size() > position) {

                    Log.d("DBG", "Remove item " + position);

                    Message message = mMessages.get(position);
                    mMessages.remove(message);
                    mAdapter.notifyDataSetChanged();
                    updateMessageStatus(message, Message.STATUS_REMOVED);
                    return  true;
                }
                return false;
            }
        });
        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMessages.size() > position) {

                    Log.d("DBG", "Show message " + position);

                    // Start message details activity
                    Message message = mMessages.get(position);
                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("Message", message);
                    intent.putExtras(data);
                    startActivityForResult(intent,1000);

                    // Update message list status
                    message.setStatus(Message.STATUS_READ);
                    mAdapter.notifyDataSetChanged();

                    // Update message status to server
                    updateMessageStatus(message, Message.STATUS_READ);
                }
            }
        });

        layoutData = findViewById(R.id.layoutMessage);
        layoutStatus = findViewById(R.id.layoutStatus);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMessages.clear();
        if (mAuthentication.isAuthenticated()) {
            Customer customer = mAuthentication.getLoginCustomer();
            loadMessages(customer, mMessages.size(), PAGE_SIZE_DEFAULT);
        } else {
            showRequireLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_mark_all_as_read:
                markAllAsRead();
                return true;

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
        if (userScrolled && firstVisibleItem + visibleItemCount == mMessages.size()) {
            Log.d("DBG", "Scroll to end...");

            userScrolled = false;
            Customer customer = mAuthentication.getLoginCustomer();
            if (customer != null) {
                loadMessages(customer, mMessages.size(), PAGE_SIZE_DEFAULT);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            userScrolled = true;
        }
    }

    class LoadMessagesTask extends AsyncTask<String, Void, APIResult> {
        @Override
        protected APIResult doInBackground(String... strings) {
            try {
                if (strings != null && strings.length >= 3) {
                    String phone = strings[0];
                    int skip = Integer.parseInt(strings[1]);
                    int take = Integer.parseInt(strings[2]);
                    return APIService.getMessages(phone, skip, take);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(APIResult result) {
            loadMessagesCompleted(result);
        }
    }

    class UpdateMessageStatusTask extends AsyncTask<Integer, Void, APIResult> {
        @Override
        protected APIResult doInBackground(Integer... integers) {
            try {
                if (integers != null && integers.length >= 2) {
                    int messageId = integers[0];
                    int status = integers[1];
                    return APIService.updateMessageStatus(messageId, status);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(APIResult result) {
            updateMessageStatusCompleted(result);
        }
    }

    class MarkAllAsReadTask extends AsyncTask<String, Void, APIResult> {
        @Override
        protected APIResult doInBackground(String... strings) {
            try {
                if (strings != null && strings.length > 0) {
                    String phone = strings[0];
                    return APIService.setAllMessageAsRead(phone);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(APIResult result) {
            markAllAsReadCompleted(result);
        }
    }
}

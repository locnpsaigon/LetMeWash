package vn.letmewash.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import vn.letmewash.android.R;
import vn.letmewash.android.libs.Common;
import vn.letmewash.android.models.Order;

/**
 * Created by camel on 12/6/17.
 */

public class OrderAdapter extends BaseAdapter {

    Context mContext;
    List<Order> mData;

    static class ViewHolder {
        TextView tvOrderNo;
        TextView tvDateTime;
        TextView tvTitle;
        TextView tvAmount;
        TextView tvStatus;
    }

    public OrderAdapter(Context mContext, List<Order> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public Object getItem(int position) {
        return mData != null && mData.size() > position ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // init convert view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.order_item, null);
            holder = new ViewHolder();
            holder.tvOrderNo = convertView.findViewById(R.id.tvOrderNo);
            holder.tvDateTime = convertView.findViewById(R.id.tvDateTime);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvAmount = convertView.findViewById(R.id.tvAmount);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = (Order) getItem(position);
        holder.tvOrderNo.setText("Đơn hàng: #" + order.getOrderId());
        Date orderDate = Common.getDate(order.getDate(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
        if (orderDate != null) {
            holder.tvDateTime.setText("Thời gian: " + Common.formatDate(orderDate, "dd/MM HH:mm"));
        }
        holder.tvTitle.setText(order.getTitle());
        holder.tvAmount.setText(Common.formatDecimal(order.getAmount()) + "Đ");
        switch (order.getStatus()) {
            case Order.STATUS_OPENED:
                holder.tvStatus.setText(mContext.getString(R.string.status_opened));
                holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                break;
            case Order.STATUS_PENDING:
                holder.tvStatus.setText(mContext.getString(R.string.status_processing));
                holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                break;
            case Order.STATUS_PROCESSING:
                holder.tvStatus.setText(mContext.getString(R.string.status_processing));
                holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlue));
                break;
            case Order.STATUS_FINSIHED:
                holder.tvStatus.setText(mContext.getString(R.string.status_finished));
                holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkGray));
                break;
            case Order.STATUS_CLOSED:
                holder.tvStatus.setText(mContext.getString(R.string.status_close));
                holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                break;
            default:
                holder.tvStatus.setText(mContext.getString(R.string.status_opened));
                holder.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                break;
        }

        return convertView;
    }
}

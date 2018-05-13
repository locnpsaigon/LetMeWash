package vn.letmewash.android.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import vn.letmewash.android.ServiceDetailsActivity;
import vn.letmewash.android.R;
import vn.letmewash.android.libs.Common;
import vn.letmewash.android.models.ServiceDetails;

/**
 * Created by camel on 11/26/17.
 */

public class ServiceDetailsAdapter extends BaseAdapter {

    Context mContext;
    List<ServiceDetails> mData;
    HashMap mCheckedItems;

    public ServiceDetailsAdapter(Context mContext, List<ServiceDetails> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mCheckedItems = new HashMap();
    }

    private Boolean isChecked(int position) {
        ServiceDetails details = (ServiceDetails) getItem(position);
        if (details != null) {
            int key =  details.getItemId();
            if (mCheckedItems.containsKey(key)) {
                return (Boolean) mCheckedItems.get(key);
            }
        }
        return false;
    }

    private void setChecked(int position, boolean checked) {
        ServiceDetails details = (ServiceDetails) getItem(position);
        if (details != null) {
            int key = details.getItemId();
            mCheckedItems.remove(key);
            if (checked) {
                mCheckedItems.put(key, checked);
            }
        }
        notifyDataSetChanged();
    }

    private void toggle(int position) {
        setChecked(position, !isChecked(position));
        notifyDataSetChanged();
    }

    public Boolean isAllChecked() {
        if (mData != null) {
            for (ServiceDetails details : mData) {
                int key =  details.getItemId();
                if (mCheckedItems.containsKey(key)) {
                    Boolean wasChecked = (Boolean) mCheckedItems.get(key);
                    if (!wasChecked) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void setAllChecked(Boolean checked) {
        mCheckedItems.clear();
        if (mData != null) {
            for (ServiceDetails details : mData) {
                int key =  details.getItemId();
                mCheckedItems.put(key, checked);
            }
        }
        notifyDataSetChanged();
    }

    public HashMap getCheckedItems() {
        return mCheckedItems;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        // init convert view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.service_details_item, null);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvPriceOriginal = convertView.findViewById(R.id.tvPriceOriginal);
            holder.tvPriceOriginal.setPaintFlags(holder.tvPriceOriginal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDiscountPercent = convertView.findViewById(R.id.tvDiscountPercent);
            holder.cbxSelected = convertView.findViewById(R.id.cbxSelected);
            holder.cbxSelected.setClickable(false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // show car service details
        final ServiceDetails details = (ServiceDetails) getItem(position);
        if (details != null) {
            holder.tvTitle.setText(details.getItemName());
            holder.cbxSelected.setChecked(isChecked(position));
            boolean hasPrice = details.getPrice() > 0;
            boolean hasDiscount = (details.getPriceOriginal() - details.getPrice()) > 0;
            if (hasPrice) {
                holder.tvPrice.setText(Common.formatDecimal(details.getPrice()) + "Đ");
                holder.tvPrice.setVisibility(View.VISIBLE);
                if (hasDiscount) {
                    double percent = 100 - (details.getPrice() / details.getPriceOriginal()) * 100;
                    if (percent > 0) {
                        // show discount info
                        holder.tvPriceOriginal.setText(Common.formatDecimal(details.getPriceOriginal()) + "Đ");
                        holder.tvPriceOriginal.setVisibility(View.VISIBLE);
                        holder.tvDiscountPercent.setText("-" + Common.formatDecimal(percent) + "%");
                        holder.tvDiscountPercent.setVisibility(View.VISIBLE);
                    } else {
                        hasDiscount = false;
                    }
                }
            } else {
                holder.tvPrice.setText("Đang cập nhật giá");
                holder.tvPrice.setVisibility(View.VISIBLE);
            }
            if (!hasDiscount) {
                holder.tvPriceOriginal.setText("");
                holder.tvPriceOriginal.setVisibility(View.INVISIBLE);
                holder.tvDiscountPercent.setText("");
                holder.tvDiscountPercent.setVisibility(View.INVISIBLE);
            }
        }
        convertView.setClickable(true);
        convertView.setFocusable(true);
        final int pos = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DBG", "Item " + pos + " clicked!!!");
                toggle(position);
                // update payment amount
                ServiceDetailsActivity activity = (ServiceDetailsActivity) mContext;
                if (activity != null) {
                    activity.showCashAmount();
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvPrice;
        TextView tvPriceOriginal;
        TextView tvDiscountPercent;
        CheckBox cbxSelected;
    }
}

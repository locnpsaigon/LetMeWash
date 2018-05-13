package vn.letmewash.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import vn.letmewash.android.CartActivity;
import vn.letmewash.android.R;
import vn.letmewash.android.controllers.Cart;
import vn.letmewash.android.libs.Common;
import vn.letmewash.android.models.ServiceBooking;
import vn.letmewash.android.models.ServiceDetails;

/**
 * Created by camel on 11/29/17.
 */

public class CartAdapter extends BaseAdapter {

    Context mContext;
    List<ServiceBooking> mData;
    Boolean showRemoveButton;

    public CartAdapter(Context mContext, List<ServiceBooking> bookings) {
        this.mContext = mContext;
        this.mData = bookings;
        this.showRemoveButton = true;
    }

    public void setShowRemoveButton(Boolean isShow) {
        this.showRemoveButton = isShow;
    }

    @Override
    public int getCount() {
        int count = 0;
        for (ServiceBooking csb : mData) {
            count++;
            count += csb.getDetails().size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        int idx = -1;
        for (ServiceBooking csb : mData) {
            idx++;
            if (idx == position) {
                return csb;
            }
            for (ServiceDetails details : csb.getDetails()) {
                idx++;
                if (idx == position) {
                    return details;
                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cart_item, null);
            holder = new ViewHolder();
            holder.layoutMaster = convertView.findViewById(R.id.layout_master);
            holder.layoutDetails = convertView.findViewById(R.id.layout_details);
            holder.tvGroup = convertView.findViewById(R.id.tvGroup);
            holder.tvItemName = convertView.findViewById(R.id.tvItemName);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.imgIcon = convertView.findViewById(R.id.imgIcon);
            holder.imgRemove = convertView.findViewById(R.id.imgRemove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get list item
        Object item = getItem(position);

        // show group info
        if (item.getClass() == ServiceBooking.class) {

            holder.layoutMaster.setVisibility(View.VISIBLE);
            holder.layoutDetails.setVisibility(View.INVISIBLE);

            ServiceBooking csb = (ServiceBooking) item;
            holder.tvGroup.setText(csb.getServiceName());
        }

        // show details info
        if (item.getClass() == ServiceDetails.class) {
            holder.layoutMaster.setVisibility(View.INVISIBLE);
            holder.layoutDetails.setVisibility(View.VISIBLE);

            ServiceDetails details = (ServiceDetails) item;
            holder.tvItemName.setText(details.getItemName());
            holder.tvPrice.setText(Common.formatDecimal(details.getPrice()) + "Đ");
            holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Cart cart = Cart.getInstance();
                        cart.remove(mContext, position);
                        CartActivity activity = (CartActivity) mContext;
                        if (activity != null) {
                            activity.updateCartStatus();
                        }
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.imgRemove.setVisibility(showRemoveButton ? View.VISIBLE : View.INVISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout layoutMaster, layoutDetails;
        ImageView imgIcon, imgRemove;
        TextView tvGroup, tvItemName, tvPrice;
    }
}

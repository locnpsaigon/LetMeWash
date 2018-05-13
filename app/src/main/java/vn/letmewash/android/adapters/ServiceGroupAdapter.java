package vn.letmewash.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.letmewash.android.R;
import vn.letmewash.android.models.ServiceGroup;

/**
 * Created by camel on 11/21/17.
 */

public class ServiceGroupAdapter extends BaseAdapter {

    Context mContext;
    List<ServiceGroup> mData;

    public ServiceGroupAdapter(Context context, List<ServiceGroup> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.service_group_item, null);
            holder.imgIcon = convertView.findViewById(R.id.imgIcon);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // show list item info
        ServiceGroup serviceGroup = (ServiceGroup) getItem(position);
        if (serviceGroup != null) {
            String iconUrl = serviceGroup.getIconURL();
            Picasso.with(mContext).load(iconUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_unavailable)
                    .into(holder.imgIcon);
            holder.tvTitle.setText(serviceGroup.getGroupName().toUpperCase());
            holder.tvDescription.setText(serviceGroup.getDescription());
        }

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mData != null && mData.size() > position ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView imgIcon;
        TextView tvTitle;
        TextView tvDescription;
    }
}

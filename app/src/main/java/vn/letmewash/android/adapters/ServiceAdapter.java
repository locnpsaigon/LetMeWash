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
import vn.letmewash.android.models.Service;

/**
 * Created by camel on 11/24/17.
 */

public class ServiceAdapter extends BaseAdapter {

    Context mContext;
    List<Service> mData;

    static class ViewHolder {
        ImageView imgIcon;
        TextView tvTitle;
        TextView tvDescripton;
    }

    public ServiceAdapter(Context mContext, List<Service> mData) {
        this.mContext = mContext;
        this.mData = mData;
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
            convertView = inflater.inflate(R.layout.service_item, null);
            holder = new ViewHolder();
            holder.imgIcon = convertView.findViewById(R.id.imgIcon);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvDescripton = convertView.findViewById(R.id.tvDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // show list item
        Service service = (Service) getItem(position);
        if (service != null) {
            String iconUrl = service.getIconURL();
            Picasso.with(mContext).load(iconUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_unavailable)
                    .into(holder.imgIcon);
            holder.tvTitle.setText(service.getServiceName());
            holder.tvDescripton.setText(service.getDescription());
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mData != null && mData.size() > position ? mData.get(position) : null;
    }


}

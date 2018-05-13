package vn.letmewash.android.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.letmewash.android.R;
import vn.letmewash.android.models.Message;

import static vn.letmewash.android.controllers.Constants.TEXT_SHORT_LENGTH;

/**
 * Created by camel on 11/22/17.
 */

public class MessageAdapter extends BaseAdapter {

    Context mContext;
    List<Message> mData;

    public MessageAdapter(Context mContext, List<Message> mData) {
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.app_message_item, null);
            holder = new ViewHolder();
            holder.imgIcon = convertView.findViewById(R.id.imgIcon);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvMessgae = convertView.findViewById(R.id.tvMessage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = (Message) getItem(position);
        if (message != null) {
            // get mail title/message
            String shortTitle = message.getTitle();
            String shortMessage = message.getMessage();

            // shorten text
            if (shortTitle.length() > TEXT_SHORT_LENGTH) {
                shortTitle = shortTitle.substring(0, TEXT_SHORT_LENGTH) + "...";
            }
            if (shortMessage.length() > TEXT_SHORT_LENGTH) {
                shortMessage = shortMessage.substring(0, TEXT_SHORT_LENGTH);
            }

            // show mail message
            if (message.getStatus() == Message.STATUS_READ) {
                holder.imgIcon.setImageResource(R.drawable.ic_read_message);
            } else {
                holder.imgIcon.setImageResource(R.drawable.ic_filled_message);
            }
            holder.tvTitle.setText(shortTitle);
            holder.tvMessgae.setText(shortMessage);
        }
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return (mData != null && mData.size() > position) ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView imgIcon;
        TextView tvTitle;
        TextView tvMessgae;
    }
}

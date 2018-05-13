package vn.letmewash.android.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by camel on 12/9/17.
 */

public class MessageStatus implements Serializable {

    private int Read;
    private int Unread;

    public static MessageStatus fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, MessageStatus.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<MessageStatus> fromJsonArray(String jsonArray) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonArray, new TypeToken<List<MessageStatus>>(){}.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRead() {
        return Read;
    }

    public void setRead(int read) {
        Read = read;
    }

    public int getUnread() {
        return Unread;
    }

    public void setUnread(int unread) {
        Unread = unread;
    }
}

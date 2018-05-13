package vn.letmewash.android.libs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import vn.letmewash.android.MainActivity;
import vn.letmewash.android.R;

/**
 * Created by camel on 12/9/17.
 */

public class NotificationHelper {

    static int id = 0;

    Context mContext;

    public NotificationHelper(Context context) {
        this.mContext = context;
    }

    public void showNotificaiotn(String title, String message, String sumamary) {
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_launcher);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext.getApplicationContext())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_alarm)
                .setLargeIcon(largeIcon).setContentText(message)
                .setSound(alarmSound);;

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(sumamary);
        mBuilder.setStyle(bigText);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext.getApplicationContext(),
                MainActivity.class);

        // The stack builder object will contain an artificial back
        // stack for
        // the
        // started Activity.
        // getApplicationContext() ensures that navigating backward from
        // the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder
                .create(mContext.getApplicationContext());

        // Adds the back stack for the Intent (but not the Intent
        // itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the
        // stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(id++, mBuilder.build());
    }
}

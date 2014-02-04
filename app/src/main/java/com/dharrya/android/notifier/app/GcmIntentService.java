package com.dharrya.android.notifier.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dharrya.android.notifier.app.database.Notify;
import com.dharrya.android.notifier.app.database.NotifyProvider;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(Preferences.LOG_TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                String title = extras.get("title").toString();
                String message = extras.get("message").toString();
                String date = extras.get("date").toString();
                sendNotification(title, message);
                saveNotification(title, message, date);

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(Preferences.LOG_TAG, "Deleted messages on server: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void saveNotification(String title, String message, String date) {
        ContentValues values = new ContentValues();
        values.put(Notify.COL_TITLE, title);
        values.put(Notify.COL_MESSAGE, message);
        values.put(Notify.COL_DATE, date);
        ContentResolver resolver = getContentResolver();
        resolver.insert(NotifyProvider.URI_NOTIFIES, values);
    }

    private void sendNotification(String title, String msg) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NotifyListActivity.class), 0);

        long[] pattern = {500,300,100,300,500,300,200,300,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setLights(Color.BLUE, 500, 500)
                        .setVibrate(pattern)
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

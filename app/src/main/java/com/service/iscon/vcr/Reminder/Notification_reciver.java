package com.service.iscon.vcr.Reminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.service.iscon.vcr.Activities.HomeActivity;
import com.service.iscon.vcr.R;

/**
 * Created by admin on 6/6/2016.
 */
public class Notification_reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
        .NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context,HomeActivity.class);

        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_intent
        ,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notify For Chant")
                .setContentText("Hare Krishna It's Time to Chant")
                .setSound(sound)
                .setAutoCancel(true);

        notificationManager.notify(100,builder.build());
    }
}

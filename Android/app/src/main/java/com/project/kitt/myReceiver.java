package com.project.kitt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//for alarm manager: https://stackoverflow.com/questions/34494910/setting-notification-alarm-in-an-android-application
//for notification builder & channel: https://www.youtube.com/watch?v=ub4_f6ksxL0

public class myReceiver extends BroadcastReceiver {
    private static final int uniqueID = 0;
    private final String CHANNEL_ID = "channel";

    /**
     * The goBack intent is used for when the user clicks on the notification it takes them back to the app
     * @param context needed to create the notification channel and intents
     * @param intent used to get the name of the food since it was passed through the intent when setting alarm
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent goBack = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(goBack);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
        String nameOfFood = intent.getStringExtra("foodName");
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info);
        builder.setContentTitle("Don't forget, "+ nameOfFood + " is expiring soon!");
        builder.setContentText("You have food expiring soon! Please check app for details.");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(uniqueID, builder.build());
    }

    /**
     *
     * Notification channel is the group for the food notifications
     * @param context used to register the channel with the system
     *
     */

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notif name";
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

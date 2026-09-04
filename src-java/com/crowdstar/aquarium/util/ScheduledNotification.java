package com.crowdstar.aquarium.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.crowdstar.aquarium.Aquarium;

public class ScheduledNotification extends BroadcastReceiver {

    public static final String EXTRA_ACTION = "notification_action";

    public static final String EXTRA_BODY = "notification_body";

    public static final String EXTRA_TANK_ID = "notification_tank_id";

    public void onReceive(Context paramContext, Intent paramIntent) {
        NotificationManager notificationManager =
            (NotificationManager) paramContext.getSystemService("notification");
        String str1 = paramIntent.getStringExtra("notification_body");
        String str2 = paramIntent.getStringExtra("notification_action");
        paramIntent.getStringExtra("notification_tank_id");
        Notification notification = new Notification(
            2130837506,
            str1,
            System.currentTimeMillis()
        );
        PendingIntent pendingIntent = PendingIntent.getActivity(
            paramContext,
            0,
            new Intent(paramContext, Aquarium.class),
            0
        );
        notification.setLatestEventInfo(
            paramContext.getApplicationContext(),
            str1,
            str2,
            pendingIntent
        );
        notificationManager.notify(1, notification);
    }
}

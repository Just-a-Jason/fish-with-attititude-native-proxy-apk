package com.crowdstar.aquarium.util;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class LocalNotifications {
  public static void cancelNotifications(Context paramContext) {
    ((AlarmManager)paramContext.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(paramContext, 0, new Intent(paramContext, ScheduledNotification.class), 134217728));
  }
  
  public static void clearNotifications(Context paramContext) {
    ((NotificationManager)paramContext.getSystemService("notification")).cancelAll();
  }
  
  public static void scheduleFutureNotification(Context paramContext, String paramString1, String paramString2, long paramLong, String paramString3) {
    if (!paramContext.getResources().getBoolean(2131165184)) {
      AlarmManager alarmManager = (AlarmManager)paramContext.getSystemService("alarm");
      Intent intent = new Intent(paramContext, ScheduledNotification.class);
      intent.putExtra("notification_body", paramString1);
      intent.putExtra("notification_action", paramString2);
      intent.putExtra("notification_tank_id", paramString3);
      alarmManager.set(0, paramLong, PendingIntent.getBroadcast(paramContext, (int)System.currentTimeMillis(), intent, 134217728));
    } 
  }
}


/* Location:              /home/jason/Pobrane/fish.jar!/com/crowdstar/aquarium/util/LocalNotifications.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
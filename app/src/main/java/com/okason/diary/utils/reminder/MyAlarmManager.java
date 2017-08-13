package com.okason.diary.utils.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.okason.diary.R;
import com.okason.diary.models.Task;
import com.okason.diary.utils.Constants;

import java.util.Calendar;



public class MyAlarmManager {

    private static final String TAG = "MyAlarmManager";
    private static final Class COMPONENT_CLASS = AlarmReceiver.class;


    private MyAlarmManager() {
    }


    public static void createAlarm(Context context, Task task) {
        Notification notification = buildNotification(context, task);

        Intent intent = new Intent(context, COMPONENT_CLASS);
        intent.putExtra(Constants.TODO_LIST_ID, task.getId());
        intent.putExtra(Constants.ALARM_NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(task.getId()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerTime = getTriggerTime(context, task);
        long intervalTime = getIntervalTime(task, triggerTime);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalTime, pendingIntent);
    }


    public static void deleteAlarm(Context context, PendingIntent pendingIntent) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {
            alarmMgr.cancel(pendingIntent);
        }
    }


    private static long getIntervalTime(Task task, long baseDateTimeAtMillis) {
        return task.getRepeatFrequency().getInterval(baseDateTimeAtMillis);
    }


    private static long getTriggerTime(Context context, Task task) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String taskNotificationMinutes = prefs.getString(context.getString(R.string.pref_tasknotification_key), "0");
        long taskNotificationMillis = Integer.parseInt(taskNotificationMinutes) * 60 * 1000;

        Calendar dueTime = Calendar.getInstance();
        dueTime.setTimeInMillis(task.getDueDateAndTime() - taskNotificationMillis);

        return dueTime.before(Calendar.getInstance())
                ? Calendar.getInstance().getTimeInMillis()
                : dueTime.getTimeInMillis();
    }


    private static Notification buildNotification(Context context, Task task) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibrate = prefs.getBoolean(context.getString(R.string.pref_vibration_key), true);
        String sound = prefs.getString(context.getString(R.string.pref_sound_key), null);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription())
                .setAutoCancel(true);

        if (vibrate) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        if (!TextUtils.isEmpty(sound)) {
            notificationBuilder.setSound(Uri.parse(sound));
        }

        return notificationBuilder.build();
    }
}

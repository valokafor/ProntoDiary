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
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.models.realmentities.TaskEntity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.DateHelper;
import com.okason.diary.utils.date.TimeUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class MyAlarmManager {

    private static final String TAG = "MyAlarmManager";
    private static final Class COMPONENT_CLASS = AlarmReceiver.class;


    private MyAlarmManager() {
    }


    public static void createAlarm(Context context, TaskEntity task) {
        Notification notification = buildNotification(context, task);

        Intent intent = new Intent(context, AlarmReceiver.class);
        String serializedTask = new Gson().toJson(task);
        intent.putExtra(Constants.SERIALIZED_TASK, serializedTask);
        intent.putExtra(Constants.ALARM_NOTIFICATION, notification);
        Log.d(TAG, "Task Id: " + task.getId());

        int pendingIntentId = TimeUtils.currentTimeMillis(task.getDateCreated());
        Log.d(TAG, "pendingIntentId :" + pendingIntentId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerTime = getTriggerTime(context, task);
        long intervalTime = getIntervalTime(task, triggerTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(intervalTime);

        Log.d(TAG, "triggerTime : " + DateFormat.getTimeFormat(context)
                .format(triggerTime));

        Log.d(TAG, "intervalTime :" + minutes + " minutes");

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalTime, pendingIntent);
    }


    public static void deleteAlarm(Context context, PendingIntent pendingIntent) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {
            alarmMgr.cancel(pendingIntent);
        }
    }


    private static long getIntervalTime(TaskEntity task, long baseDateTimeAtMillis) {
        long interval = TimeUtils.getInterval(baseDateTimeAtMillis, task.getRepeatFrequency());
        return interval;
    }


    private static long getTriggerTime(Context context, TaskEntity task) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String taskNotificationMinutes = prefs.getString(context.getString(R.string.pref_tasknotification_key), "0");


        Log.d(TAG, "taskNotificationMinutes: " + taskNotificationMinutes);
        long taskNotificationMillis = Integer.parseInt(taskNotificationMinutes) * 60 * 1000;

        Log.d(TAG, "taskNotificationMillis: " + taskNotificationMillis);

        Calendar dueTime = Calendar.getInstance();
        Log.d(TAG, "task.getDueDateAndTime: " + TimeUtils.getReadableModifiedDateWithTime(task.getDueDateAndTime()));
        dueTime.setTimeInMillis(task.getDueDateAndTime() - taskNotificationMillis);

        Log.d(TAG, "duetime: " + DateFormat.getTimeFormat(context)
                .format(dueTime.getTime()));

        boolean shouldSetAlarm = dueTime.after(Calendar.getInstance());

        Log.d(TAG, "shouldSetAlarm :" + shouldSetAlarm);

        if (shouldSetAlarm){
            return dueTime.getTimeInMillis();
        } else {
            return Calendar.getInstance().getTimeInMillis();
        }

    }


    private static Notification buildNotification(Context context, TaskEntity task) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibrate = prefs.getBoolean(context.getString(R.string.pref_vibration_key), true);
        String sound = prefs.getString(context.getString(R.string.pref_sound_key), null);
        String time = DateHelper.getTimeShort(context, task.getDueDateAndTime());
        String status = TimeUtils.getSubTaskStatus(task);
        String message = "Due on " + time + " \\n" + status;


        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(task.getTitle())
                .setContentText(message)
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

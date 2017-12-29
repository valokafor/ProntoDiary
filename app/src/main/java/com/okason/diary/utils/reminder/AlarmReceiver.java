package com.okason.diary.utils.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.okason.diary.models.Task;
import com.okason.diary.ui.todolist.AddTaskActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private static final Class COMPONENT_CLASS = AddTaskActivity.class;



    @Override
    public void onReceive(final Context context, final Intent intent) {

            final String serializedTask = intent.getStringExtra(Constants.SERIALIZED_TASK);
            Log.d(TAG, "serializedTask : " + serializedTask);
            Gson gson = new Gson();
            Task currentTask = gson.fromJson(serializedTask, Task.class);
            if (currentTask != null) {

                Notification notification = intent.getParcelableExtra(Constants.ALARM_NOTIFICATION);
                int pendingIntentId = TimeUtils.currentTimeMillis(currentTask.getDateCreated());
                Log.d(TAG, "pendingIntentId :" + pendingIntentId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Delete alarm if date of task expired
                if (currentTask != null && Calendar.getInstance().after(currentTask.getRepeatEndDate())) {
                    MyAlarmManager.deleteAlarm(context, pendingIntent);
                    return;
                }

                // this condition is for weekdays
                if (currentTask.getRepeatFrequency().equals(Constants.REMINDER_WEEK_DAYS)) {
                    int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                        return;
                    }
                }

                intent.setClass(context, COMPONENT_CLASS);
                notification.contentIntent =
                        PendingIntent.getActivity(context, pendingIntentId, intent, FLAG_UPDATE_CURRENT);

                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(pendingIntentId, notification);
            }

    }


}

package com.okason.diary.utils.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.okason.diary.NoteListActivity;
import com.okason.diary.models.Task;
import com.okason.diary.utils.Constants;

import java.util.Calendar;



public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private static final Class COMPONENT_CLASS = NoteListActivity.class;


    @Override
    public void onReceive(Context context, Intent intent) {
        String taskId = intent.getStringExtra(Constants.TASK_ID);
        Task task = new Task();
        Notification notification = intent.getParcelableExtra(Constants.ALARM_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(taskId), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Delete alarm if date of task expired
        if (task != null && Calendar.getInstance().after(task.getRepeatEndDate())) {
            MyAlarmManager.deleteAlarm(context, pendingIntent);
            return;
        }

        // this condition is for weekdays
        if (task.getRepeatFrequency().equals(Reminder.WEEKDAYS)) {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                return;
            }
        }

        intent.setClass(context, COMPONENT_CLASS);
        notification.contentIntent =
                PendingIntent.getActivity(context, Integer.parseInt(taskId), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(taskId), notification);

    }
}

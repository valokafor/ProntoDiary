package com.okason.diary.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.okason.diary.R;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.ui.todolist.AddSubTaskActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.DateHelper;
import com.okason.diary.utils.date.TimeUtils;

public class NotificationUtil {

    public static void createNotification(Context context, ProntoTask prontoTask) {
        // Create intent for notification onClick behaviour
        Intent viewIntent = new Intent(context, AddSubTaskActivity.class);
        viewIntent.putExtra(Constants.TASK_ID, prontoTask.getId());
        viewIntent.putExtra("NOTIFICATION_DISMISS", true);
        PendingIntent pending = PendingIntent.getActivity(context, prontoTask.getReminder().getId(), viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        buildNotification(context, prontoTask, pending);


    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public static void buildNotification(Context context, ProntoTask prontoTask, PendingIntent pending) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibrate = prefs.getBoolean(context.getString(R.string.pref_vibration_key), true);
        String sound = prefs.getString(context.getString(R.string.pref_sound_key), null);
        String time = DateHelper.getTimeShort(context, prontoTask.getReminder().getDateAndTime());
        String status = TimeUtils.getSubTaskStatus(prontoTask);
        String message = "Due on " + time + " \\n" + status;


        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(prontoTask.getTitle())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(prontoTask.getDescription()))
                .setContentText(prontoTask.getDescription() + " " + message)
                .setTicker(prontoTask.getTitle())
                .setContentIntent(pending)
                .setAutoCancel(true);


        if (vibrate) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        if (!TextUtils.isEmpty(sound)) {
            notificationBuilder.setSound(Uri.parse(sound));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(prontoTask.getReminder().getId(), notificationBuilder.build());
    }
}
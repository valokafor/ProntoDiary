package com.okason.diary.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.okason.diary.data.RealmManager;
import com.okason.diary.data.ReminderDao;
import com.okason.diary.models.Reminder;
import com.okason.diary.utils.Constants;

import java.util.Calendar;

import io.realm.Realm;

public class AlarmUtil {

    public static void setAlarm(Context context, Intent intent, int notificationId, Calendar calendar) {
        intent.putExtra("NOTIFICATION_ID", notificationId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public static void setNextAlarm(Context context, int reminderId) {
        try(Realm realm = RealmManager.setUpRealm()) {
            ReminderDao reminderDao = new ReminderDao(realm);
            Reminder reminder = reminderDao.getReminderById(reminderId);
            Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminder.getDateAndTime());
            calendar.set(Calendar.SECOND, 0);

            switch (reminder.getRepeatType()) {
                case Constants.HOURLY:
                    calendar.add(Calendar.HOUR, reminder.getInterval());
                    break;
                case Constants.DAILY:
                    calendar.add(Calendar.DATE, reminder.getInterval());
                    break;
                case Constants.WEEKLY:
                    calendar.add(Calendar.WEEK_OF_YEAR, reminder.getInterval());
                    break;
                case Constants.MONTHLY:
                    calendar.add(Calendar.MONTH, reminder.getInterval());
                    break;
                case Constants.YEARLY:
                    calendar.add(Calendar.YEAR, reminder.getInterval());
                    break;
                case Constants.SPECIFIC_DAYS:
                    Calendar weekCalendar = (Calendar) calendar.clone();
                    weekCalendar.add(Calendar.DATE, 1);
                    for (int i = 0; i < 7; i++) {
                        int position = (i + (weekCalendar.get(Calendar.DAY_OF_WEEK) - 1)) % 7;
                        if (reminder.getDaysOfWeekList().get(position)) {
                            calendar.add(Calendar.DATE, i + 1);
                            break;
                        }
                    }
                    break;
            }

            reminderDao.setDateAndTime(calendar.getTimeInMillis(), reminderId);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            setAlarm(context, alarmIntent, reminder.getId(), calendar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
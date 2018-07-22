package com.okason.diary.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.okason.diary.data.RealmManager;
import com.okason.diary.data.ReminderDao;
import com.okason.diary.models.Reminder;
import com.okason.diary.ui.todolist.AddTaskActivity;

import io.realm.Realm;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private static final Class COMPONENT_CLASS = AddTaskActivity.class;




    @Override
    public void onReceive(final Context context, final Intent intent) {

        try (Realm realm = RealmManager.setUpRealm()) {
            ReminderDao reminderDao = new ReminderDao(realm);
            Reminder reminder = reminderDao.getReminderById(intent.getIntExtra("NOTIFICATION_ID", 0));

            if (reminder != null) {
                reminderDao.incrementNumberOfTimesReminderShown(reminder.getId());

                NotificationUtil.createNotification(context, reminder.getParentProntoTask());

                // Check if new alarm needs to be set
                if (reminder.getNumberToShow() > reminder.getNumberShown() || reminder.isIndefinite()) {
                    AlarmUtil.setNextAlarm(context, reminder.getId());
                }
                Intent updateIntent = new Intent("BROADCAST_REFRESH");
                LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




    }


}

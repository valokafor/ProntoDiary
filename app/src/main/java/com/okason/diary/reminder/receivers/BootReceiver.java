package com.okason.diary.reminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.okason.diary.data.RealmManager;
import com.okason.diary.data.ReminderDao;
import com.okason.diary.reminder.AlarmReceiver;
import com.okason.diary.reminder.AlarmUtil;
import com.okason.diary.reminder.DateAndTimeUtil;
import com.okason.diary.models.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Realm realm = RealmManager.setUpRealm();
        ReminderDao reminderDao = new ReminderDao(realm);
        RealmResults<Reminder> reminderList = reminderDao.getAllReminders();
        List<Reminder> activeReminders = new ArrayList<>();
        for (Reminder reminder: reminderList){
            if (reminder.getNumberShown() < reminder.getNumberToShow() || reminder.isIndefinite()){
                activeReminders.add(reminder);
            }
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        for (Reminder reminder : activeReminders) {
            Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminder.getDateAndTime());
            calendar.set(Calendar.SECOND, 0);
            AlarmUtil.setAlarm(context, alarmIntent, reminder.getId(), calendar);
        }
    }
}
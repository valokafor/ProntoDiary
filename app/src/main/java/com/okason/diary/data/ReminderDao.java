package com.okason.diary.data;

import com.okason.diary.reminder.Reminder;

import java.util.Calendar;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by valokafor on 5/27/18.
 */

public class ReminderDao {
    private Realm realm;
    private final static String TAG = "ReminderDao";

    public ReminderDao(Realm realm) {
        this.realm = realm;
    }


    public Reminder createNewReminder() {
        String reminderId = UUID.randomUUID().toString();
        realm.beginTransaction();
        Reminder reminder = realm.createObject(Reminder.class, reminderId);
        reminder.setDateCreated(System.currentTimeMillis());
        reminder.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return reminder;
    }

    public Reminder getReminderById(String reminderId) {
        try {
            Reminder reminder = realm.where(Reminder.class).equalTo("id", reminderId).findFirst();
            return reminder;
        } catch (Exception e) {
            return null;
        }
    }

    public void updateReminder(Calendar calendar, int repeatType, boolean indefinite,
                               int timesToShow, int interval, RealmList<Boolean> daysOfWeek, String reminderId) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Reminder reminder = realm.where(Reminder.class).equalTo("id", reminderId).findFirst();
                if (reminder != null) {
                    reminder.setDateAndTime(calendar.getTimeInMillis());
                    reminder.setRepeatType(repeatType);
                    reminder.setIndefinite(indefinite);
                    reminder.setNumberToShow(timesToShow);
                    reminder.setInterval(interval);
                    reminder.setDaysOfWeek(daysOfWeek);
                }
            }
        });

    }
}

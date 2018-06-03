package com.okason.diary.data;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.models.Reminder;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 5/27/18.
 */

public class ReminderDao {
    private Realm realm;
    private final static String TAG = "ReminderDao";

    public ReminderDao(Realm realm) {
        this.realm = realm;
    }

    public RealmResults<Reminder> getAllReminders() {
        RealmResults<Reminder> reminderResult = realm.where(Reminder.class).findAll();
        return reminderResult;

    }


    public Reminder createNewReminder() {
        int reminderId = (int) ProntoDiaryApplication.reminderPrimaryKey.incrementAndGet();
        realm.beginTransaction();
        Reminder reminder = realm.createObject(Reminder.class, reminderId);
        reminder.setDateCreated(System.currentTimeMillis());
        reminder.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return reminder;
    }

    public Reminder getReminderById(int reminderId) {
        try {
            Reminder reminder = realm.where(Reminder.class).equalTo("id", reminderId).findFirst();
            return reminder;
        } catch (Exception e) {
            return null;
        }
    }

    public void updateReminder(Calendar calendar, int repeatType, boolean indefinite,
                               int timesToShow, int interval, String daysOfWeek, int reminderId) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Reminder reminder = backgroundRealm.where(Reminder.class).equalTo("id", reminderId).findFirst();
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

    public void setDateAndTime(long timeInMillis, int reminderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Reminder reminder = backgroundRealm.where(Reminder.class).equalTo("id", reminderId).findFirst();
                reminder.setDateAndTime(timeInMillis);
                reminder.setDateModified(System.currentTimeMillis());

            }
        });
    }

    public void incrementNumberOfTimesReminderShown(int reminderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Reminder reminder = backgroundRealm.where(Reminder.class).equalTo("id", reminderId).findFirst();
                reminder.setNumberShown(reminder.getNumberShown() + 1);
                reminder.setDateModified(System.currentTimeMillis());

            }
        });

    }

    public boolean isNotificationPresent(int reminderId) {
        return false;
    }
}

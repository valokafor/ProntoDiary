package com.okason.diary.utils.reminder;

import com.okason.diary.R;

import java.util.Calendar;

/**
 * Created by valokafor on 7/19/17.
 */

public enum  Reminder {
    NO("no_reminder",R.string.reminder_label_one_time_event),
    MINUTE("minute", R.string.reminder_label_minute),
    HOURLY("hourly", R.string.reminder_label_hourly),
    DAILY("daily", R.string.reminder_label_daily),
    WEEKLY("weekly", R.string.reminder_label_weekly),
    WEEKDAYS("weekdays", R.string.reminder_label_week_days),
    MONTHLY("monthly", R.string.reminder_label_monthly),
    YEARLY("yearly", R.string.reminder_label_yearly);

    private String id;

    private int idResource;


    Reminder(String id, int idResource) {
        this.id = id;
        this.idResource = idResource;
    }


    public String getId() {
        return id;
    }


    public int getIdResource() {
        return idResource;
    }


    public static Reminder getById(String id) {
        Reminder[] values = Reminder.values();
        for (Reminder value : values) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        return NO;
    }


    public long getInterval(long baseAtMillis) {
        Calendar baseTime = Calendar.getInstance();
        baseTime.setTimeInMillis(baseAtMillis);

        long baseTimeMillis = baseTime.getTimeInMillis();

        switch (this) {
            case MINUTE:
                baseTime.add(Calendar.MINUTE, 1);
                return baseTime.getTimeInMillis() - baseTimeMillis;
            case HOURLY:
                baseTime.add(Calendar.HOUR, 1);
                return baseTime.getTimeInMillis() - baseTimeMillis;
            case DAILY:
                baseTime.add(Calendar.HOUR, 24);
                return baseTime.getTimeInMillis() - baseTimeMillis;
            case WEEKLY:
                baseTime.add(Calendar.HOUR, 24 * 7);
                return baseTime.getTimeInMillis() - baseTimeMillis;
            case MONTHLY:
                baseTime.add(Calendar.MONTH, 1);
                return baseTime.getTimeInMillis() - baseTimeMillis;
            case YEARLY:
                baseTime.add(Calendar.YEAR, 1);
                return baseTime.getTimeInMillis() - baseTimeMillis;
            default:
                return 0;
        }
    }
}

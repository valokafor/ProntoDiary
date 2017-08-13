package com.okason.diary.utils.reminder;

import java.util.Calendar;

/**
 * Created by valokafor on 7/19/17.
 */

public enum  Reminder {
    NO,
    MINUTE,
    HOURLY,
    DAILY,
    WEEKLY,
    WEEKDAYS,
    MONTHLY,
    YEARLY;

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
            case WEEKDAYS:
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

package com.okason.diary.reminder;

import android.content.Context;

import com.okason.diary.R;
import com.okason.diary.utils.Constants;

import io.realm.RealmList;

public class TextFormatUtil {

    public static String formatDaysOfWeekText(Context context, RealmList<Boolean> daysOfWeek) {
        final String[] shortWeekDays = DateAndTimeUtil.getShortWeekDays();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getString(R.string.repeats_on));
        stringBuilder.append(" ");
        for (int i = 0; i < daysOfWeek.size(); i++) {
            if (daysOfWeek.get(i)) {
                stringBuilder.append(shortWeekDays[i]);
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public static String formatAdvancedRepeatText(Context context, int repeatType, int interval) {
        String typeText;
        switch (repeatType) {
            default:
            case Constants.HOURLY:
                typeText = context.getResources().getQuantityString(R.plurals.hour, interval);
                break;
            case Constants.DAILY:
                typeText = context.getResources().getQuantityString(R.plurals.day, interval);
                break;
            case Constants.WEEKLY:
                typeText = context.getResources().getQuantityString(R.plurals.week, interval);
                break;
            case Constants.MONTHLY:
                typeText = context.getResources().getQuantityString(R.plurals.month, interval);
                break;
            case Constants.YEARLY:
                typeText = context.getResources().getQuantityString(R.plurals.year, interval);
                break;
        }
        return context.getString(R.string.repeats_every, interval, typeText);
    }
}
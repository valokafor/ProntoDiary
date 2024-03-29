/*
 * Copyright (C) 2016 Federico Iosue (federico.iosue@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundatibehaon, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.okason.diary.utils.date;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.utils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Helper per la generazione di date nel formato specificato nelle costanti
 *
 * @author 17000026
 */
public class DateHelper {

    public static String getSortableDate() {
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SORTABLE);
        Date now = Calendar.getInstance().getTime();
        result = sdf.format(now);
        return result;
    }


    /**
     * Build a formatted date string starting from values obtained by a DatePicker
     *
     * @param year
     * @param month
     * @param day
     * @param format
     * @return
     */
    public static String onDateSet(int year, int month, int day, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return sdf.format(cal.getTime());
    }


    /**
     * Build a formatted time string starting from values obtained by a TimePicker
     *
     * @param hour
     * @param minute
     * @param format
     * @return
     */
    public static String onTimeSet(int hour, int minute, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return sdf.format(cal.getTime());
    }


    public static String getLocalizedDateTime(Context mContext,
                                              String dateString, String format) {
        String res = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SORTABLE_OLD);
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e1) {
                Log.e(Constants.TAG, "String is not formattable into date");
            }
        }

        if (date != null) {
            String dateFormatted = DateUtils.formatDateTime(mContext, date.getTime(), DateUtils.FORMAT_ABBREV_MONTH);
            String timeFormatted = DateUtils.formatDateTime(mContext, date.getTime(), DateUtils.FORMAT_SHOW_TIME);
            res = dateFormatted + " " + timeFormatted;
        }

        return res;
    }


    /**
     * @param mContext
     * @param date
     * @return
     */
    public static String getDateTimeShort(Context mContext, Long date) {
        if (date == null)
            return "";

        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);

        int flags = DateUtils.FORMAT_ABBREV_MONTH;
        if (c.get(Calendar.YEAR) != now.get(Calendar.YEAR))
            flags = flags | DateUtils.FORMAT_SHOW_YEAR;
        return DateUtils.formatDateTime(mContext, date, flags)
                + " " + DateUtils.formatDateTime(mContext, date, DateUtils.FORMAT_SHOW_TIME);
    }


    /**
     * @param mContext
     * @return
     */
    public static String getTimeShort(Context mContext, Long time) {
        if (time == null)
            return "";
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
       // return DateUtils.formatDateTime(mContext, time, DateUtils.FORMAT_SHOW_TIME);

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        return dateFormat.format(c.getTime());
    }


    /**
     * @param mContext
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static String getTimeShort(Context mContext, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        return DateUtils.formatDateTime(mContext, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }


    /**
     * Formats a short time period (minutes)
     *
     * @param time
     * @return
     */
    public static String formatShortTime(Context mContext, long time) {
        String m = String.valueOf(time / 1000 / 60);
        String s = String.format("%02d", (time / 1000) % 60);
        return m + ":" + s;
    }






    public static String getNoteReminderText(long reminder) {
        return ProntoDiaryApplication.getAppContext().getString(R.string.alarm_set_on) + " " + getDateTimeShort(ProntoDiaryApplication
				.getAppContext(), reminder);
    }




//	public static String getFormattedDate(Long timestamp, boolean prettified) {
//		if(prettified) {
//			return it.feio.android.omninotes.utils.date.DateUtils.prettyTime(timestamp);
//		} else {
//			return DateHelper.getDateTimeShort(ProntoDiaryApplication.getAppContext(), timestamp);
//		}
//	}

}

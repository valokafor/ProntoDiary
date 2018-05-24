package com.okason.diary.utils.date;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.models.realmentities.SubTask;
import com.okason.diary.models.realmentities.Task;
import com.okason.diary.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Valentine on 2/9/2016.
 */
public class TimeUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }


    public static String getReadableModifiedDateWithTime(long date){

        String displayDate = new SimpleDateFormat("MMM dd, yyyy - h:mm a").format(new Date(date));
        return displayDate;
    }


    public static String getDueDate(long date){

        String displayDate = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date(date));
        return displayDate;
    }

    public static String getDatetimeSuffix(long date){
        String timeStamp = new SimpleDateFormat("yyyy_MMM_dd_HH_mm").format(new Date(date));
        return timeStamp;
    }

    public static String getReadableDateWithoutTime(long date){

        String displayDate = new SimpleDateFormat("MMM dd, yyyy").format(new Date(date));
        return displayDate;
    }

    public static int currentTimeMillis(long timeInMillis) {
        return (int) (timeInMillis % Integer.MAX_VALUE);
    }

    public static long getInterval(long baseAtMillis, String interval) {
        Calendar baseTime = Calendar.getInstance();
        baseTime.setTimeInMillis(baseAtMillis);

        long baseTimeMillis = baseTime.getTimeInMillis();

        if (interval.equals(Constants.REMINDER_NO_REMINDER)) {
            return 0;
        } else if (interval.equals(Constants.REMINDER_MINUTE)){
            baseTime.add(Calendar.MINUTE, 1);
            return baseTime.getTimeInMillis() - baseTimeMillis;

        }else if (interval.equals(Constants.REMINDER_HOURLY)){
            baseTime.add(Calendar.HOUR, 1);
            return baseTime.getTimeInMillis() - baseTimeMillis;

        } else if (interval.equals(Constants.REMINDER_DAILY)){
            baseTime.add(Calendar.HOUR, 24);
            return baseTime.getTimeInMillis() - baseTimeMillis;

        } else if (interval.equals(Constants.REMINDER_WEEK_DAYS)){
            baseTime.add(Calendar.HOUR, 24);
            return baseTime.getTimeInMillis() - baseTimeMillis;

        } else if (interval.equals(Constants.REMINDER_WEEKLY)){
            baseTime.add(Calendar.HOUR, 24 * 7);
            return baseTime.getTimeInMillis() - baseTimeMillis;
        } else if (interval.equals(Constants.REMINDER_MONTHLY)){
            baseTime.add(Calendar.MONTH, 1);
            return baseTime.getTimeInMillis() - baseTimeMillis;
        } else if (interval.equals(Constants.REMINDER_YEARLY)){
            baseTime.add(Calendar.YEAR, 1);
            return baseTime.getTimeInMillis() - baseTimeMillis;
        } else {
            return 0;
        }


    }

    public static String getSubTaskStatus(Task task) {
        StringBuilder stringBuilder = new StringBuilder(40);

        int numberOfTasks = 0;
        String tasksLabel = ProntoDiaryApplication.getAppContext().getString(R.string.label_sub_task);
        numberOfTasks = task.getSubTask().size();

        if (numberOfTasks > 1){
            tasksLabel = tasksLabel + "s";
        }
        tasksLabel = numberOfTasks + " "  + tasksLabel;

        stringBuilder.append(tasksLabel);

        int completedTasks = 0;
        int pendingTasks = 0;

        if (task.getSubTask().size() > 0){
            for (SubTask subTask: task.getSubTask()){
                if (subTask.isChecked()){
                    completedTasks++;
                }else {
                    pendingTasks++;
                }
            }

            stringBuilder.append(" (");
            stringBuilder.append(completedTasks);
            stringBuilder.append(" ");
            stringBuilder.append(ProntoDiaryApplication.getAppContext().getString(R.string.label_done));
            stringBuilder.append(", ");
            stringBuilder.append(pendingTasks);
            stringBuilder.append(" " + ProntoDiaryApplication.getAppContext().getString(R.string.label_pending));
            stringBuilder.append(")");

        }

        return stringBuilder.toString();
    }







}

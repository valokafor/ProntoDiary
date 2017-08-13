package com.okason.diary.utils.reminder;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePreference extends DialogPreference {

    private final long mDefaultValue;
    private TimePicker mTimePicker;


    public TimePreference(final Context context, final AttributeSet attrs) {
        super(context, attrs, android.R.attr.dialogPreferenceStyle);

        // Set default time: 08:00
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        mDefaultValue = calendar.getTimeInMillis();
    }


    private void setTime(final long time) {
        persistLong(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }


    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(getContext()));
        return mTimePicker;
    }


    private Calendar getPersistedTime() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getPersistedLong(mDefaultValue));
        return calendar;
    }


    @Override
    protected void onBindDialogView(final View v) {
        super.onBindDialogView(v);

        final Calendar calendar = getPersistedTime();
        mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }


    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
            calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());

            if (!callChangeListener(calendar.getTimeInMillis())) {
                return;
            }
            setTime(calendar.getTimeInMillis());
        }
    }


    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        return a.getInteger(index, 0);
    }


    @Override
    protected void onSetInitialValue(final boolean restorePersistedValue, final Object defaultValue) {
        long time;
        if (defaultValue == null) {
            time = restorePersistedValue ? getPersistedLong(mDefaultValue) : mDefaultValue;
        } else if (defaultValue instanceof Long) {
            time = restorePersistedValue ? getPersistedLong((Long) defaultValue) : (Long) defaultValue;
        } else if (defaultValue instanceof Calendar) {
            time = restorePersistedValue ? getPersistedLong(((Calendar) defaultValue).getTimeInMillis()) : ((Calendar) defaultValue).getTimeInMillis();
        } else {
            time = restorePersistedValue ? getPersistedLong(mDefaultValue) : mDefaultValue;
        }

        setTime(time);
    }
}
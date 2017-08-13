package com.okason.diary.ui.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.okason.diary.R;
import com.okason.diary.utils.reminder.TimePreference;

import java.text.DateFormat;
import java.util.Date;

public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else if (preference instanceof RingtonePreference) {
                String ringtoneSummary = preference.getContext().getString(R.string.pref_no_sound);
                if (!TextUtils.isEmpty(stringValue)) {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    ringtoneSummary = ringtone == null
                            ? null
                            : ringtone.getTitle(preference.getContext());
                }
                preference.setSummary(ringtoneSummary);
            } else if (preference instanceof CheckBoxPreference) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                stringValue = (boolean) value
                        ? preference.getContext().getString(R.string.pref_enabled)
                        : preference.getContext().getString(R.string.pref_disabled);
                checkBoxPreference.setSummary(stringValue);
            } else if (preference instanceof TimePreference) {
                final DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(preference.getContext());
                final Date date = new Date((long) value);
                preference.setSummary(dateFormat.format(date.getTime()));
            } else {
                preference.setSummary(stringValue);
            }

            return true;
        }
    };


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        Object newValue;
        if (preference instanceof CheckBoxPreference) {
            newValue = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getBoolean(preference.getKey(), true);
        } else if (preference instanceof TimePreference) {
            newValue = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getLong(preference.getKey(), 0);
        } else {
            newValue = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), "");
        }

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vibration_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sound_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_tasknotification_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_daysummary_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_time_key)));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

}

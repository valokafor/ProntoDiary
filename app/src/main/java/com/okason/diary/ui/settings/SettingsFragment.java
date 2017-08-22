package com.okason.diary.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;

import com.okason.diary.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

//        PreferenceScreen screen = this.getPreferenceScreen(); // "null". See onViewCreated.
//
//        // Create the Preferences Manually - so that the key can be set programatically.
//        PreferenceCategory category = new PreferenceCategory(screen.getContext());
//        category.setTitle("Account Info");
//        screen.addPreference(category);
//
//        EditTextPreference editTextPreference = new EditTextPreference(screen.getContext());
//        editTextPreference.setText("Val Okafor");
//        editTextPreference.setTitle("Val Okafor");
//        editTextPreference.setSummary("okasonhitech@gmail.com");
//        category.addPreference(editTextPreference);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


}

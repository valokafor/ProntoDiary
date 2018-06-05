package com.okason.diary.ui.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.okason.diary.R;
import com.okason.diary.models.inactive.SampleData;

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

        Preference addSampleDataButton = findPreference("add_sample_data");
        addSampleDataButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new SampleData(getActivity()).getSampleNotesRealm();
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Add Sample Data");
                FirebaseAnalytics.getInstance(getActivity()).logEvent("add_sample_data", bundle);
                getActivity().onBackPressed();
                return true;
            }
        });

        Preference removeSampleDataButton = findPreference("remove_sample_data");
        removeSampleDataButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new SampleData(getActivity()).removeSampleData();
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Remove Sample Data");
                FirebaseAnalytics.getInstance(getActivity()).logEvent("remove_sample_data", bundle);
                getActivity().onBackPressed();
                return true;
            }
        });

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

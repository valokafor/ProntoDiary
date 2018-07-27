package com.okason.diary.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.models.inactive.SampleData;
import com.okason.diary.ui.auth.PinEntryActivity;
import com.okason.diary.utils.SettingsHelper;

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

                Intent intent = new Intent(getActivity(), NoteListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }
        });


        Preference addPasscode = findPreference("enable_pass_code");
        addPasscode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), PinEntryActivity.class));
                return true;
            }
        });

        Preference removePasscode = findPreference("remove_pass_code");
        removePasscode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SettingsHelper.getHelper(getActivity()).removePinCode();
                Toast.makeText(getActivity(), getString(R.string.pin_code_removed), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), NoteListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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

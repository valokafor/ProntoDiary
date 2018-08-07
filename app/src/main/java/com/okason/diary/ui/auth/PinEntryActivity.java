package com.okason.diary.ui.auth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.models.inactive.ProntoJournalUser;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;
import com.okason.diary.utils.date.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PinEntryActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_save) Button saveButton;
    @BindView(R.id.btn_cancel) Button cancelButton;
    @BindView(R.id.input_pin) EditText pinInputEditText;
    @BindView(R.id.input_email) EditText emailInputEditText;
    @BindView(R.id.repeat_email) EditText repeatEmailEditText;
    @BindView(R.id.rootView) View rootView;
    private Activity activity;
    private SettingsHelper settingsHelper;

    private DocumentReference profileCloudReference;
    private FirebaseUser firebaseUser;
    private String userId;
    private FirebaseFirestore database;
    private final static String TAG = "PinEntryActivity";
    private int pinCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);
        ButterKnife.bind(this);
        activity = this;
        settingsHelper = SettingsHelper.getHelper(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.input_email)
    public void onEmailfieldTouched(View view){
        Intent intent = AccountManager.newChooseAccountIntent(
                null,
                null,
                new String[] {"com.google"},
                false,
                null,
                null,
                null,
                null);

        startActivityForResult(intent, 12);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12 && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            emailInputEditText.setText(accountName);
            repeatEmailEditText.setText(accountName);
            Log.d(TAG, accountName);
        }
    }


    @OnClick(R.id.btn_save)
    public void onSaveButtonClicked(View view){

        String pinInput = pinInputEditText.getText().toString().trim();
        if (TextUtils.isEmpty(pinInput)) {
            makeToast(getString(R.string.hint_enter_passcode));
            pinInputEditText.setError(getString(R.string.error_field_required));
            return;
        }
        String firstCharacter = pinInput.substring(0, 1);
        if (firstCharacter.equals("0")){
            makeToast("Pincode should not start with zero");
            return;
        }

        try {
            pinCode = Integer.parseInt(pinInput);
        } catch (NumberFormatException e) {
            Crashlytics.log(Log.DEBUG, TAG, e.getLocalizedMessage());
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        if (pinCode == 0){
            return;
        }


        settingsHelper.saveUserPinCode(pinCode);

        Bundle bundle = new Bundle();
        FirebaseAnalytics.getInstance(activity).logEvent("add_pin_code", bundle);

        String email = emailInputEditText.getText().toString();
        String repeatEmail = repeatEmailEditText.getText().toString();
        if (!email.equals(repeatEmail)){
            makeToast("Repeat email is different from email");
        } else {
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.progress_dialo)
                    .content(R.string.please_wait)
                    .icon(ContextCompat.getDrawable(activity, R.mipmap.ic_launcher))
                    .progress(true, 0)
                    .show();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                userId = firebaseUser.getUid();
                database = FirebaseFirestore.getInstance();
                String fcmToken = settingsHelper.getMessagingToken();
                if (!TextUtils.isEmpty(fcmToken)) {
                    profileCloudReference = database.collection(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE).document(fcmToken);

                    profileCloudReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                ProntoJournalUser user;
                                if (snapshot.exists()) {
                                    user = snapshot.toObject(ProntoJournalUser.class);
                                } else {
                                    user = new ProntoJournalUser();
                                }

                                user.setFcmTokens(fcmToken);
                                user.setDateModified(System.currentTimeMillis());
                                user.setEmailAddress(email);
                                user.setPinCode(pinCode);
                                user.setDateCreated(TimeUtils.getReadableDateWithoutTime(System.currentTimeMillis()));
                                profileCloudReference.set(user);
                            }
                            dialog.dismiss();
                        }
                    });
                }

            }

        }
        navigateToListOfJournals();
    }


    private void navigateToListOfJournals() {
        Intent intent = new Intent(PinEntryActivity.this, NoteListActivity.class);
        startActivity(intent);
    }

    private void makeToast(String message) {
        try {
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary));
            TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

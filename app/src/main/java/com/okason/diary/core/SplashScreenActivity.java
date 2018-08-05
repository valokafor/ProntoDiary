package com.okason.diary.core;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.models.inactive.ProntoJournalUser;
import com.okason.diary.ui.appintro.StartupActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;
import com.okason.diary.utils.date.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    public static final String TAG = "SplashScreenActivity";
    private Activity activity;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setupMessagingService();
        firebaseLogin();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = sharedPreferences.getBoolean("first_run", true);
        if (firstRun) {
            startActivity(new Intent(activity, StartupActivity.class));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_run", false).commit();
        } else if (SettingsHelper.getHelper(this).isPinCodeEnabled()) {
            promptForPincode();

        } else {
            navigateToListOfJournals();
        }

    }

    private void promptForPincode() {
        dialog = new MaterialDialog.Builder(this)
                .title(R.string.enter_pin_code)
                .content(R.string.pin_code_required)
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                .negativeText("Forgot Pin?")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        promptForEmail();
                    }
                })
                .input(R.string.hint_pincode, 0, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        verifyPincode(input);
                    }


                }).show();
    }

    //Todo - Implement pincode recovery
    private void promptForEmail() {

        MaterialDialog forgorPinDialog = new MaterialDialog.Builder(this)
                .title(R.string.forgot_pincode)
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .input(R.string.hint_enter_email, 0, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        handleForgotPincode(input.toString());
                    }


                }).show();

    }

    private void handleForgotPincode(String email) {
        String fcmToken = SettingsHelper.getHelper(activity).getMessagingToken();
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !TextUtils.isEmpty(fcmToken)) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference profileCloudReference = database.collection(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE).document(fcmToken);

            profileCloudReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        ProntoJournalUser user;
                        if (snapshot.exists()) {
                            user = snapshot.toObject(ProntoJournalUser.class);
                            if (user.getEmailAddress() != null && user.getEmailAddress().equals(email)) {
                                int pincode = user.getPinCode();

                                Map<String, Object> pinCodeRequest = new HashMap<>();
                                pinCodeRequest.put("email", email);
                                pinCodeRequest.put("fcmToken", fcmToken);
                                pinCodeRequest.put("pinCode", pincode);
                                pinCodeRequest.put("date", TimeUtils.getReadableModifiedDateWithTime(System.currentTimeMillis()));

                                database.collection(Constants.FORGOT_PINCODE_REF)
                                        .add(pinCodeRequest)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(activity, "Check your email for pincode", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity, "Error fetching pincode: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        } else {
                            Toast.makeText(activity, R.string.missing_record, Toast.LENGTH_LONG).show();
                        }
                        ;

                    }
                    dialog.dismiss();
                }
            });


        } else {
            Toast.makeText(activity, R.string.missing_record, Toast.LENGTH_LONG).show();
        }


    }

    private void firebaseLogin() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Constants.PRONTO_USER_GENERIC, Constants.EMAIL_PASSWORD);
        }
    }

    private void verifyPincode(CharSequence input) {
        int savedPinCode = SettingsHelper.getHelper(SplashScreenActivity.this).getSavedPinCode();
        int pinCode = Integer.parseInt(input.toString());
        if (savedPinCode == pinCode){
            navigateToListOfJournals();
        } else {
            Toast.makeText(activity, "Wrong pincode", Toast.LENGTH_SHORT).show();
            promptForPincode();
        }
    }

    /**
     * Start the service to confirm FCM is set up properly.
     */
    private void setupMessagingService() {
        Log.d(TAG, "Set up messaging services.");
        String token = SettingsHelper.getHelper(this).getMessagingToken();
        if (token == null) {
            Log.d(TAG, "No FCM token defined. Requesting new token.");
            FirebaseInstanceId.getInstance().getToken();

        }
    }

    private void navigateToListOfJournals() {
        Intent intent = new Intent(SplashScreenActivity.this, NoteListActivity.class);
        startActivity(intent);
    }




}

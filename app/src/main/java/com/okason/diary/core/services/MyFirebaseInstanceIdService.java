package com.okason.diary.core.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.okason.diary.core.events.OnFirebaseTokenRefreshed;
import com.okason.diary.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private DocumentReference profileCloudReference;
    private FirebaseUser firebaseUser;
    private String userId;
    private FirebaseFirestore database;

        public static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();


        @Override
        public void onTokenRefresh() {
            super.onTokenRefresh();
            final String token = FirebaseInstanceId.getInstance().getToken();

            EventBus.getDefault().post(new OnFirebaseTokenRefreshed(token));
            SettingsHelper.getHelper(getApplicationContext()).setMessagingToken(token);
            Log.d(TAG, "FCM token retrieved: " + token);
        }


}

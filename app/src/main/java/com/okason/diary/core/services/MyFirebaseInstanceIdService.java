package com.okason.diary.core.services;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;
    private ProntoDiaryUser prontoDiaryUser;

        public static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();


        @Override
        public void onTokenRefresh() {
            super.onTokenRefresh();
            final String token = FirebaseInstanceId.getInstance().getToken();

            //Update Token in Database after token refresh

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);

            final String oldToken = SettingsHelper.getHelper(getApplicationContext()).getMessagingToken();
            if (!TextUtils.isEmpty(oldToken)){
                mProntoDiaryUserRef.orderByChild("fcmToken").equalTo(oldToken).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            prontoDiaryUser = snapshot.getValue(ProntoDiaryUser.class);
                            prontoDiaryUser.setFcmToken(token);
                            mProntoDiaryUserRef.child(prontoDiaryUser.getId()).setValue(prontoDiaryUser);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            Log.d(TAG, "FCM token retrieved: " + token);

            //Save to settings on main thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SettingsHelper.getHelper(getApplicationContext()).setMessagingToken(token);
                }
            });

        }
}

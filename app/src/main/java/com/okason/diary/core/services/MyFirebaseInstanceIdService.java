package com.okason.diary.core.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.okason.diary.models.inactive.ProntoJournalUser;
import com.okason.diary.utils.SettingsHelper;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;
    private ProntoJournalUser prontoJournalUser;

        public static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();


        @Override
        public void onTokenRefresh() {
            super.onTokenRefresh();
            final String token = FirebaseInstanceId.getInstance().getToken();


            //Update Token in Database after token refresh

//            mDatabase = FirebaseDatabase.getInstance().getReference();
//            mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);
//
//            final String oldToken = SettingsHelper.getHelper(getApplicationContext()).getMessagingToken();
//            if (!TextUtils.isEmpty(oldToken)){
//                mProntoDiaryUserRef.orderByChild("fcmToken").equalTo(oldToken).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
//                            prontoJournalUser = snapshot.getValue(ProntoJournalUser.class);
//                            prontoJournalUser.setFcmToken(token);
//                            mProntoDiaryUserRef.child(prontoJournalUser.getId()).setValue(prontoJournalUser);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//            }

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

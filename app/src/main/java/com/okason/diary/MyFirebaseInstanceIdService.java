package com.okason.diary;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.okason.diary.core.events.OnFirebaseTokenRefreshed;
import com.okason.diary.models.inactive.ProntoJournalUser;
import com.okason.diary.utils.Constants;

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

            Log.d(TAG, "FCM token retrieved: " + token);

//            //Save to settings on main thread
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    SettingsHelper.getHelper(getApplicationContext()).setMessagingToken(token);
//                }
//            });


            //Update Token in Database after token refresh

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null && !firebaseUser.getEmail().equals(Constants.EMAIL_LOGIN)) {
                userId = firebaseUser.getUid();
                database = FirebaseFirestore.getInstance();
                profileCloudReference = database.collection(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE).document(userId);

                profileCloudReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()){
                                ProntoJournalUser user = snapshot.toObject(ProntoJournalUser.class);
                                if (user != null){
                                    user.getFcmTokens().add(token);
                                    user.setDateModified(System.currentTimeMillis());
                                    profileCloudReference.set(user);
                                }
                            }
                        }
                    }
                });
            }




        }
}

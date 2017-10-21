package com.okason.diary.utils.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.firestore.DocumentSnapshot;
import com.okason.diary.NoteListActivity;
import com.okason.diary.models.Task;
import com.okason.diary.ui.addnote.DataAccessManager;
import com.okason.diary.utils.Constants;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private static final Class COMPONENT_CLASS = NoteListActivity.class;
    private DataAccessManager dataAccessManager;


    @Override
    public void onReceive(final Context context, final Intent intent) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            dataAccessManager = new DataAccessManager(firebaseUser.getUid());
            final String taskId = intent.getStringExtra(Constants.TASK_ID);
            dataAccessManager.getTaskPath().document(taskId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Task currentTask = documentSnapshot.toObject(Task.class);
                            if (currentTask != null) {
                                handleReminder(currentTask);

                            } else {
                                FirebaseCrash.report(new Exception("Saved Task is Null after Alarm Receiver Fired"));
                            }

                        }

                        private void handleReminder(Task currentTask) {

                            Notification notification = intent.getParcelableExtra(Constants.ALARM_NOTIFICATION);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) currentTask.getDateCreated(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            // Delete alarm if date of task expired
                            if (currentTask != null && Calendar.getInstance().after(currentTask.getRepeatEndDate())) {
                                MyAlarmManager.deleteAlarm(context, pendingIntent);
                                return;
                            }

                            // this condition is for weekdays
                            if (currentTask.getRepeatFrequency().equals(Reminder.WEEKDAYS)) {
                                int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                                if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                                    return;
                                }
                            }

                            intent.setClass(context, COMPONENT_CLASS);
                            notification.contentIntent =
                                    PendingIntent.getActivity(context, Integer.parseInt(taskId), intent, FLAG_UPDATE_CURRENT);

                            NotificationManager notificationManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(Integer.parseInt(taskId), notification);

                        }
                    });


        } else {
            FirebaseCrash.report(new Exception("Firebase User is Null after Alarm Receiver Fired"));
        }


    }
}

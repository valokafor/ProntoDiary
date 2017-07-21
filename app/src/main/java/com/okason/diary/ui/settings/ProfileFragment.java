package com.okason.diary.ui.settings;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.data.TaskRealmRepository;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.ui.auth.AuthUiActivity;
import com.okason.diary.ui.auth.SignInActivity;
import com.okason.diary.ui.auth.UserManager;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private View mRootView;
    @BindView(R.id.toolbar_button_login)
    Button loginButton;

    @BindView(R.id.toolbar_button_logout) Button logoutButton;

    @BindView(R.id.toolbar_profile_image)
    ImageView profileImage;

    @BindView(R.id.toolbar_profile_task_count)
    TextView taskCountTextView;

    @BindView(R.id.toolbar_profile_note_count)
    TextView journalCountTextView;

    @BindView(R.id.toolbar_profile_name)
    TextView userName;



    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private ProntoDiaryUser prontoDiaryUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, mRootView);
        mAuth = FirebaseAuth.getInstance();


        mFirebaseUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProntoDiaryUserRef = mDatabase.child(Constants.PRONTO_DIARY_USER_CLOUD_REFERENCE);

        if (mFirebaseUser != null) {
            updateProfile();
        }

        if (mFirebaseUser != null){
            mProntoDiaryUserRef.orderByChild("firebaseUid").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                        prontoDiaryUser = snapshot.getValue(ProntoDiaryUser.class);
                        if (prontoDiaryUser == null){
                            loginButton.setVisibility(View.VISIBLE);
                            logoutButton.setVisibility(View.GONE);
                        }else {
                            loginButton.setVisibility(View.GONE);
                            logoutButton.setVisibility(View.VISIBLE);
                            populateProfile(prontoDiaryUser);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        return mRootView;
    }

    private void updateProfile() {
        final String token = SettingsHelper.getHelper(getActivity()).getMessagingToken();
        if (!TextUtils.isEmpty(token)){
            mProntoDiaryUserRef.orderByChild("fcmToken").equalTo(token).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @MainThread
    private void populateProfile(ProntoDiaryUser prontoDiaryUser) {
        if (prontoDiaryUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(Uri.parse(prontoDiaryUser.getPhotoUrl()))
                    .fitCenter()
                    .into(profileImage);
        }

        userName.setText(
                TextUtils.isEmpty(prontoDiaryUser.getDisplayName()) ? "No display name" : prontoDiaryUser.getDisplayName());

        int numNote = 0;
        try {
            numNote = new NoteRealmRepository().getAllNotes().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String notes = numNote > 1 ? getString(R.string.label_journals) : getString(R.string.label_journal);
        journalCountTextView.setText(numNote + " " + notes);


        String taskLabel = ProntoDiaryApplication.getAppContext().getString(R.string.zero_task);
        int taskCount = 0;
        try {
            taskCount = new TaskRealmRepository().getAllTaskAndSubTaskCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (taskCount > 0) {
            taskLabel = taskCount > 1 ? taskCount
                    + " " + getString(R.string.label_tasks) : taskCount
                    + " " + getString(R.string.label_task) ;
        }

        taskCountTextView.setText(taskLabel);


    }

    @OnClick(R.id.toolbar_button_login)
    public void onLoginButtonClicked(View view){
        startActivity(new Intent(getActivity(), AuthUiActivity.class));
    }

    @OnClick(R.id.toolbar_button_logout)
    public void onLogoutButtonClicked(View view){

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext(), R.style.dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(getString(R.string.please_attention));
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage("You logged in with " + prontoDiaryUser.getLoginProvider() + " Please use the same login method to sign again next time to access your data");
        alertDialog.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UserManager.logoutActiveUser();
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    showSnackbar(R.string.sign_out_failed);
                                }
                            }
                        });
            }
        });
        alertDialog.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();



    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }


}

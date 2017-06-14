package com.okason.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.ShowFragmentEvent;
import com.okason.diary.ui.auth.AuthUiActivity;
import com.okason.diary.ui.folder.FolderListFragment;
import com.okason.diary.ui.notes.NoteListFragment;
import com.okason.diary.ui.settings.SettingsActivity;
import com.okason.diary.ui.settings.SyncFragment;
import com.okason.diary.ui.todolist.TodoListFragment;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteListActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private boolean unregisteredUser = false;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private ConnectivityManager connectivityManager;


    public static final String ACTION_IGNORE_CURRENT_USER = "action.ignoreCurrentUser";
    public static final String TAG = "NoteListActivity";

    private String currentFragmentTag = "";
    private String currentFragmentTitle = "";

    @BindView(R.id.root)
    View mRootView;

    @BindView(R.id.image_button_notes)
    ImageButton noteButton;

    @BindView(R.id.image_button_todo_list)
    ImageButton todoListButton;

    @BindView(R.id.image_button_folder)
    ImageButton folderButton;

    @BindView(R.id.image_button_sync)
    ImageButton syncButton;

    @BindView(R.id.image_button_settings)
    ImageButton settingsButton;

    @BindView(R.id.image_button_login)
    ImageButton loginButton;

    @BindView(R.id.notes_text_view)
    TextView noteTextView;

    @BindView(R.id.todo_list_text_view)
    TextView todoListTextView;

    @BindView(R.id.folder_text_view)
    TextView  folderTextView;

    @BindView(R.id.sync_text_view)
    TextView syncTextView;

    @BindView(R.id.settings_text_view)
    TextView settingsTextView;

    @BindView(R.id.login_text_view)
    TextView loginTextView;


    @BindView(R.id.linear_layout_settings)
    LinearLayout settingsLayout;

    @BindView(R.id.linear_layout_sync)
    LinearLayout syncLayout;

    @BindView(R.id.linear_layout_login)
    LinearLayout loginLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mActivity = this;

        checkNetworkConnected();


    }

    private void checkLoginStatus() {

        //Check to see if the user has registered before
        //if yes, check to see if the user is not logged in, show login
        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        unregisteredUser = preferences.getBoolean(Constants.ANONYMOUS_USER, true);
        if (unregisteredUser){
            loginAnonymously();
            settingsLayout.setVisibility(View.GONE);
            syncLayout.setVisibility(View.VISIBLE);
        }else {
            loginRegisteredUser();
            settingsLayout.setVisibility(View.VISIBLE);
            syncLayout.setVisibility(View.GONE);
        }
    }


    private void loginAnonymously() {
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }

        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null){
                        String tempUserId = user.getUid();
                        preferences.edit().putString(Constants.ANONYMOUS_ACCOUNT_USER_ID, tempUserId).commit();
                        ProntoDiaryApplication.setCloudSyncEnabled(false);
                        updateUI();
                    }

                }else {
                    makeToast(getString(R.string.anonymous_account_failed_error));
                }
            }
        });
    }

    private void loginRegisteredUser() {
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser == null){
            //Go to sign in Activity
            startActivity(new Intent(mActivity, AuthUiActivity.class));
        }else {
            ProntoDiaryApplication.setCloudSyncEnabled(true);
            updateUI();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
    }



    private void updateUI() {
//        if (user == null){
//            return;
//        }
        noteButton.setImageResource(R.drawable.ic_action_book_red_light);
        noteTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
        openFragment(new NoteListFragment(), getString(R.string.label_journals), Constants.NOTE_LIST_FRAGMENT_TAG);

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBottomNavigationIcons();
                noteButton.setImageResource(R.drawable.ic_action_book_red_light);
                noteTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
                openFragment(new NoteListFragment(), getString(R.string.label_journals), Constants.NOTE_LIST_FRAGMENT_TAG);
            }
        });

        todoListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBottomNavigationIcons();
                todoListButton.setImageResource(R.drawable.ic_action_tick_light_red);
                todoListTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
                openFragment(new TodoListFragment(), getString(R.string.label_todo_list), Constants.TODO_LIST_FRAGMENT_TAG);

            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBottomNavigationIcons();
                syncButton.setImageResource(R.drawable.ic_action_reload_light_red);
                syncTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
                openFragment(new SyncFragment(), getString(R.string.title_cloud_sync), Constants.SYNC_FRAGMENT_TAG);

            }
        });


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBottomNavigationIcons();
                settingsButton.setImageResource(R.drawable.ic_action_settings_light_red);
                settingsTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
                startActivity(new Intent(mActivity, SettingsActivity.class));
            }
        });

        folderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBottomNavigationIcons();
                folderButton.setImageResource(R.drawable.ic_action_folder_tabs_light_red);
                folderTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
                openFragment(new FolderListFragment(), getString(R.string.label_folders), Constants.FOLDER_FRAGMENT_TAG);


            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowFragmentEvent(ShowFragmentEvent event){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(event.getTag());
        if (fragment != null){
            openFragment(fragment, event.getTitle(), event.getTag());
        }else {
            openFragment(new NoteListFragment(), getString(R.string.title_activity_note_list), Constants.NOTE_LIST_FRAGMENT_TAG);
        }
    }

    private void resetBottomNavigationIcons() {
        noteButton.setImageResource(R.drawable.ic_action_book_holo_light);
        noteTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));

        folderButton.setImageResource(R.drawable.ic_action_folder_tabs_holo_light);
        folderTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));

        todoListButton.setImageResource(R.drawable.ic_action_tick_holo_light);
        todoListTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));

        syncButton.setImageResource(R.drawable.ic_action_reload_holo_light);
        syncTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));

        loginButton.setImageResource(R.drawable.ic_action_lock_open_holo_light);
        loginTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));

        settingsButton.setImageResource(R.drawable.ic_action_settings_holo_light);
        settingsTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));
    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void openFragment(Fragment fragment, String screenTitle, String tag){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment, tag)
                .addToBackStack(screenTitle)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }


    /**
     * Check network conenctivity and direct user to settings if no network.
     */
    private void checkNetworkConnected() {
        if (!isActiveNetworkConnected()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.noNetwork)
                    .setPositiveButton(R.string.fixNetworking, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNeutralButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            // next check google play
            checkPlayServices();
        }
    }


    private boolean isActiveNetworkConnected() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * Check Google Play services and provide a fix-it dialog if not up to date.
     */
    private void checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int result = api.isGooglePlayServicesAvailable(this);
        if (result == 0) {
            // next step start service
            checkLoginStatus();
            return;
        }

        if (api.isUserResolvableError(result)) {
            // show the fix-it dialog
            Dialog dialog = api.getErrorDialog(this, result, 0);
            dialog.show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.playServicesError)
                    .setNeutralButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }









}

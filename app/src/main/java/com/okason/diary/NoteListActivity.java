package com.okason.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.AddDefaultDataEvent;
import com.okason.diary.core.events.DisplayFragmentEvent;
import com.okason.diary.core.events.RealmDatabaseRegistrationCompletedEvent;
import com.okason.diary.core.events.ShowFragmentEvent;
import com.okason.diary.core.services.AddSampleDataIntentService;
import com.okason.diary.ui.auth.AuthUiActivity;
import com.okason.diary.ui.auth.UserManager;
import com.okason.diary.ui.folder.FolderListFragment;
import com.okason.diary.ui.notes.NoteListFragment;
import com.okason.diary.ui.settings.AccountFragment;
import com.okason.diary.ui.todolist.TaskListFragment;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.SyncUser;

public class NoteListActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean unregisteredUser = false;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;

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

    @BindView(R.id.settings_text_view)
    TextView settingsTextView;

    @BindView(R.id.login_text_view)
    TextView loginTextView;


    @BindView(R.id.linear_layout_settings)
    LinearLayout settingsLayout;

    @BindView(R.id.linear_layout_login)
    LinearLayout loginLayout;

    @BindView(R.id.linear_layout_todo_list)
    LinearLayout todoListLayout;

    @BindView(R.id.linear_layout_notes)
    LinearLayout notesLayout;

    @BindView(R.id.linear_layout_folder)
    LinearLayout folderLayout;

    @BindView(R.id.toolbar_title) TextView toolbarTitle;

    private MaterialDialog progressDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mActivity = this;

        if (savedInstanceState == null){
            checkNetworkConnected();
        }
    }



    @Override
    public void onBackPressed() {
        finish();
    }

//    private void checkLoginStatus() {
//        //Check Firebase User status,
//
//        final SyncUser user = SyncUser.currentUser();
//        if (user == null) {
//            //Check to see if the user has registered before
//            //if yes, check to see if the user is not logged in, show login
//            boolean registeredUser = SettingsHelper.getHelper(mActivity).isRegisteredUser();
//            if (!registeredUser){
//                //Show Anonymous Login
//                Realm.setDefaultConfiguration(UserManager.getLocalConfig());
//               // realm = Realm.getDefaultInstance();
//                loginLayout.setVisibility(View.VISIBLE);
//                settingsLayout.setVisibility(View.GONE);
//                updateUI(null);
//            }else {
//                //User has registered before, show login page
//                startActivity(new Intent(this, SignInActivity.class));}
//        }else {
//            UserManager.setActiveUser(user);
//            loginLayout.setVisibility(View.GONE);
//            settingsLayout.setVisibility(View.VISIBLE);
//            updateUI(user);
//        }
//    }

    private void checkLoginStatus() {
        //Check Firebase User status,

        final SyncUser user = SyncUser.currentUser();
        if (user == null) {
            loginLayout.setVisibility(View.VISIBLE);
            settingsLayout.setVisibility(View.GONE);
            ProntoDiaryApplication.setCloudSyncEnabled(false);
            updateUI();
        }else {
            UserManager.setActiveUser(user);
            loginLayout.setVisibility(View.GONE);
            settingsLayout.setVisibility(View.VISIBLE);
            ProntoDiaryApplication.setCloudSyncEnabled(true);
            updateUI();
        }


       //SampleData.addSampleNotes();
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

    @OnClick(R.id.image_button_notes)
    public void onNoteIconClicked(View view){
        handleNoteButtonClicked();
    }

    @OnClick(R.id.notes_text_view)
    public void onNoteTextClicked(View view){
        handleNoteButtonClicked();
    }

    @OnClick(R.id.image_button_todo_list)
    public void onTodoListIconClicked(View view){
        handleTodoListButtonClicked();
    }

    @OnClick(R.id.todo_list_text_view)
    public void onTodoListTextClicked(View view){
        handleTodoListButtonClicked();
    }

    @OnClick(R.id.image_button_folder)
    public void onFolderIconClicked(View view){
        handleFolderButtonClicked();
    }

    @OnClick(R.id.folder_text_view)
    public void onFolderTextClicked(View view){
        handleFolderButtonClicked();
    }

    @OnClick(R.id.image_button_settings)
    public void onSettingsIconClicked(View view){
        handleSettingsButtonClicked();
    }

    @OnClick(R.id.settings_text_view)
    public void onSettingsTextClicked(View view){
        handleSettingsButtonClicked();
    }

    @OnClick(R.id.image_button_login)
    public void onLoginIconClicked(View view){
        handleLoginButtonClicked();
    }

    @OnClick(R.id.login_text_view)
    public void onLoginTextClicked(View view){
        handleLoginButtonClicked();
    }


    private void handleTodoListButtonClicked() {
        resetBottomNavigationIcons();
        todoListButton.setImageResource(R.drawable.ic_task_list_dark_green);
        todoListTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary_dark));
        todoListTextView.setTypeface(todoListTextView.getTypeface(), Typeface.BOLD);
        openFragment(new TaskListFragment(), getString(R.string.title_todo_list), Constants.TODO_LIST_FRAGMENT_TAG);

    }

    private void handleFolderButtonClicked() {
        resetBottomNavigationIcons();
        folderButton.setImageResource(R.drawable.ic_folder_dark_green);
        folderTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary_dark));
        folderTextView.setTypeface(folderTextView.getTypeface(), Typeface.BOLD);
        openFragment(new FolderListFragment(), getString(R.string.label_folders), Constants.FOLDER_FRAGMENT_TAG);
    }


    private void handleSettingsButtonClicked() {
        resetBottomNavigationIcons();
        settingsButton.setImageResource(R.drawable.ic_account_passkey_dark_green);
        settingsTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary_dark));
        settingsTextView.setTypeface(settingsTextView.getTypeface(), Typeface.BOLD);
        openFragment(new AccountFragment(), getString(R.string.label_account), Constants.ACCOUNT_TAG);
    }

    private void handleLoginButtonClicked(){
        resetBottomNavigationIcons();
        loginButton.setImageResource(R.drawable.ic_login_gray);
        loginTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary_dark));
        loginTextView.setTypeface(loginTextView.getTypeface(), Typeface.BOLD);
        startActivity(new Intent(mActivity, AuthUiActivity.class));

    }

    private void handleNoteButtonClicked() {
        resetBottomNavigationIcons();
        noteButton.setImageResource(R.drawable.ic_post_it_dark_green);
        noteTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary_dark));
        noteTextView.setTypeface(noteTextView.getTypeface(), Typeface.BOLD);

        //Apply Tag filter is one exist.
        NoteListFragment fragment = new NoteListFragment();
        if (getIntent().hasExtra(Constants.TAG_FILTER)){
            String tagName = getIntent().getStringExtra(Constants.TAG_FILTER);
            fragment.setArguments(getIntent().getExtras());
            String title = getString(R.string.label_tag) + ": " + tagName;
            openFragment(new NoteListFragment(), title, Constants.NOTE_LIST_FRAGMENT_TAG);
        }else {
            openFragment(fragment, getString(R.string.label_journals), Constants.NOTE_LIST_FRAGMENT_TAG);
        }


    }


    private void updateUI() {
        addDefaultData();
        handleNoteButtonClicked();

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisplayFragmentEvent(DisplayFragmentEvent event){

        Fragment fragment = event.getFragment();
        if (fragment != null){
            openFragment(fragment, event.getTitle(), event.getTitle());
        }else {
            openFragment(new NoteListFragment(), getString(R.string.title_activity_note_list), Constants.NOTE_LIST_FRAGMENT_TAG);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRealmDatabaseRegistrationComplete(RealmDatabaseRegistrationCompletedEvent event){
        if (event.isInProgress()){
            progressDialog = new MaterialDialog.Builder(mActivity)
                    .title(getString(R.string.please_wait))
                    .content(getString(R.string.syncing_data))
                    .positiveText(getString(R.string.label_yes))
                    .show();
        }else {
            progressDialog.dismiss();
            checkLoginStatus();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddDefaultDataEvent(AddDefaultDataEvent event){
        addDefaultData();
    }

    private void resetBottomNavigationIcons() {
        noteButton.setImageResource(R.drawable.ic_post_it_gray);
        noteTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));
        noteTextView.setTypeface(noteTextView.getTypeface(), Typeface.NORMAL);

        folderButton.setImageResource(R.drawable.ic_folder_gray);
        folderTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));
        folderTextView.setTypeface(folderTextView.getTypeface(), Typeface.NORMAL);

        todoListButton.setImageResource(R.drawable.ic_task_gray);
        todoListTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));
        todoListTextView.setTypeface(todoListTextView.getTypeface(), Typeface.NORMAL);

        loginButton.setImageResource(R.drawable.ic_login_gray);
        loginTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));
        loginTextView.setTypeface(loginTextView.getTypeface(), Typeface.NORMAL);

        settingsButton.setImageResource(R.drawable.ic_account_passkey_gray);
        settingsTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.secondary_text));
        settingsTextView.setTypeface(settingsTextView.getTypeface(), Typeface.NORMAL);
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
        //getSupportActionBar().setTitle(screenTitle);
        toolbarTitle.setText(screenTitle);
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
            setupMessagingService();
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


    //Checks if this is the first time this app is running and then
    //starts an Intent Services that adds some default data
    private void addDefaultData() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        editor = preferences.edit();
        if (preferences.getBoolean(Constants.FIRST_RUN, true)) {
            startService(new Intent(this, AddSampleDataIntentService.class));
            editor.putBoolean(Constants.FIRST_RUN, false).commit();
        }

    }


    private void setupMessagingService() {
        String token = SettingsHelper.getHelper(this).getMessagingToken();
        if (TextUtils.isEmpty(token)) {
            // need to retrieve a messaging token
            Log.d(TAG, "No FCM token defined. Requesting new token.");
            token = FirebaseInstanceId.getInstance().getToken();
            SettingsHelper.getHelper(getApplicationContext()).setMessagingToken(token);
        }

        checkLoginStatus();

    }











}

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
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.AddDefaultDataEvent;
import com.okason.diary.core.events.DisplayFragmentEvent;
import com.okason.diary.core.events.ShowFragmentEvent;
import com.okason.diary.core.services.AddSampleDataIntentService;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.ui.auth.RegisterActivity;
import com.okason.diary.ui.auth.UserManager;
import com.okason.diary.ui.folder.FolderListActivity;
import com.okason.diary.ui.notes.NoteListFragment;
import com.okason.diary.ui.settings.SettingsActivity;
import com.okason.diary.ui.tag.TagListActivity;
import com.okason.diary.ui.todolist.TodoListActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class NoteListActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean unregisteredUser = false;
    private Activity mActivity;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mProntoDiaryUserRef;

    private ConnectivityManager connectivityManager;


    public static final String ACTION_IGNORE_CURRENT_USER = "action.ignoreCurrentUser";
    public static final String TAG = "NoteListActivity";


    public static final String ANONYMOUS = "anonymous";
    public static final String ANONYMOUS_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/prontonotepad-65983.appspot.com/o/annonymous_user.jpg";
    public static final String ANONYMOUS_EMAIL = "anonymous@noemail.com";
    private static final String LOG_TAG = "NoteListActivity";

    private String username;
    private String photoUrl;
    private String emailAddress;

    private AccountHeader header = null;
    private Drawer drawer = null;
    private SettingsHelper settingsHelper;
    private Uri mInvitationUrl;


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


    private MaterialDialog progressDialog;
    private Bundle savedInstanceBundle;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mActivity = this;
        settingsHelper = SettingsHelper.getHelper(mActivity);
        mAuth = FirebaseAuth.getInstance();


        savedInstanceBundle = savedInstanceState;


    }

    private void setupNavigationDrawer(Bundle savedInstanceState) {

        SettingsHelper settingsHelper = SettingsHelper.getHelper(mActivity);

        if (SyncUser.currentUser() != null){
            SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(SyncUser.currentUser(), ProntoDiaryApplication.COMMON_REALM_URL).build();
            Realm commonRealm = Realm.getInstance(syncConfiguration);
            ProntoDiaryUser prontoDiaryUser = commonRealm.where(ProntoDiaryUser.class).equalTo("realmUserId", SyncUser.currentUser().getIdentity()).findFirst();
            if (prontoDiaryUser != null){
                username = prontoDiaryUser.getDisplayName();
                emailAddress = prontoDiaryUser.getEmailAddress();
                photoUrl = prontoDiaryUser.getPhotoUrl();
            }
            commonRealm.close();
        }

        username = TextUtils.isEmpty(username) ? ANONYMOUS : username;
        emailAddress = TextUtils.isEmpty(emailAddress) ? ANONYMOUS_EMAIL : emailAddress;
        photoUrl = TextUtils.isEmpty(photoUrl) ? ANONYMOUS_PHOTO_URL : photoUrl;

        IProfile  profile = new ProfileDrawerItem()
                .withName(username)
                .withEmail(emailAddress)
                .withIcon(photoUrl)
                .withIdentifier(102);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.nav_bar_header_dark)
                .addProfiles(profile)
                .build();
        drawer = new DrawerBuilder()
                .withAccountHeader(header)
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Journals").withIcon(GoogleMaterial.Icon.gmd_calendar_note).withIdentifier(Constants.NOTES),
                        new PrimaryDrawerItem().withName("Todo List").withIcon(GoogleMaterial.Icon.gmd_format_list_bulleted).withIdentifier(Constants.TODO_LIST),
                        new PrimaryDrawerItem().withName("Folders").withIcon(GoogleMaterial.Icon.gmd_folder).withIdentifier(Constants.FOLDERS),
                        new PrimaryDrawerItem().withName("Tags").withIcon(GoogleMaterial.Icon.gmd_tag).withIdentifier(Constants.TAGS),
                        new PrimaryDrawerItem().withName("Share App").withIcon(GoogleMaterial.Icon.gmd_share).withIdentifier(Constants.SHARE_APP),
                        new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(Constants.SETTINGS)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable){
                            String name = ((Nameable) drawerItem).getName().getText(mActivity);
                            toolbar.setTitle(name);
                        }

                        if (drawerItem != null){
                            //handle on navigation drawer item
                            onTouchDrawer((int) drawerItem.getIdentifier());
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(mActivity);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();
        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_lock_open).withIdentifier(Constants.LOGIN));
        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_lock).withIdentifier(Constants.LOGOUT));
        if (SyncUser.currentUser() != null){
            drawer.removeStickyFooterItemAtPosition(0);
        } else {
            drawer.removeStickyFooterItemAtPosition(1);
        }

    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private void checkLoginStatus() {
        //Check Firebase User status,
        final SyncUser user = SyncUser.currentUser();
        if (user != null) {
            UserManager.setActiveUser(user);
        }
        //Apply Tag filter is one exist.
        NoteListFragment fragment = new NoteListFragment();
        if (getIntent().hasExtra(Constants.TAG_FILTER)){
            String tagName = getIntent().getStringExtra(Constants.TAG_FILTER);
            fragment.setArguments(getIntent().getExtras());
            String title = getString(R.string.label_tag) + ": " + tagName;
            openFragment(new NoteListFragment(), title);
        }else {
            openFragment(fragment, getString(R.string.label_journals));
        }
        //SampleData.addSampleNotes();
        setupNavigationDrawer(savedInstanceBundle);
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
        checkNetworkConnected();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowFragmentEvent(ShowFragmentEvent event){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(event.getTag());
        if (fragment != null){
            openFragment(fragment, event.getTitle());
        }else {
            openFragment(new NoteListFragment(), getString(R.string.title_activity_note_list));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisplayFragmentEvent(DisplayFragmentEvent event){

        Fragment fragment = event.getFragment();
        if (fragment != null){
            openFragment(fragment, event.getTitle());
        }else {
            openFragment(new NoteListFragment(), getString(R.string.title_activity_note_list));
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddDefaultDataEvent(AddDefaultDataEvent event){
        addDefaultData();
    }



    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void openFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(screenTitle)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }


    /**r
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


    private void onTouchDrawer(int position) {
        switch (position){
            case Constants.NOTES:
                //Do Nothing, we are already on Notes
                openFragment(new NoteListFragment(), getString(R.string.label_journals));
                break;
            case Constants.FOLDERS:
                //Go To Folders Screen
                startActivity(new Intent(mActivity, FolderListActivity.class));
                break;
            case Constants.TAGS:
                //Go To TAGS Screen
                startActivity(new Intent(mActivity, TagListActivity.class));
                break;
            case Constants.SETTINGS:
                //Go To Settings Screen
                startActivity(new Intent(mActivity, SettingsActivity.class));
                break;
            case Constants.LOGOUT:
                logout();
                break;
            case Constants.LOGIN:
                startActivity(new Intent(mActivity, RegisterActivity.class));
                break;
            case Constants.DELETE:
                //Delete Account
              //  deleteAccountClicked();
                break;
            case Constants.TODO_LIST:
                startActivity(new Intent(mActivity, TodoListActivity.class));
                break;
            case Constants.SHARE_APP:
                generateInviteLink();
                break;
        }

    }

    private void logout() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mActivity, R.style.dialog);
        LayoutInflater inflater = getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(getString(R.string.please_attention));
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage("You logged in with " + SettingsHelper.getHelper(mActivity).getLoginProvider() +
                ". Please use the same login method to sign-in again next time to access your data");
        alertDialog.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserManager.logoutActiveUser();
                //User is logging out, erased saved Profile info so that they do no show in the Nav Drawer
                //When user is not logged in
                settingsHelper.saveProfile("", "", "", "");
                startActivity(new Intent(mActivity, RegisterActivity.class));
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

    private void buildDynamicLink(){
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://prontodiary.com/"))
                .setDynamicLinkDomain(Constants.DYNAMIC_LINK_DOMAIN)
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        mInvitationUrl = dynamicLink.getUri();
    }

    private void generateInviteLink(){

        SyncUser user = SyncUser.currentUser();
        String uid = user.getIdentity();
        String link = "https://invite.prontodiary.com/?invitedby=" + uid;

        if (mFirebaseUser != null && SyncUser.currentUser() != null){
            mProntoDiaryUserRef.orderByChild("realmUserId").equalTo(SyncUser.currentUser().getIdentity()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            ProntoDiaryUser prontoDiaryUser = snapshot.getValue(ProntoDiaryUser.class);
                            sendInvite(prontoDiaryUser.getRealmUserId(), prontoDiaryUser.getDisplayName());
                        } catch (Exception e) {
                            //Failed to retrieve Pronto Diary User object
                            e.printStackTrace();
                            Log.d(NoteListActivity.TAG, e.getLocalizedMessage());
                            return;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    private void sendInvite(String realmUserId, String displayName){

        String link = "https://invite.prontodiary.com/?invitedby=" + realmUserId;

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain(Constants.DYNAMIC_LINK_DOMAIN)
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        mInvitationUrl = dynamicLink.getUri();

       // String referrerName = SettingsHelper.getHelper(mActivity).getDisplayName();
        String subject = String.format("%s wants you to try Pronto Diary App!", displayName);
        String invitationLink = mInvitationUrl.toString();
        String msg = "Capture important thoughts and moments with Pronto Diary App! Use my referrer link: "
                + invitationLink;
        String msgHtml = String.format("<p>Start Journaling with Pronto Diary's! Use my "
                + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}

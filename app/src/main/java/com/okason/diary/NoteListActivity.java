package com.okason.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
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
import com.okason.diary.billing.BillingManager;
import com.okason.diary.billing.BillingProvider;
import com.okason.diary.billing.MainViewController;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.ui.auth.AuthUiActivity;
import com.okason.diary.ui.folder.FolderListActivity;
import com.okason.diary.ui.notes.NotesFragment;
import com.okason.diary.ui.settings.SettingsActivity;
import com.okason.diary.ui.tag.TagListActivity;
import com.okason.diary.ui.todolist.TodoListActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteListActivity extends AppCompatActivity implements BillingProvider {
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean unregisteredUser = false;
    private Activity mActivity;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAnalytics firebaseAnalytics;
    private static final int REQUEST_INVITE = 0;


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
    private FloatingActionButton floatingActionButton;

    private BillingManager mBillingManager;
    private MainViewController mViewController;


    @BindView(R.id.root)
    View mRootView;

    private Bundle savedInstanceBundle;
    private static final String LOGO_URL = "https://firebasestorage.googleapis.com/v0/b/prontodiary-bee92.appspot.com/o/pronto_diary_high_res.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = this;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        settingsHelper = SettingsHelper.getHelper(mActivity);
        firebaseAnalytics = FirebaseAnalytics.getInstance(mActivity);
        setupNavigationDrawer(savedInstanceBundle);
        showFloatingActionButton();
        //new SampleData(this).getSampleNotesRealm();;

        mViewController = new MainViewController(this);
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());

    }

    /**
     * Only show Floating Action Button when this Fragment is attached to the Main Activity
     * Do not show when this Fragment is attached to Folder Activity
     */
    private void showFloatingActionButton() {
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, AddNoteActivity.class));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
      //  EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
      //  EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
       showNoteListFragment();
      //  checkForDynamicLinkInvite(getIntent());
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void setupNavigationDrawer(Bundle savedInstanceState) {

        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            try {
                username = firebaseUser.getDisplayName();
                emailAddress = firebaseUser.getEmail();
                photoUrl = firebaseUser.getPhotoUrl().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            username = TextUtils.isEmpty(username) ? ANONYMOUS : username;
            emailAddress = TextUtils.isEmpty(emailAddress) ? ANONYMOUS_EMAIL : emailAddress;
            photoUrl = TextUtils.isEmpty(photoUrl) ? ANONYMOUS_PHOTO_URL : photoUrl;

            IProfile profile = new ProfileDrawerItem()
                    .withName(username)
                    .withEmail(emailAddress)
                    .withIcon(photoUrl)
                    .withIdentifier(102);

            header = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.nav_bar_header_dark)
                    .addProfiles(profile)
                    .build();

        }else {
            header = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.nav_bar_header_dark)
                    .build();
        }


        drawer = new DrawerBuilder()
                .withAccountHeader(header)
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Journals").withIcon(GoogleMaterial.Icon.gmd_calendar_note).withIdentifier(Constants.JOURNALS),
                        new PrimaryDrawerItem().withName("Todo List").withIcon(GoogleMaterial.Icon.gmd_format_list_bulleted).withIdentifier(Constants.TODO_LIST),
                        new PrimaryDrawerItem().withName("Folders").withIcon(GoogleMaterial.Icon.gmd_folder).withIdentifier(Constants.FOLDERS),
                        new PrimaryDrawerItem().withName("Tags").withIcon(GoogleMaterial.Icon.gmd_tag).withIdentifier(Constants.TAGS),
                        new PrimaryDrawerItem().withName("Share App").withIcon(GoogleMaterial.Icon.gmd_share).withIdentifier(Constants.SHARE_APP),
                        new PrimaryDrawerItem().withName("Contact Developer").withIcon(GoogleMaterial.Icon.gmd_email).withIdentifier(Constants.CONTACT_US),
                        new PrimaryDrawerItem().withName("Remove Ads").withIcon(GoogleMaterial.Icon.gmd_email).withIdentifier(Constants.REMOVE_ADS),
                        new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(Constants.SETTINGS)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            String name = ((Nameable) drawerItem).getName().getText(mActivity);
                            toolbar.setTitle(name);
                        }

                        if (drawerItem != null) {
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
        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Enable Sync").withIcon(GoogleMaterial.Icon.gmd_lock_open).withIdentifier(Constants.LOGIN));
     //   drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_lock).withIdentifier(Constants.LOGOUT));

        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            drawer.removeStickyFooterItemAtPosition(0);
        }
//        else {
//            drawer.removeStickyFooterItemAtPosition(1);
//        }


    }


    @Override
    public void onBackPressed() {
        finish();
    }


//    private void checkLoginStatus() {
//        if (firebaseUser != null) {
//            showNoteListFragment();
//        }else {
//            startActivity(AuthUiActivity.createIntent(mActivity));
//        }
//
//    }

    private void showNoteListFragment() {
        //Apply ProntoTag filter is one exist.

        boolean exist = getIntent() != null;
        boolean hasTag = getIntent().hasExtra(Constants.TAG_FILTER);
        if (exist && hasTag) {
            String tagName = getIntent().getStringExtra(Constants.TAG_FILTER);
            NotesFragment fragment = NotesFragment.newInstance(false, tagName, "");
            String title = "#" + tagName;
            openFragment(fragment, title);
        } else {
            openFragment(new NotesFragment(), getString(R.string.label_journals));
        }


    }




//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onShowFragmentEvent(ShowFragmentEvent event) {
//
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(event.getTag());
//        if (fragment != null) {
//            openFragment(fragment, event.getTitle());
//        } else {
//            openFragment(new NoteListFragment(), getString(R.string.title_activity_note_list));
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDisplayFragmentEvent(DisplayFragmentEvent event) {
//
//        Fragment fragment = event.getFragment();
//        if (fragment != null) {
//            openFragment(fragment, event.getTitle());
//        } else {
//            openFragment(new NoteListFragment(), getString(R.string.title_activity_note_list));
//        }
//    }


    private void makeToast(String message) {
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.primary));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void openFragment(Fragment fragment, String screenTitle) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(screenTitle)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }


    private void onTouchDrawer(int position) {
        switch (position) {
            case Constants.JOURNALS:
                //Do Nothing, we are already on Notes
                //openFragment(new NoteListFragment(), getString(R.string.label_journals));
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
                startActivity(new Intent(mActivity, AuthUiActivity.class));
                break;
            case Constants.DELETE:
                //Delete Account
                //  deleteAccountClicked();
                break;
            case Constants.TODO_LIST:
                startActivity(new Intent(mActivity, TodoListActivity.class));
                break;
            case Constants.CONTACT_US:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@okason.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Pronto Journal Support");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    makeToast("No email client found");
                }
                break;
            case Constants.SHARE_APP:
                if (settingsHelper.isRegisteredUser() && firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
                    generateInviteLink();
                } else {
                    startActivity(new Intent(mActivity, AuthUiActivity.class));
                }
                break;
            case Constants.REMOVE_ADS:
                mBillingManager.initiatePurchaseFlow(MainViewController.SKU_ID_PREMIUM, BillingClient.SkuType.INAPP);
                break;

        }

    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(NoteListActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(mActivity, AuthUiActivity.class));
                            finish();
                        } else {
                            makeToast(getString(R.string.sign_out_failed));
                        }
                    }
                });

    }


    private void generateInviteLink() {
        if (firebaseUser != null) {

            String uid = firebaseUser.getUid();
            String name = firebaseUser.getDisplayName();
            sendInvite(uid, name);
        } else {
            makeToast(getString(R.string.login_required));
        }
    }

    private void sendInvite(String realmUserId, final String displayName) {

        String link = "https://invite.prontodiary.com/?invitedby=" + realmUserId;

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain("by3kf.app.goo.gl")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();


        com.google.android.gms.tasks.Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.getUri())
                .buildShortDynamicLink()
                .addOnCompleteListener(mActivity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();

                            // String referrerName = settingsHelper.getDisplayName();
                            String subject = String.format("Try Pronto Diary App!", displayName);
                            String invitationLink = shortLink.toString();
                            String msg = "Capture important thoughts and moments with Pronto Diary App!";
                            String msgHtml = String.format("<p>Start Journaling with Pronto Diary's! Use my "
                                    + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

                            Intent intent = new AppInviteInvitation.IntentBuilder(subject)
                                    .setMessage(msg)
                                    .setDeepLink(shortLink)
                                    .setCustomImage(Uri.parse(LOGO_URL))
                                    .setCallToActionText(getString(R.string.invitation_cta))
                                    .build();
                            startActivityForResult(intent, REQUEST_INVITE);
                        } else {
                            String errorMessage = task.getException().getCause().getMessage();
                            makeToast("Short link task is not successful");
                        }

                    }
                });


        // mInvitationUrl = dynamicLink.getUri();


    }

    private void checkForDynamicLinkInvite(Intent intent) {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(mActivity, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            if (deepLink != null && deepLink.getBooleanQueryParameter("invitedby", false)) {
                                String referrerUid = deepLink.getQueryParameter("invitedby");
                                if (!TextUtils.isEmpty(referrerUid)) {
//                                    ProntoJournalUser referrer = UserManager.getProntoDiaryUserById(referrerUid);
//                                    if (referrer != null){
//                                        makeToast("Welcome " + referrer.getDisplayName() + " friend!");
//                                    }
                                }
                            }
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }


    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isPremiumPurchased() {
        return mViewController.isPremiumPurchased();
    }

    public void onBillingManagerSetupFinished() {
        makeToast("onBillingManagerSetupFinished");
//        if (mAcquireFragment != null) {
//            mAcquireFragment.onManagerReady(this);
//        }
    }
    /**
     * Remove loading spinner and refresh the UI
     */
    public void showRefreshedUi() {
        makeToast("showRefreshedUi");
    }

}

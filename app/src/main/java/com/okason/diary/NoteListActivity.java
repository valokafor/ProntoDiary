package com.okason.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.okason.diary.core.events.ShowFragmentEvent;
import com.okason.diary.data.RealmManager;
import com.okason.diary.data.SampleData;
import com.okason.diary.models.Note;
import com.okason.diary.ui.auth.SignInActivity;
import com.okason.diary.ui.notes.NoteListFragment;
import com.okason.diary.ui.settings.SettingsActivity;
import com.okason.diary.ui.settings.SyncFragment;
import com.okason.diary.ui.todolist.TodoListFragment;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncUser;

public class NoteListActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private boolean unregisteredUser = false;
    private Activity mActivity;


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

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActivity = this;
        realm = Realm.getDefaultInstance();


        //Check to see if the user has registered before
        //if yes, check to see if the user is not logged in, show login
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean unregisteredUser = preferences.getBoolean(Constants.UNREGISTERED_USER, true);
        if (unregisteredUser) {
            syncLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        }else {
            syncLayout.setVisibility(View.GONE);
            final SyncUser user = SyncUser.currentUser();
            if (user == null) {
                loginLayout.setVisibility(View.VISIBLE);
            }else {
                loginLayout.setVisibility(View.GONE);
            }

        }
        updateUI();
        //addSampleData();
    }



    @Override
    public void onStart() {
        super.onStart();
        RealmResults<Note> notes = realm.where(Note.class).findAll();
        if (notes != null && notes.size() > 0){
            Log.d(TAG, "Count of Notes: " + notes.size());
            for (Note note: notes){
                Log.d(TAG, note.getTitle());
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    private void updateUI() {
        noteButton.setImageResource(R.drawable.ic_action_book_red_light);
        noteTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
        openFragment(new NoteListFragment(), getString(R.string.label_journals), Constants.NOTE_LIST_FRAGMENT_TAG);

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBottomNavigationIcons();
                noteButton.setImageResource(R.drawable.ic_action_book_red_light);
                noteTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
                openFragment(new NoteListFragment(), getString(R.string.title_activity_note_list), Constants.NOTE_LIST_FRAGMENT_TAG);
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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBottomNavigationIcons();
                loginButton.setImageResource(R.drawable.ic_action_lock_open_light_red);
                loginTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.primary));
                startActivity(new Intent(mActivity, SignInActivity.class));

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


    private void addSampleData(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    List<Note> notes = SampleData.getSampleNotes();
                    for (Note note: notes){
                        realm.beginTransaction();
                        long id = RealmManager.getNextNoteId(realm);
                        Note savedNote = realm.createObject(Note.class, id);
                        savedNote.setTitle(note.getTitle());
                        savedNote.setContent(note.getContent());
                        savedNote.setDateModified(note.getDateCreated());
                        realm.commitTransaction();
                    }

                }finally {
                    realm.close();
                }

            }
        });
        thread.start();
    }



}

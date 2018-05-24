package com.okason.diary.ui.addnote;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.data.NoteDao;
import com.okason.diary.models.realmentities.NoteEntity;
import com.okason.diary.ui.notedetails.NewNoteDetailFragment;
import com.okason.diary.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class AddNoteActivity extends AppCompatActivity {


    private final static String LOG_TAG = AddNoteActivity.class.getSimpleName();
    private Activity mActivity;
    @BindView(android.R.id.content)
    View mRootView;
    private Realm realm;


    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mActivity = this;
        realm = Realm.getDefaultInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_check_white_24dp);



        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(Constants.NOTE_ID)) {
                String noteId = getIntent().getStringExtra(Constants.NOTE_ID);
                NoteEntity passedInNote = new NoteDao(realm).getNoteEntityById(noteId);
                NewNoteDetailFragment fragment = NewNoteDetailFragment.newInstance(noteId);
                openFragment(NewNoteEditorFragment.newInstance(noteId),
                        passedInNote.getTitle());
            } else {
                openFragment(NewNoteEditorFragment.newInstance(""), getString(R.string.add_new_journal));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void openFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.container, fragment)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }



    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

}

package com.okason.diary.ui.notedetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.okason.diary.R;
import com.okason.diary.data.NoteDao;
import com.okason.diary.models.realmentities.NoteEntity;
import com.okason.diary.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class NoteDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(Constants.NOTE_ID)) {
                String noteId = getIntent().getStringExtra(Constants.NOTE_ID);
                NoteEntity passedInNote = new NoteDao(realm).getNoteEntityById(noteId);
                NewNoteDetailFragment fragment = NewNoteDetailFragment.newInstance(noteId);
                if (passedInNote != null) {
                    openFragment(fragment, passedInNote.getTitle());
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
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

    /**
     * Creates an Intent that is used to start this Activity
     * @param context - this context
     * @param noteId - Note id
     * @return
     */
    public static Intent getStartIntent(final Context context, final String noteId) {
        Intent intent = new Intent(context, NoteDetailActivity.class);
        intent.putExtra(Constants.NOTE_ID, noteId);
        return intent;
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

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}

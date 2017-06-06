package com.okason.diary.ui.notedetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okason.diary.R;
import com.okason.diary.core.events.EditNoteButtonClickedEvent;
import com.okason.diary.models.Note;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().hasExtra(Constants.SERIALIZED_NOTE)) {

            Note passedInNote = getCurrentNote(getIntent());
            String noteId = passedInNote.getId();
            NoteDetailFragment fragment = NoteDetailFragment.newInstance(noteId);
            if (passedInNote != null) {
                openFragment(fragment, TimeUtils.getReadableModifiedDate(passedInNote.getDateModified()));
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    public Note getCurrentNote(Intent intent){
        if (intent != null && getIntent().hasExtra(Constants.SERIALIZED_NOTE)){
            String serializedNote = intent.getStringExtra(Constants.SERIALIZED_NOTE);
            if (!serializedNote.isEmpty()){
                Gson gson = new Gson();
                Note note = gson.fromJson(serializedNote, new TypeToken<Note>(){}.getType());
                return note;
            }
        }
        return null;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditNoteButtonClickedEvent(EditNoteButtonClickedEvent event){
        Intent editNoteIntent = new Intent(NoteDetailActivity.this, AddNoteActivity.class);
        editNoteIntent.putExtra(Constants.NOTE_ID, event.getClickedNote().getId());
        startActivity(editNoteIntent);
    }



    /**
     * Creates an Intent that is used to start this Activity
     * @param context - this context
     * @param serializedNote - Serialized Note that will be show first
     * @return
     */
    public static Intent getStartIntent(final Context context, final String serializedNote) {
        Intent intent = new Intent(context, NoteDetailActivity.class);
        intent.putExtra(Constants.SERIALIZED_NOTE, serializedNote);
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


}

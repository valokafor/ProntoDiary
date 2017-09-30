package com.okason.diary.ui.notedetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnEditNoteButtonClickedListener;
import com.okason.diary.models.Note;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.utils.Constants;

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

        final Gson gson = new Gson();

        if (getIntent() != null && getIntent().hasExtra(Constants.SERIALIZED_NOTE)){
            String serializedNote = getIntent().getStringExtra(Constants.SERIALIZED_NOTE);
            Note passedInNote = gson.fromJson(serializedNote, Note.class);
            String title = getString(R.string.note_detail);
            NoteDetailFragment fragment = NoteDetailFragment.newInstance(serializedNote);
            fragment.setEditNoteistener(new OnEditNoteButtonClickedListener() {
                @Override
                public void onEditNote(Note clickedNote) {
                    String serializedNote = gson.toJson(clickedNote);
                    Intent editNoteIntent = new Intent(NoteDetailActivity.this, AddNoteActivity.class);
                    editNoteIntent.putExtra(Constants.SERIALIZED_NOTE, serializedNote);
                    startActivity(editNoteIntent);
                }
            });

            openFragment(fragment, title);
        }else {
            finish();
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

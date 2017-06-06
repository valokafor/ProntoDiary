package com.okason.diary.ui.notedetails;

import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.core.events.EditNoteButtonClickedEvent;
import com.okason.diary.models.Note;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Valentine on 5/13/2017.
 */

public class NoteDetailPresenter implements NoteDetailContract.Action {
    private final NoteDetailContract.View mView;
    private final DatabaseReference noteCloudReference;
    private Note mCurrentNote = null;


    private final String noteId;

    public NoteDetailPresenter(final NoteDetailContract.View view, DatabaseReference noteCloudReference, String noteId) {
        this.mView = view;
        this.noteCloudReference = noteCloudReference;
        this.noteId = noteId;

        if (!TextUtils.isEmpty(noteId)){
            Query noteQuery = noteCloudReference.orderByChild("id").equalTo(noteId);
            noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null){
                        mCurrentNote = dataSnapshot.getValue(Note.class);
                        showNoteDetails();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mView.showMessage(databaseError.getMessage());
                }
            });
        }
    }

    @Override
    public void onEditNoteClick() {
        EventBus.getDefault().post(new EditNoteButtonClickedEvent(mCurrentNote));
    }

    @Override
    public void showNoteDetails() {
        mView.displayNote(mCurrentNote);

    }

    @Override
    public void onDeleteNoteButtonClicked() {
        mView.showDeleteConfirmation(mCurrentNote);

    }

    @Override
    public String getCurrentNoteId() {
       return noteId;
    }

    @Override
    public Note getCurrentNote() {
        return mCurrentNote;
    }

    @Override
    public void deleteNote() {
      //  mRepository.deleteNote(noteId);
    }
}

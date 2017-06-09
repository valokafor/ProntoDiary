package com.okason.diary.ui.notes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.okason.diary.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentine on 4/15/2017.
 */

public class NoteListPresenter implements NoteListContract.Actions{
    private final NoteListContract.View mView;
    private final DatabaseReference noteCloudReference;


    private boolean isDualScreen = false;

    public NoteListPresenter(NoteListContract.View mView, DatabaseReference noteCloudReference) {
        this.mView = mView;
        this.noteCloudReference = noteCloudReference;
    }


    @Override
    public void loadNotes(boolean forceUpdate) {
        mView.setProgressIndicator(true);
        final List<Note> notes = new ArrayList<>();
        noteCloudReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    Note note = noteSnapshot.getValue(Note.class);
                    notes.add(note);
                }
                if (notes != null && notes.size() > 0){
                    mView.showEmptyText(false);
                    mView.showNotes(notes);
                    mView.setProgressIndicator(false);
                }else {
                    mView.showEmptyText(true);
                    mView.setProgressIndicator(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void deleteNote(Note note) {
       // mRepository.deleteNote(note.getId());
    }




}

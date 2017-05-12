package com.okason.diary.ui.addnote;

import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.ui.notes.NoteListContract;

/**
 * Created by Valentine on 5/8/2017.
 */

public class AddNotePresenter implements AddNoteContract.Action {

    private final AddNoteContract.View mView;
    private  NoteListContract.Repository mRepository;
    private Note mCurrentNote = null;

    private boolean isDualScreen = false;

    public AddNotePresenter(AddNoteContract.View mView) {
        this.mView = mView;
        mRepository = new NoteRealmRepository();
    }

    @Override
    public void deleteJournal() {

    }

    @Override
    public void onTitleChange(String newTitle) {
        if (mCurrentNote == null){
            mCurrentNote = mRepository.createNewNote();
        }
        mRepository.updatedNoteTitle(mCurrentNote.getId(), newTitle);

    }



    @Override
    public void onFolderChange(Folder newFolder) {

    }

    @Override
    public void onNoteContentChange(String newContent) {
        if (mCurrentNote == null){
            mCurrentNote = mRepository.createNewNote();
        }
        mRepository.updatedNoteContent(mCurrentNote.getId(), newContent);
    }

    @Override
    public void getCurrentNote(String noteId) {
        mCurrentNote = mRepository.getNoteById(noteId);
        if (mCurrentNote != null){
            mView.populateNote(mCurrentNote);
        }
    }

}

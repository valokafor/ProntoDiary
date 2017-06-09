package com.okason.diary.ui.addnote;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;

/**
 * Created by Valentine on 5/8/2017.
 */

public class AddNotePresenter implements AddNoteContract.Action {

    private final AddNoteContract.View mView;
    private final DatabaseReference noteCloudReference;
    private Note mCurrentNote = null;
    private boolean dataChanged = false;
    private boolean isInEditMode = false;


    private boolean isDualScreen = false;

    public AddNotePresenter(final AddNoteContract.View mView, DatabaseReference noteCloudReference, Note note) {
        this.mView = mView;
        this.noteCloudReference = noteCloudReference;
        this.mCurrentNote = note;
        if (note != null){
            isInEditMode = true;
        }else {
            mCurrentNote = new Note();
        }


    }

    @Override
    public void deleteJournal() {

    }

    @Override
    public void onDeleteNoteButtonClicked() {
        if (mCurrentNote == null){
            mView.showMessage(ProntoDiaryApplication.getAppContext().getString(R.string.no_notes_found));
            return;
        }
    }

    @Override
    public void onTitleChange(String newTitle) {
        if (mCurrentNote == null){
            mCurrentNote = new Note();
        }
        mCurrentNote.setTitle(newTitle);
        dataChanged = true;

    }



    @Override
    public void onFolderChange(Folder newFolder) {

    }

    @Override
    public void onNoteContentChange(String newContent) {
        if (mCurrentNote == null){
            mCurrentNote = new Note();
        }
        mCurrentNote.setContent(newContent);
        dataChanged = true;
    }



    @Override
    public Note getCurrentNote() {
        return mCurrentNote;
    }

    @Override
    public String getCurrentNoteId() {
        if (mCurrentNote != null){
            return mCurrentNote.getId();
        }
        return null;
    }

    @Override
    public void updateUI() {
        if (mCurrentNote != null){
            mView.populateNote(mCurrentNote);
        }

    }
//
//    @Override
//    public void updatedUI() {
//        updatetNote(mCurrentNote.getId());
//    }

    /**
     * Called when an attachment is added to a Note
     * @param attachment - the added attachment
     */
    @Override
    public void onAttachmentAdded(Attachment attachment) {

        if (mCurrentNote == null){
            mCurrentNote = new Note();
        }

        //Add the attachment to the Note
        mCurrentNote.getAttachments().add(attachment);
        dataChanged = true;
        updateUI();
    }

    @Override
    public void onSaveAndExit() {

        if (dataChanged){
            mView.showMessage(ProntoDiaryApplication.getAppContext().getString(R.string.saving_journal));

            //Check to see if the Note is completely blank
            if (TextUtils.isEmpty(mCurrentNote.getTitle())
                    && TextUtils.isEmpty(mCurrentNote.getContent())
                    && mCurrentNote.getAttachments().size() == 0){
                return;
            }

            //Check to see if Title is empty
            if (TextUtils.isEmpty(mCurrentNote.getTitle())){
                mCurrentNote.setTitle(ProntoDiaryApplication.getAppContext().getString(R.string.missing_title));
            }

            //Check to see if content is empty
            if (TextUtils.isEmpty(mCurrentNote.getContent())){
                mCurrentNote.setContent(ProntoDiaryApplication.getAppContext().getString(R.string.missing_content));
            }

            //Data need to be saved
            if (isInEditMode){
                //Update data
                noteCloudReference.child(mCurrentNote.getId()).setValue(mCurrentNote);
            }else {
                //Save new data
                String key = noteCloudReference.push().getKey();
                mCurrentNote.setId(key);
                noteCloudReference.child(key).setValue(mCurrentNote);
            }
        }





    }



}

package com.okason.diary.ui.addnote;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
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

    private boolean isDualScreen = false;

    public AddNotePresenter(AddNoteContract.View mView, DatabaseReference noteCloudReference) {
        this.mView = mView;
        this.noteCloudReference = noteCloudReference;
    }

    @Override
    public void deleteJournal() {

    }

    @Override
    public void onDeleteNoteButtonClicked() {
        if (mCurrentNote == null){

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
        //First ensure a Note has been created
        if (mCurrentNote == null){
            mCurrentNote = new Note();
        }
        //Add the attachment to the Note
        mCurrentNote.getAttachments().add(attachment);
        mView.showProgressDialog();


    }

    @Override
    public void onFileAttachmentSelected(Uri fileUri, String fileName) {
        //First ensure a Note has been created
        if (mCurrentNote == null){
            mCurrentNote = new Note();
        }

        noteCloudReference.se

        mRepository.addFileAttachment(fileUri, fileName, mCurrentNote.getId());
        mView.showProgressDialog();
    }

}

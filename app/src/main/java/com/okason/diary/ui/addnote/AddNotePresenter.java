package com.okason.diary.ui.addnote;

import android.net.Uri;
import android.text.TextUtils;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Attachment;
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
    private String title = "";
    private String content = "";

    public AddNotePresenter(AddNoteContract.View mView) {
        this.mView = mView;
        mRepository = new NoteRealmRepository();
    }

    @Override
    public void deleteJournal() {

    }

    @Override
    public void onSaveAndExit() {
        //User has clicked Save and Exit button
        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)){
            mView.showMessage(ProntoDiaryApplication.getAppContext().getString(R.string.saving_journal));

            if (mCurrentNote == null){
                mCurrentNote = mRepository.createNewNote();
            }


            //Check to see if Title is empty
            if (TextUtils.isEmpty(title)){
                mCurrentNote.setTitle(ProntoDiaryApplication.getAppContext().getString(R.string.missing_title));
            }

            //Check to see if content is empty
            if (TextUtils.isEmpty(content)){
                mCurrentNote.setContent(ProntoDiaryApplication.getAppContext().getString(R.string.missing_content));
            }

            mRepository.updatedNoteContent(mCurrentNote.getId(), content);
            mRepository.updatedNoteTitle(mCurrentNote.getId(), title);
        }

    }

    @Override
    public void onDeleteNoteButtonClicked() {
        if (mCurrentNote == null){

        }
    }

    @Override
    public void onTitleChange(String newTitle) {
      title = newTitle;

    }



    @Override
    public void onFolderChange(Folder newFolder) {

    }

    @Override
    public void onNoteContentChange(String newContent) {
        content = newContent;
    }

    @Override
    public void updatedtNote(String noteId) {
        mCurrentNote = mRepository.getNoteById(noteId);
        if (mCurrentNote != null){
            mView.populateNote(mCurrentNote);
        }
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
    public void updatedUI() {
        updatedtNote(mCurrentNote.getId());
    }

    /**
     * Called when an attachment is added to a Note
     * @param attachment - the added attachment
     */
    @Override
    public void onAttachmentAdded(Attachment attachment) {
        //First ensure a Note has been created
        if (mCurrentNote == null){
            mCurrentNote = mRepository.createNewNote();
        }

        //Add the attachment to the Note
        mRepository.addAttachment(mCurrentNote.getId(), attachment);
        mView.showProgressDialog();


    }

    @Override
    public void onFileAttachmentSelected(Uri fileUri, String fileName) {
        //First ensure a Note has been created
        if (mCurrentNote == null){
            mCurrentNote = mRepository.createNewNote();
        }

        mRepository.addFileAttachment(fileUri, fileName, mCurrentNote.getId());
        mView.showProgressDialog();
    }

}

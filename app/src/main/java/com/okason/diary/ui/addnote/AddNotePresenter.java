package com.okason.diary.ui.addnote;

import android.text.TextUtils;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.data.FolderRealmRepository;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;

import java.util.List;

import static com.okason.diary.core.ProntoDiaryApplication.getAppContext;

/**
 * Created by Valentine on 5/8/2017.
 */

public class AddNotePresenter implements AddNoteContract.Action {

    private final AddNoteContract.View mView;
    private  AddNoteContract.Repository mRepository;
    private Note mCurrentNote = null;

    private boolean dataChanged = false;
    private boolean isInEditMode = false;

    private String title;
    private String content;


    private boolean isDualScreen = false;

    public AddNotePresenter(final AddNoteContract.View mView, String noteid) {
        this.mView = mView;
        mRepository = new NoteRealmRepository();

        if (!TextUtils.isEmpty(noteid)){
            mCurrentNote = mRepository.getNoteById(noteid);
            mView.populateNote(mCurrentNote);
        }
    }

    @Override
    public void deleteJournal() {

    }

    @Override
    public void onDeleteNoteButtonClicked() {
        if (mCurrentNote == null){
            mView.showMessage(getAppContext().getString(R.string.no_notes_found));
            return;
        }
    }

    @Override
    public void onTitleChange(String newTitle) {
        dataChanged = true;

    }



    @Override
    public void onFolderChange(String folderId) {
        if (mCurrentNote == null){
            mCurrentNote = mRepository.createNewNote();
        }
        mRepository.setFolder(folderId, mCurrentNote.getId());
        dataChanged = true;

    }

    @Override
    public void onTagAdded(Tag tag) {
        if (mCurrentNote == null){
            mCurrentNote = mRepository.createNewNote();
        }
        mRepository.addTag(mCurrentNote.getId(), tag.getId());
        dataChanged = true;

    }

    @Override
    public void onTagRemoved(Tag tag) {
        if (mCurrentNote != null){
            mRepository.removeTag(mCurrentNote.getId(), tag.getId());
            dataChanged = true;

        }
    }

    @Override
    public void onNoteContentChange(String newContent) {
        dataChanged = true;
    }



    @Override
    public Note getCurrentNote() {
        if (mCurrentNote == null){
            mCurrentNote = mRepository.createNewNote();
        }
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

    @Override
    public List<Tag> getAllTags() {
        return mRepository.getAllTags();
    }

    @Override
    public List<Folder> getAllFolders() {
        return new FolderRealmRepository().getAllFolders();
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
            mCurrentNote = mRepository.createNewNote();
        }

        mRepository.updatedNoteContent(mCurrentNote.getId(), mView.getContent());
        mRepository.updatedNoteTitle(mCurrentNote.getId(), mView.getTitle());
        //Add the attachment to the Note
        mRepository.addAttachment(mCurrentNote.getId(), attachment);
    }




    @Override
    public void onSaveAndExit() {

        if (dataChanged){
            mView.showMessage(getAppContext().getString(R.string.saving_journal));

            if (mCurrentNote == null){
                mCurrentNote = mRepository.createNewNote();
            }


            //Check to see if Title is empty
            if (TextUtils.isEmpty(mView.getTitle())){
                title = ProntoDiaryApplication.getAppContext().getString(R.string.missing_title);
            }else {
                title = mView.getTitle();
            }

            //Check to see if content is empty
            if (TextUtils.isEmpty(mView.getContent())){
                content = ProntoDiaryApplication.getAppContext().getString(R.string.missing_content);
            }else {
                content = mView.getContent();
            }

            mRepository.updatedNoteContent(mCurrentNote.getId(), content);
            mRepository.updatedNoteTitle(mCurrentNote.getId(), title);

        }




            //Upload the attachments to cloud
//            if (ProntoDiaryApplication.isCloudSyncEnabled() && mCurrentNote != null &&
//                    !TextUtils.isEmpty(mCurrentNote.getId()) && mCurrentNote.getAttachments().size() > 0){
//                // Start MyUploadService to upload the file, so that the file is uploaded
//                // even if this Activity is killed or put in the background
//                Toast.makeText(ProntoDiaryApplication.getAppContext(),ProntoDiaryApplication.getAppContext()
//                        .getString(R.string.progress_uploading), Toast.LENGTH_SHORT );
//                Intent uploadServiceIntent = new Intent( mView.getContext(), AttachmentUploadService.class)
//                        .putExtra(AttachmentUploadService.NOTE_ID, mCurrentNote.getId())
//                        .setAction(AttachmentUploadService.ACTION_UPLOAD);
//               mView.getContext().startService(uploadServiceIntent);
//            }


        mView.goBackToParent();


    }

    @Override
    public Folder getFolderById(String id) {
        return new FolderRealmRepository().getFolderById(id);
    }


}

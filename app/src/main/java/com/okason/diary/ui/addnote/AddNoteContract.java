package com.okason.diary.ui.addnote;

import android.content.Context;

import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;

/**
 * Created by Valentine on 5/8/2017.
 */

public interface AddNoteContract {

    interface View{
        void showMessage(String message);
        void populateNote(Note note);
        void showProgressDialog();
        void hideProgressDialog();
        void goBackToParent();
        Context getContext();


    }

    interface Action{
        void deleteJournal();
        void onDeleteNoteButtonClicked();
        void onTitleChange(String newTitle);
        void onFolderChange(Folder newFolder);
        void onNoteContentChange(String newContent);
        String getCurrentNoteId();
        Note getCurrentNote();
        void updateUI();
        void onAttachmentAdded(Attachment attachment);
        void onSaveAndExit();

    }


}

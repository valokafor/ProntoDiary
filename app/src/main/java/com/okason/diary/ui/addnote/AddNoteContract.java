package com.okason.diary.ui.addnote;

import android.net.Uri;

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

    }

    interface Action{
        void deleteJournal();
        void onDeleteNoteButtonClicked();
        void onTitleChange(String newTitle);
        void onFolderChange(Folder newFolder);
        void onNoteContentChange(String newContent);
        Note getCurrentNote();
        String getCurrentNoteId();
        void updatedUI();
        void onAttachmentAdded(Attachment attachment);
        void onFileAttachmentSelected(Uri fileUri, String fileName);
    }


}

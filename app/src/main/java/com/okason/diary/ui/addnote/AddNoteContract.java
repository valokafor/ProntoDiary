package com.okason.diary.ui.addnote;

import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;

/**
 * Created by Valentine on 5/8/2017.
 */

public interface AddNoteContract {

    interface View{
        void populateNote(Note note);

    }

    interface Action{
        void deleteJournal();
        void onTitleChange(String newTitle);
        void onFolderChange(Folder newFolder);
        void onNoteContentChange(String newContent);
        void getCurrentNote(String noteId);
        String getCurrentNoteId();
        void updatedUI();
        void onAttachmentAdded(Attachment attachment);
    }


}

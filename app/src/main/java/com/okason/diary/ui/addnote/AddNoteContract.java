package com.okason.diary.ui.addnote;

import android.content.Context;

import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;

import java.util.List;

import io.realm.Realm;

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
        String getTitle();
        String getContent();
    }

    interface Action{
        void deleteJournal();
        void onDeleteNoteButtonClicked();
        void onTitleChange(String newTitle);
        void onFolderChange(String folderId);
        void onTagAdded(Tag tag);
        void onTagRemoved(Tag tag);
        void onNoteContentChange(String newContent);
        String getCurrentNoteId();
        Note getCurrentNote();
        void updateUI();
        List<Tag> getAllTags();
        List<Folder> getAllFolders();
        void onAttachmentAdded(Attachment attachment);
        void onSaveAndExit();
        Folder getFolderById(String id);


    }

    interface Repository{
        List<Note> getAllNotes();
        List<Tag> getAllTags();
        int getNotePosition(String noteId);
        Note getNoteById(String noteId);
        Note createNewNote();
        void updatedNoteTitle(String noteId, String title);
        void updatedNoteContent(String noteId, String content);
        void setFolder(String folderId, String noteId);
        void addTag(String noteId, String tagId);
        void removeTag(String noteId, String tagId);
        void deleteNote(String noteId);
        void saveNote(Note note);
        Attachment getAttachmentbyId(String id);
        void addAttachment(String noteId, Attachment attachment);
        boolean noteExists(Realm realm, String noteId);



    }

    interface FolderRepository{
        List<Folder> getAllFolders();
        Folder getFolderById(String id);
        Folder createNewFolder();
        void updatedFolderTitle(String id, String title);
        void deleteFolder(String folderId);
    }

    interface TagRepository{
        List<Tag> getAllTags();
        Tag getTagById(String id);
        Tag createNewTag();
        void updatedTagTitle(String id, String title);
        void deleteTag(String tagId);
    }


}

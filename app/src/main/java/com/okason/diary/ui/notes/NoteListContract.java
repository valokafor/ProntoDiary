package com.okason.diary.ui.notes;

import android.content.Context;

import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;

import java.util.List;

/**
 * Created by Valentine on 4/15/2017.
 */

public interface NoteListContract {
    interface View{
        void onDataSetChanged();
        void showNotes(List<Note> notes);
        void showEmptyText(boolean showText);
        void showDeleteConfirmation(Note note);
        void setProgressIndicator(boolean active);
        Context getContext();
    }

    interface Actions{
        void loadNotes(boolean forceUpdate);
        void deleteNote(Note note);



    }

    interface Repository{
        List<Note> getAllNotes();
        int getNotePosition(String noteId);
        Note getNoteById(String noteId);
        Note createNewNote();
        void updatedNoteTitle(String noteId, String title);
        void updatedNoteContent(String noteId, String content);
        void setFolder(String folderId, String noteId);
        void deleteNote(String noteId);
        void saveNote(Note note);
        Attachment getAttachmentbyId(String id);


    }
}

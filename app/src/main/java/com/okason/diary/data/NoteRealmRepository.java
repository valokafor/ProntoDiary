package com.okason.diary.data;

import com.google.firebase.database.DatabaseReference;
import com.okason.diary.core.events.ItemDeletedEvent;
import com.okason.diary.core.events.OnAttachmentAddedToNoteEvent;
import com.okason.diary.core.events.UpdateTagLayoutEvent;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;
import com.okason.diary.ui.addnote.AddNoteContract;
import com.okason.diary.ui.auth.UserManager;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by valokafor on 6/22/17.
 */

public class NoteRealmRepository implements AddNoteContract.Repository {
    private DatabaseReference mDatabase;
    private DatabaseReference noteCloudReference;
    private DatabaseReference storageRecordCloudReference;


    @Override
    public List<Note> getAllNotes() {
        List<Note> viewModels = new ArrayList<>();
        try(Realm realm = Realm.getInstance(UserManager.getConfig())) {
            RealmResults<Note> notes = realm.where(Note.class).findAll();
            if (notes != null && notes.size() > 0){
                viewModels = realm.copyFromRealm(notes);
            }
        }
        return viewModels;
    }

    @Override
    public List<Tag> getAllTags() {
        List<Tag> viewModels = new ArrayList<>();
        try(Realm realm = Realm.getInstance(UserManager.getConfig())) {
            RealmResults<Tag> tags = realm.where(Tag.class).findAll();
            if (tags != null && tags.size() > 0){
                viewModels = realm.copyFromRealm(tags);
            }
        }
        return viewModels;
    }



    @Override
    public int getNotePosition(String noteId) {
        List<Note> noteList = getAllNotes();
        for (int i = 0; i < noteList.size(); i++){
            if (noteList.get(i).getId().equals(noteId)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public Note getNoteById(String noteId) {
        Note selectedNote;
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            selectedNote = realm.where(Note.class).equalTo("id", noteId).findFirst();
            selectedNote = realm.copyFromRealm(selectedNote);
        }catch (Exception e){
            selectedNote = null;
        }
        return selectedNote;
    }

    @Override
    public Note createNewNote() {
        String noteId = UUID.randomUUID().toString();
        Note note;
        try(Realm realm = Realm.getInstance(UserManager.getConfig())) {
            realm.beginTransaction();
            note = realm.createObject(Note.class, noteId);
            note.setDateCreated(System.currentTimeMillis());
            note.setDateModified(System.currentTimeMillis());
            realm.commitTransaction();
            note = realm.copyFromRealm(note);
        }
        return note;
    }

    @Override
    public void updatedNoteTitle(final String noteId, final String title) {
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            realm.beginTransaction();
            Note selectedNote = realm.where(Note.class).equalTo("id", noteId).findFirst();
            if (selectedNote != null){
                selectedNote.setTitle(title);
                selectedNote.setDateModified(System.currentTimeMillis());
            }
            realm.commitTransaction();
        }

    }

    @Override
    public void updatedNoteContent(final String noteId, final String content) {
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            realm.beginTransaction();
            Note selectedNote = realm.where(Note.class).equalTo("id", noteId).findFirst();
            if (selectedNote != null){
                selectedNote.setContent(content);
                selectedNote.setDateModified(System.currentTimeMillis());
            }
            realm.commitTransaction();
        }

    }

    @Override
    public void setFolder(final String folderId, final String noteId) {
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    Folder selectedFolder = backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();

                    if (selectedNote  != null && selectedFolder != null){
                        //Set folder
                        selectedNote.setFolder(selectedFolder);

                        //Check to see if Folder already contains the Note
                        if (!selectedFolder.getNotes().contains(selectedNote)){
                            selectedFolder.getNotes().add(selectedNote);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void addTag(final String noteId, final String tagId) {
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    Tag selectedTag = backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst();

                    if (noteContainsTag(selectedNote, selectedTag)){
                        return;
                    }

                    if (selectedNote != null && selectedTag != null){
                        //Add Tag to Note
                        selectedNote.getTags().add(selectedTag);
                        selectedTag.getNotes().add(selectedNote);
                        List<Tag> tags = backgroundRealm.copyFromRealm(selectedNote).getTags();
                        EventBus.getDefault().post(new UpdateTagLayoutEvent(tags));
                    }

                }
            });

        }

    }

    //Preventds adding duplicate tag to Note
    private boolean noteContainsTag(Note selectedNote, Tag selectedTag) {
        List<Tag> tags = selectedNote.getTags();
        for (Tag t: tags){
            if (t.getId().equals(selectedTag.getId())){
                return true;
            }
        }
        return false;
    }


    @Override
    public void removeTag(final String noteId, final String tagId) {
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Note selectedNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    Tag selectedTag = backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst();


                    if (selectedNote != null && selectedTag != null){
                        //Remove Tag from Note
                        RealmList<Tag> tagsInNote = selectedNote.getTags();
                        for (int i = 0; i<tagsInNote.size(); i++){
                            Tag tempTag = selectedNote.getTags().get(i);
                            if (tempTag.getId().equals(tagId)){
                                selectedNote.getTags().remove(i);
                                break;
                            }
                        }
                        //Now Remove Note Id from Tag List of Note Ids
                        for (int i = 0; i<selectedTag.getNotes().size(); i++){
                            String selectedNoteId = selectedTag.getNotes().get(i).getId();
                            if (selectedNoteId.equals(noteId)){
                                selectedTag.getNotes().remove(i);
                                break;
                            }
                        }

                        List<Tag> tags = backgroundRealm.copyFromRealm(selectedNote).getTags();
                        EventBus.getDefault().post(new UpdateTagLayoutEvent(tags));

                    }

                }
            });

        }
        //Remove this tag from the list of Tags for the Note

    }

    @Override
    public void deleteNote(final String noteId) {
        String result;
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst().deleteFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    EventBus.getDefault().post(new ItemDeletedEvent(Constants.RESULT_OK));
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    EventBus.getDefault().post(new ItemDeletedEvent(error.getLocalizedMessage()));
                }
            });
        }

    }

    @Override
    public void saveNote(Note note) {

    }

    @Override
    public Attachment getAttachmentbyId(String id) {
        Attachment attachment;
        try (Realm realm = Realm.getInstance(UserManager.getConfig())){
            attachment = realm.where(Attachment.class).equalTo("id", id).findFirst();
        }catch (Exception e){
            attachment = null;
        }
        return attachment;
    }

    @Override
    public void addAttachment(final String noteId, final Attachment attachment) {
        final String attachmentId = UUID.randomUUID().toString();

        try (final Realm realm = Realm.getInstance(UserManager.getConfig())){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    final Attachment savedAttachment = backgroundRealm.createObject(Attachment.class, attachmentId);
                    savedAttachment.update(attachment);

                    Note parentNote = backgroundRealm.where(Note.class).equalTo("id", noteId).findFirst();
                    parentNote.getAttachments().add(savedAttachment);
                    EventBus.getDefault().post(new OnAttachmentAddedToNoteEvent(backgroundRealm.copyFromRealm(parentNote), attachmentId));

                }
            });

        }
    }

   // private void uploadFileToFirebase(Data)

    @Override
    public boolean noteExists(Realm realm, String noteId) {
        Note selectedNote;
        selectedNote = realm.where(Note.class).equalTo("id", noteId).findFirst();
        return selectedNote != null;
    }


}

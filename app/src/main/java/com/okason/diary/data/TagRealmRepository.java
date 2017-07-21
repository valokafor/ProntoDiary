package com.okason.diary.data;

import com.okason.diary.models.Note;
import com.okason.diary.models.Tag;
import com.okason.diary.ui.addnote.AddNoteContract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by valokafor on 6/22/17.
 */

public class TagRealmRepository implements AddNoteContract.TagRepository {
    @Override
    public List<Tag> getAllTags() {
        List<Tag> viewModels = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Tag> tags = realm.where(Tag.class).findAll();
            if (tags != null && tags.size() > 0){
                viewModels = realm.copyFromRealm(tags);
            }
        }
        return viewModels;
    }

    @Override
    public Tag getTagById(String id) {
        Tag selectedTag;
        try (Realm realm = Realm.getDefaultInstance()){
            selectedTag = realm.where(Tag.class).equalTo("id", id).findFirst();
            selectedTag = realm.copyFromRealm(selectedTag);
        }catch (Exception e){
            selectedTag = null;
        }
        return selectedTag;
    }

    @Override
    public Tag createNewTag() {
        String id = UUID.randomUUID().toString();
        Tag tag;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            tag = realm.createObject(Tag.class, id);
            tag.setDateCreated(System.currentTimeMillis());
            tag.setDateModified(System.currentTimeMillis());
            realm.commitTransaction();
            tag = realm.copyFromRealm(tag);
        }
        return tag;
    }

    public Tag getTagByName(String tagName) {
        Tag selectedTag;
        try (Realm realm = Realm.getDefaultInstance()){
            selectedTag = realm.where(Tag.class).equalTo("tagName", tagName).findFirst();
            selectedTag = realm.copyFromRealm(selectedTag);
        }catch (Exception e){
            selectedTag = null;
        }
        return selectedTag;
    }

    @Override
    public Tag getOrCreateTag(String tagName) {
        Tag tag;
        tag = getTagByName(tagName);
        if (tag == null){
            tag = createNewTag();
        }
        return tag;
    }

    @Override
    public void updatedTagTitle(final String id, final String title) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Tag selectedTag = backgroundRealm.where(Tag.class).equalTo("id", id).findFirst();
                    if (selectedTag != null) {
                        selectedTag.setTagName(title);
                        selectedTag.setDateModified(System.currentTimeMillis());
                    }
                }
            });
        }

    }

    @Override
    public void deleteTag(final String tagId) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst().deleteFromRealm();
                }
            });
        }
    }

    @Override
    public List<Note> getNotesForTag(String tagName) {
        List<Note> notes = new ArrayList<>();
        try (Realm realm = Realm.getDefaultInstance()){
            Tag selectedTag = realm.where(Tag.class).equalTo("tagName", tagName).findFirst();
            if (selectedTag != null) {
                RealmList<Note> savedNotes = selectedTag.getNotes();
                if (savedNotes != null && savedNotes.size() > 0){
                    for (Note note: savedNotes){
                        notes.add(realm.copyFromRealm(note));
                    }
                }
            }

        }
        return notes;
    }
}

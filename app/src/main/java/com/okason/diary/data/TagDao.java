package com.okason.diary.data;

import com.okason.diary.models.Tag;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 5/20/18.
 */

public class TagDao {

    private Realm realm;

    public TagDao(Realm realm) {
        this.realm = realm;
    }


    public RealmResults<Tag> getAllTags() {
        RealmResults<Tag> tags = realm.where(Tag.class).findAll();
        return tags;
    }


    public Tag getTagById(String tagId) {
        try {
            Tag selectedTag = realm.where(Tag.class).equalTo("id", tagId).findFirst();
            return selectedTag;
        } catch (Exception e) {
            return null;
        }
    }


    public Tag createNewTag() {
        String id = UUID.randomUUID().toString();
        realm.beginTransaction();
        Tag tag = realm.createObject(Tag.class, id);
        tag.setDateCreated(System.currentTimeMillis());
        tag.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return tag;
    }

    public Tag getTagByName(String tagName) {
        try {
            Tag selectedTag = realm.where(Tag.class).equalTo("tagName", tagName).findFirst();
            return selectedTag;
        } catch (Exception e) {
            return null;
        }
    }


    public Tag getOrCreateTag(String tagName) {
        Tag tag = getTagById(tagName);
        if (tag == null){
            tag = createNewTag();
        }
        return tag;
    }


    public void updatedTagTitle(final String id, final String title) {
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


    public void deleteTag(final String tagId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(Tag.class).equalTo("id", tagId).findFirst().deleteFromRealm();
            }
        });
    }
}







package com.okason.diary.data;

import com.okason.diary.models.realmentities.TagEntity;

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


    public RealmResults<TagEntity> getAllTags() {
        RealmResults<TagEntity> tags = realm.where(TagEntity.class).findAll();
        return tags;
    }


    public TagEntity getTagById(String tagId) {
        try {
            TagEntity selectedTag = realm.where(TagEntity.class).equalTo("id", tagId).findFirst();
            return selectedTag;
        } catch (Exception e) {
            return null;
        }
    }


    public TagEntity createNewTag() {
        String id = UUID.randomUUID().toString();
        realm.beginTransaction();
        TagEntity tag = realm.createObject(TagEntity.class, id);
        tag.setDateCreated(System.currentTimeMillis());
        tag.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return tag;
    }

    public TagEntity getTagByName(String tagName) {
        try {
            TagEntity selectedTag = realm.where(TagEntity.class).equalTo("tagName", tagName).findFirst();
            return selectedTag;
        } catch (Exception e) {
            return null;
        }
    }


    public TagEntity getOrCreateTag(String tagName) {
        TagEntity tag = getTagById(tagName);
        if (tag == null){
            tag = createNewTag();
        }
        return tag;
    }


    public void updatedTagTitle(final String id, final String title) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                TagEntity selectedTag = backgroundRealm.where(TagEntity.class).equalTo("id", id).findFirst();
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
                backgroundRealm.where(TagEntity.class).equalTo("id", tagId).findFirst().deleteFromRealm();
            }
        });
    }
}







package com.okason.diary.data;

import com.okason.diary.models.ProntoTag;

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


    public RealmResults<ProntoTag> getAllTags() {
        RealmResults<ProntoTag> prontoTags = realm.where(ProntoTag.class).findAll();
        return prontoTags;
    }


    public ProntoTag getTagById(String tagId) {
        try {
            ProntoTag selectedProntoTag = realm.where(ProntoTag.class).equalTo("id", tagId).findFirst();
            return selectedProntoTag;
        } catch (Exception e) {
            return null;
        }
    }


    public ProntoTag createNewTag() {
        String id = UUID.randomUUID().toString();
        realm.beginTransaction();
        ProntoTag prontoTag = realm.createObject(ProntoTag.class, id);
        prontoTag.setDateCreated(System.currentTimeMillis());
        prontoTag.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return prontoTag;
    }

    public ProntoTag getTagByName(String tagName) {
        try {
            ProntoTag selectedProntoTag = realm.where(ProntoTag.class).equalTo("tagName", tagName).findFirst();
            return selectedProntoTag;
        } catch (Exception e) {
            return null;
        }
    }


    public ProntoTag getOrCreateTag(String tagName) {
        ProntoTag prontoTag = getTagById(tagName);
        if (prontoTag == null){
            prontoTag = createNewTag();
        }
        return prontoTag;
    }


    public void updatedTagTitle(final String id, final String title) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                ProntoTag selectedProntoTag = backgroundRealm.where(ProntoTag.class).equalTo("id", id).findFirst();
                if (selectedProntoTag != null) {
                    selectedProntoTag.setTagName(title);
                    selectedProntoTag.setDateModified(System.currentTimeMillis());
                }
            }
        });

    }


    public void deleteTag(final String tagId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(ProntoTag.class).equalTo("id", tagId).findFirst().deleteFromRealm();
            }
        });
    }
}







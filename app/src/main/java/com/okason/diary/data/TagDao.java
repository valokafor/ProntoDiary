package com.okason.diary.data;

import android.content.Intent;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.services.DataUploadIntentService;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.dto.ProntoTagDto;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

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


    public ProntoTag createNewTag(String tagName) {
        String id = UUID.randomUUID().toString();
        realm.beginTransaction();
        ProntoTag tag = realm.createObject(ProntoTag.class, id);
        tag.setDateCreated(System.currentTimeMillis());
        tag.setDateModified(System.currentTimeMillis());
        tag.setTagName(tagName);
        realm.commitTransaction();
        return tag;
    }

    public ProntoTag getTagByName(String tagName) {
        try {
            ProntoTag selectedTag = realm.where(ProntoTag.class).equalTo("tagName", tagName).findFirst();
            return selectedTag;
        } catch (Exception e) {
            return null;
        }
    }


    public ProntoTag getOrCreateTag(String tagName) {
        ProntoTag tag = getTagByName(tagName);
        if (tag == null){
            tag = createNewTag(tagName);
        }
        return tag;
    }


    public void updatedTagTitle(final String id, final String title) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                ProntoTag selectedProntoTag = backgroundRealm.where(ProntoTag.class).equalTo("id", id).findFirst();
                if (selectedProntoTag != null) {
                    selectedProntoTag.setTagName(title);
                    selectedProntoTag.setDateModified(System.currentTimeMillis());


                    //Send Intent to update Firestore data
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.TAG_ID, id);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            }
        });

    }


    public void deleteTag(final String tagId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(ProntoTag.class).equalTo("id", tagId).findFirst().deleteFromRealm();

                Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                intent.putExtra(Constants.DELETE_EVENT, true);
                intent.putExtra(Constants.DELETE_EVENT_TYPE, Constants.TAGS);
                intent.putExtra(Constants.ITEM_ID, tagId);
                DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
            }
        });
    }

    public void addTagFromCloud(ProntoTagDto tagDto) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ProntoTag tag = realm.where(ProntoTag.class).equalTo("id", tagDto.getId()).findFirst();

                if (tag != null && !TimeUtils.isCloudModifiedDateAfterLocalModifiedDate(tagDto.getDateModified(),
                        tag.getDateModified())){
                    return;
                }


                if (tag == null) {
                    String tagId = UUID.randomUUID().toString();
                    tag = realm.createObject(ProntoTag.class, tagId);
                }

                tag.setDateCreated(tagDto.getDateCreated());
                tag.setDateModified(tagDto.getDateModified());
                tag.setTagName(tagDto.getTagName());

                if (tagDto.getTaskIds().size() > 0){
                    for (String taskId: tagDto.getTaskIds()){
                        ProntoTask task = new TaskDao(realm).getTaskById(taskId);
                        if (task != null){
                            tag.getTasks().add(task);
                        }
                    }
                }

                if (tagDto.getJournalIds().size() > 0){
                    for (String journalId: tagDto.getJournalIds()){
                        Journal note = new JournalDao(realm).getJournalById(journalId);
                        if (note != null){
                            tag.getJournals().add(note);
                        }
                    }
                }

            }
        });

    }


    public ProntoTag createOrUpdateTag(String tagName) {

        String id = UUID.randomUUID().toString();
        realm.beginTransaction();
        ProntoTag prontoTag = new ProntoTag();
        prontoTag.setId(id);
        prontoTag.setDateCreated(System.currentTimeMillis());
        prontoTag.setDateModified(System.currentTimeMillis());
        prontoTag.setTagName(tagName);
        realm.insert(prontoTag);
        realm.commitTransaction();
        return prontoTag;

    }
}







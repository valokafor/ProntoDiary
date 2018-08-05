package com.okason.diary.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.okason.diary.BuildConfig;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.AttachingFileCompleteEvent;
import com.okason.diary.core.services.DataUploadIntentService;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.models.dto.AttachmentDto;
import com.okason.diary.models.dto.FolderDto;
import com.okason.diary.models.dto.JournalDto;
import com.okason.diary.models.dto.ProntoTagDto;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.StorageHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 4/28/18.
 */

public class JournalDao {
    private Realm realm;
    private final static String TAG = "JournalDao";

    public JournalDao(Realm realm) {
        this.realm = realm;
    }

    public RealmResults<Journal> getAllNotes(String tagName) {
        RealmResults<Journal> journals;
        if (TextUtils.isEmpty(tagName)) {
            journals = realm.where(Journal.class).findAll();
        } else {
            journals = realm.where(Journal.class).equalTo("tags.tagName", tagName).findAll();
        }

        for (Journal journal : journals){
            if (journal != null && TextUtils.isEmpty(journal.getContent())
                    && TextUtils.isEmpty(journal.getTitle())){
                deleteJournal(journal.getId());
            }
        }
        return journals;
    }



    public Journal getJournalById(String journalId) {
        try {
            Journal selectedJournal = realm.where(Journal.class).equalTo("id", journalId).findFirst();
            return selectedJournal;
        } catch (Exception e) {
            return null;
        }
    }

    public Journal copyOrUpdate(Journal journal){
        try {
            realm.beginTransaction();
            journal = realm.copyToRealmOrUpdate(journal);
            realm.commitTransaction();
        } catch (Exception exception) {
            realm.cancelTransaction();
            throw exception;
        }
        return journal;
    }


    public void deleteJournal(String journalId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Journal journal = backgroundRealm.where(Journal.class).equalTo("id", journalId).findFirst();
                if (journal != null){
                    journal.deleteFromRealm();

                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.DELETE_EVENT, true);
                    intent.putExtra(Constants.DELETE_EVENT_TYPE, Constants.JOURNALS);
                    intent.putExtra(Constants.ITEM_ID, journalId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            }
        });


    }

    public Journal createNewJournal() {
        String journalId = UUID.randomUUID().toString();
        realm.beginTransaction();
        Journal journal = realm.createObject(Journal.class, journalId);
        journal.setDateCreated(System.currentTimeMillis());
        journal.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return journal;
    }


    public void setFolder(String journalId, String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Journal journal = backgroundRealm.where(Journal.class).equalTo("id", journalId).findFirst();
                Folder folder = backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();
                journal.setFolder(folder);
                folder.getJournals().add(journal);
                journal.setDateModified(System.currentTimeMillis());
            }
        });
    }

    public void createNewAttachment(Uri attachmentUri, String filPath, String mimeType, String journalId) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                String attachmentId = UUID.randomUUID().toString();
                Attachment attachment = backgroundRealm.createObject(Attachment.class, attachmentId);
                attachment.setUri(attachmentUri.toString());
                attachment.setLocalFilePath(filPath);
                attachment.setMime_type(mimeType);

                Journal journal = backgroundRealm.where(Journal.class).equalTo("id", journalId).findFirst();
                if (journal != null && attachment != null){
                    journal.getAttachments().add(attachment);
                    journal.setDateModified(System.currentTimeMillis());
                }
            }
        });
    }

    public void updatedJournalContent(String journalId, String content, String title ) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Journal journal = backgroundRealm.where(Journal.class).equalTo("id", journalId).findFirst();
                if (journal != null){
                    journal.setTitle(title);
                    journal.setContent(content);
                    journal.setDateModified(System.currentTimeMillis());

                    //Send Intent to update Firestore data
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.JOURNAL_ID, journalId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            }
        });

    }

    public void createAttachmentFromUri(Context mContext, Uri uri, String journalId) {
        String name = FileHelper.getNameFromUri(mContext, uri);
        String extension = FileHelper.getFileExtension(FileHelper.getNameFromUri(mContext, uri)).toLowerCase(
                Locale.getDefault());
        File f = StorageHelper.createExternalStoragePrivateFile(mContext, uri, extension);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {

                if (f != null) {
                    String attachmentId = UUID.randomUUID().toString();
                    try {
                        Uri fileUri = FileProvider.getUriForFile(ProntoDiaryApplication.getAppContext(),
                                BuildConfig.APPLICATION_ID + ".provider",
                                f);
                        String filePath = f.getAbsolutePath();
                        String mimeType = StorageHelper.getMimeTypeInternal(mContext, uri);


                        Attachment attachment = backgroundRealm.createObject(Attachment.class, attachmentId);
                        attachment.setUri(fileUri.toString());
                        attachment.setLocalFilePath(filePath);
                        attachment.setMime_type(mimeType);
                        attachment.setName(name);
                        attachment.setSize(f.length());

                        Journal journal = backgroundRealm.where(Journal.class).equalTo("id", journalId).findFirst();
                        if (journal != null && attachment != null){
                            journal.getAttachments().add(attachment);
                            journal.setDateModified(System.currentTimeMillis());
                            EventBus.getDefault().post(new AttachingFileCompleteEvent(attachment.getId()));

                            Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                            intent.putExtra(Constants.JOURNAL_ID, journalId);
                            DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                        }
                    } catch (Exception e) {
                        Crashlytics.log(Log.DEBUG, TAG, "Attachment error: " + e.getLocalizedMessage());
                    }
                }
    ;
            }
        });

    }

    public void addTag(final String journalId, final String tagId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Journal selectedJournal = backgroundRealm.where(Journal.class).equalTo("id", journalId).findFirst();
                ProntoTag selectedProntoTag = backgroundRealm.where(ProntoTag.class).equalTo("id", tagId).findFirst();

                if (noteContainsTag(selectedJournal, selectedProntoTag)){
                    return;
                }

                if (selectedJournal != null && selectedProntoTag != null){
                    //Add ProntoTag to Journal
                    selectedJournal.getTags().add(selectedProntoTag);
                    selectedProntoTag.getJournals().add(selectedJournal);

                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.JOURNAL_ID, journalId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }

            }
        });

    }

    //Preventds adding duplicate tag to Journal
    private boolean noteContainsTag(Journal selectedJournal, ProntoTag selectedProntoTag) {
        List<ProntoTag> prontoTags = selectedJournal.getTags();
        for (ProntoTag t: prontoTags){
            if (t.getId().equals(selectedProntoTag.getId())){
                return true;
            }
        }
        return false;
    }

    public void removeTag(final String journalId, final String tagId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Journal selectedJournal = backgroundRealm.where(Journal.class).equalTo("id", journalId).findFirst();
                ProntoTag selectedProntoTag = backgroundRealm.where(ProntoTag.class).equalTo("id", tagId).findFirst();
                if (selectedJournal != null && selectedProntoTag != null) {
                    selectedJournal.getTags().remove(selectedProntoTag);
                    selectedProntoTag.getJournals().remove(selectedJournal);


                    //Update Firebase Journal Object
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.JOURNAL_ID, journalId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);

                    //Update Firebase Tag Object
                    Intent tagIntent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.TAG_ID, selectedProntoTag.getId());
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), tagIntent);
                }
            }
        });
    }

    public List<Journal> filterNotes(String query, String tagName) {
        List<Journal> journals = new ArrayList<>();
        for (Journal journal: getAllNotes(tagName)){
            String title = journal.getTitle().toLowerCase();
            String content = journal.getContent().toLowerCase();
            query = query.toLowerCase();
            if (title.contains(query) || content.contains(query)){
                journals.add(journal);
            }
        }
        return journals;
    }


    public Attachment getAttachmentById(String attachmentId) {
        try {
            Attachment attachment = realm.where(Attachment.class).equalTo("id", attachmentId).findFirst();
            return attachment;
        } catch (Exception e) {
            return null;
        }
    }

    public void updateAttachmentUrl(String attachmentId, String downloadUrl) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Attachment attachment = getAttachmentById(attachmentId);
                attachment.setCloudFilePath(downloadUrl);
            }
        });
    }

    public RealmResults<Journal> getNotesByFolder(String folderId) {
        RealmResults<Journal> journals = realm.where(Journal.class).equalTo("folder.id", folderId).findAll();
        return journals;
    }

    public void addJournalDtoToRealm(JournalDto dto) {

        if (dto != null && !TextUtils.isEmpty(dto.getId())) {

            //Create or Get Folder, this spuns a Realm Transaction of its own
            Folder folder = null;
            if (dto.getFolder() != null){
                FolderDto folderDto = dto.getFolder();
                folder= new FolderDao(realm).getOrCreateFolder(folderDto.getFolderName());
            }

            //Now create the Realm object

            Journal journal = getJournalById(dto.getId());
            if (journal == null) {
                realm.beginTransaction();
                journal = new Journal(dto);
                journal = realm.copyToRealmOrUpdate(journal);

                //Add Folder to Journal
                if (folder != null){
                    journal.setFolder(folder);
                    folder.getJournals().add(journal);
                }
                realm.commitTransaction();
            }


            //Check if Attachment exists
            if (dto.getAttachments().size() > 0){
                //Update Attachments in a transaction
                realm.beginTransaction();
                for (AttachmentDto attachmentDto: dto.getAttachments()){
                    if (attachmentDto.getId() != null) {
                        Attachment attachment = getAttachmentById(attachmentDto.getId());
                        if (attachment == null) {
                            attachment = new Attachment(attachmentDto);
                            attachment = realm.copyToRealmOrUpdate(attachment);
                        }
                        if (attachment != null) {
                            journal.getAttachments().add(attachment);
                        }
                    }
                }
                realm.commitTransaction();

            }


            //Update the list of applicable ProntoTags
            if (dto.getTags().size() > 0){
                TagDao tagDao = new TagDao(realm);
                for (ProntoTagDto tagDto: dto.getTags()){
                    ProntoTag tag = tagDao.getOrCreateTag(tagDto.getTagName());
                    if (tag != null){
                        addTag(journal.getId(), tag.getId());
                    }
                }
            }


        }

    }




}

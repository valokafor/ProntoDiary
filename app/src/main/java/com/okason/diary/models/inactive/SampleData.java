package com.okason.diary.models.inactive;

import android.content.Context;

import com.okason.diary.R;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.utils.Constants;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by vokafor on 1/18/2017.
 */

public class SampleData {
    public final static String FOLDER_NAME_1 = "Family";
    public final static String FOLDER_NAME_2 = "Work";
    public final static String FOLDER_NAME_3 = "Productivity";
    public final static String FOLDER_NAME_4 = "Finance";
    public final static String FOLDER_NAME_5 = "Health and Fitness";

    public final static String TAG_NAME_1 = "Funny";
    public final static String TAG_NAME_2 = "Interesting";
    public final static String TAG_NAME_3 = "Kids";
    public final static String TAG_NAME_4 = "Habit";
    public final static String TAG_NAME_5 = "Food";


    public final static String FOLDER_ID_1 = "e4ce6638-677f-11e8-adc0-fa7ae01bbebc";
    public final static String FOLDER_ID_2 = "e4ce68cc-677f-11e8-adc0-fa7ae01bbebc";
    public final static String FOLDER_ID_3 = "e4ce6a20-677f-11e8-adc0-fa7ae01bbebc";
    public final static String FOLDER_ID_4 = "e4ce6b42-677f-11e8-adc0-fa7ae01bbebc";
    public final static String FOLDER_ID_5 = "e4ce6c6e-677f-11e8-adc0-fa7ae01bbebc";

    public final static String TAG_ID_1 = "e4ce6d90-677f-11e8-adc0-fa7ae01bbebc";
    public final static String TAG_ID_2 = "e4ce6f7a-677f-11e8-adc0-fa7ae01bbebc";
    public final static String TAG_ID_3 = "e4ce7592-677f-11e8-adc0-fa7ae01bbebc";
    public final static String TAG_ID_4 = "e4ce77ea-677f-11e8-adc0-fa7ae01bbebc";
    public final static String TAG_ID_5 = "e4ce79a2-677f-11e8-adc0-fa7ae01bbebc";

    public final static String JOURNAL_ID_1 = "bee821f6-6780-11e8-adc0-fa7ae01bbebc";
    public final static String JOURNAL_ID_2 = "bee8250c-6780-11e8-adc0-fa7ae01bbebc";
    public final static String JOURNAL_ID_3 = "bee827dc-6780-11e8-adc0-fa7ae01bbebc";
    public final static String JOURNAL_ID_4 = "bee82a20-6780-11e8-adc0-fa7ae01bbebc";
    public final static String JOURNAL_ID_5 = "bee82d9a-6780-11e8-adc0-fa7ae01bbebc";




    private final Context context;

    public SampleData(Context context) {
        this.context = context;
    }






    public void getSampleNotesRealm() {


        try (Realm realm = Realm.getDefaultInstance()) {
            if (realm == null){
                return;
            }
            realm.beginTransaction();


            ProntoTag prontoTag1 = realm.createObject(ProntoTag.class, TAG_ID_1);
            prontoTag1.setDateCreated(System.currentTimeMillis());
            prontoTag1.setDateModified(System.currentTimeMillis());
            prontoTag1.setTagName(TAG_NAME_1);

            ProntoTag prontoTag2 = realm.createObject(ProntoTag.class, TAG_ID_2);
            prontoTag2.setDateCreated(System.currentTimeMillis());
            prontoTag2.setDateModified(System.currentTimeMillis());
            prontoTag2.setTagName(TAG_NAME_2);

            ProntoTag prontoTag3 = realm.createObject(ProntoTag.class, TAG_ID_3);
            prontoTag3.setDateCreated(System.currentTimeMillis());
            prontoTag3.setDateModified(System.currentTimeMillis());
            prontoTag3.setTagName(TAG_NAME_3);

            ProntoTag prontoTag4 = realm.createObject(ProntoTag.class, TAG_ID_4);
            prontoTag4.setDateCreated(System.currentTimeMillis());
            prontoTag4.setDateModified(System.currentTimeMillis());
            prontoTag4.setTagName(TAG_NAME_4);

            ProntoTag prontoTag5 = realm.createObject(ProntoTag.class, TAG_ID_5);
            prontoTag5.setDateCreated(System.currentTimeMillis());
            prontoTag5.setDateModified(System.currentTimeMillis());
            prontoTag5.setTagName(TAG_NAME_5);

            Folder folder1 = realm.createObject(Folder.class, FOLDER_ID_1);
            folder1.setDateCreated(System.currentTimeMillis());
            folder1.setDateModified(System.currentTimeMillis());
            folder1.setFolderName(FOLDER_NAME_1);

            Folder folder2 = realm.createObject(Folder.class, FOLDER_ID_2);
            folder2.setDateCreated(System.currentTimeMillis());
            folder2.setDateModified(System.currentTimeMillis());
            folder2.setFolderName(FOLDER_NAME_2);

            Folder folder3 = realm.createObject(Folder.class, FOLDER_ID_3);
            folder1.setDateCreated(System.currentTimeMillis());
            folder1.setDateModified(System.currentTimeMillis());
            folder1.setFolderName(FOLDER_NAME_3);

            Folder folder4 = realm.createObject(Folder.class, FOLDER_ID_4);
            folder1.setDateCreated(System.currentTimeMillis());
            folder1.setDateModified(System.currentTimeMillis());
            folder1.setFolderName(FOLDER_NAME_4);

            Folder folder5 = realm.createObject(Folder.class, FOLDER_ID_5);
            folder1.setDateCreated(System.currentTimeMillis());
            folder1.setDateModified(System.currentTimeMillis());
            folder1.setFolderName(FOLDER_NAME_5);


            Journal journal1 = realm.createObject(Journal.class, JOURNAL_ID_1);
            journal1.setTitle("DisneyLand Trip");
            journal1.setContent(context.getString(R.string.sample_journal_1));
            Calendar calendar1 = GregorianCalendar.getInstance();
            journal1.setDateModified(calendar1.getTimeInMillis());
            calendar1.add(Calendar.DAY_OF_WEEK, -2);
            calendar1.add(Calendar.MILLISECOND, 430433435);
            journal1.setDateCreated(calendar1.getTimeInMillis());


            journal1.getTags().add(prontoTag1);
            journal1.getTags().add(prontoTag2);
            journal1.getTags().add(prontoTag3);

            prontoTag1.getJournals().add(journal1);
            prontoTag2.getJournals().add(journal1);
            prontoTag3.getJournals().add(journal1);

            journal1.setFolder(folder2);
            folder2.getJournals().add(journal1);


            Attachment attachment1 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment1.setCloudFilePath("https://4.img-dpreview.com/files/p/E~TS590x0~articles/3925134721/0266554465.jpeg");
            attachment1.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment1);

            Attachment attachment2 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment2.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Fentrepreneur.jpg?alt=media&token=e4955940-abd5-4835-b992-62e8057c158a");
            attachment2.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment2);

            Attachment attachment3 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment3.setCloudFilePath("http://cdn.iphonehacks.com/wp-content/uploads/2011/10/iphone-4s-camera-1.jpg");
            attachment3.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment3);

            Attachment attachment4 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment4.setCloudFilePath("https://randomuser.me/api/portraits/men/59.jpg");
            attachment4.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment4);



            Journal journal2 = realm.createObject(Journal.class, JOURNAL_ID_2);
            journal2.setTitle("European Language");
            journal2.setContent(context.getString(R.string.sample_journal_2));

            //change the date to random time
            Calendar calendar2 = GregorianCalendar.getInstance();
            calendar2.add(Calendar.DAY_OF_WEEK, -12);
            calendar2.add(Calendar.MILLISECOND, 430430344);
            journal2.setDateCreated(calendar2.getTimeInMillis());

            calendar2 = GregorianCalendar.getInstance();
            calendar2.add(Calendar.DAY_OF_WEEK, -7);
            calendar2.add(Calendar.MILLISECOND, 10005623);
            journal2.setDateModified(calendar2.getTimeInMillis());


            journal2.getTags().add(prontoTag4);
            journal2.getTags().add(prontoTag5);

            prontoTag4.getJournals().add(journal2);
            prontoTag5.getJournals().add(journal2);
            journal2.setFolder(folder1);
            folder1.getJournals().add(journal2);


            Attachment attachment6 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment6.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Fcivilization.jpg?alt=media&token=e4efe700-68c9-424e-9a67-11b633c77f31");
            attachment6.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal2.getAttachments().add(attachment6);

            Attachment attachment7 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment7.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Fbusiness.jpg?alt=media&token=96725118-b505-4df4-afd2-43cdc8468d64");
            attachment7.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal2.getAttachments().add(attachment7);

            Attachment attachment8 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment8.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Farchitecture.jpg?alt=media&token=6aaf2500-09b7-43f6-b5f6-a68935addc6d");
            attachment8.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal2.getAttachments().add(attachment8);

            Attachment attachment5 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment5.setCloudFilePath("https://youtu.be/htONbnz_wL4");
            attachment5.setMime_type(Constants.MIME_TYPE_VIDEO);
            journal2.getAttachments().add(attachment5);


            Journal journal3 = realm.createObject(Journal.class, JOURNAL_ID_3);
            journal3.setTitle("Enduring Tradition");
            journal3.setContent(context.getString(R.string.sample_journal_3));

            //change the date to random time
            Calendar calendar3 = GregorianCalendar.getInstance();
            calendar3.add(Calendar.WEEK_OF_MONTH, -8);
            calendar3.add(Calendar.MILLISECOND, 454546454);
            journal3.setDateCreated(calendar3.getTimeInMillis());

            calendar3 = GregorianCalendar.getInstance();
            calendar3.add(Calendar.WEEK_OF_MONTH, -3);
            calendar3.add(Calendar.MILLISECOND, 534356534);
            journal3.setDateModified(calendar3.getTimeInMillis());


            journal3.getTags().add(prontoTag4);
            journal3.getTags().add(prontoTag5);

            prontoTag4.getJournals().add(journal3);
            prontoTag5.getJournals().add(journal3);
            journal3.setFolder(folder1);
            folder1.getJournals().add(journal3);

            Attachment attachment9 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment9.setCloudFilePath("http://s1.1zoom.me/big0/557/Dubai_Emirates_UAE_442993.jpg");
            attachment9.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal3.getAttachments().add(attachment9);


            Attachment attachment10 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment10.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontodiary-bee92.appspot.com/o/sample_audio_file.mp3?alt=media&token=4f658e12-d848-452c-812a-408083532b8d");
            attachment10.setMime_type(Constants.MIME_TYPE_AUDIO);
            journal3.getAttachments().add(attachment10);



            realm.commitTransaction();

        }
    }

    public void removeSampleData(){
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Journal sampleJournal1 = backgroundRealm.where(Journal.class).equalTo("id", JOURNAL_ID_1).findFirst();
                    if (sampleJournal1 != null){
                        for (Attachment attachment: sampleJournal1.getAttachments()){
                            attachment.deleteFromRealm();
                        }
                        sampleJournal1.deleteFromRealm();
                    }

                    Journal sampleJournal2 = backgroundRealm.where(Journal.class).equalTo("id", JOURNAL_ID_2).findFirst();
                    if (sampleJournal2 != null){
                        for (Attachment attachment: sampleJournal2.getAttachments()){
                            attachment.deleteFromRealm();
                        }
                        sampleJournal2.deleteFromRealm();
                    }

                    Journal sampleJournal3 = backgroundRealm.where(Journal.class).equalTo("id", JOURNAL_ID_3).findFirst();
                    if (sampleJournal3 != null){
                        for (Attachment attachment: sampleJournal3.getAttachments()){
                            attachment.deleteFromRealm();
                        }
                        sampleJournal3.deleteFromRealm();
                    }

                    Journal sampleJournal4 = backgroundRealm.where(Journal.class).equalTo("id", JOURNAL_ID_4).findFirst();
                    if (sampleJournal4 != null){
                        for (Attachment attachment: sampleJournal4.getAttachments()){
                            attachment.deleteFromRealm();
                        }
                        sampleJournal4.deleteFromRealm();
                    }

                    Journal sampleJournal5 = backgroundRealm.where(Journal.class).equalTo("id", JOURNAL_ID_5).findFirst();
                    if (sampleJournal5 != null){
                        for (Attachment attachment: sampleJournal5.getAttachments()){
                            attachment.deleteFromRealm();
                        }
                        sampleJournal5.deleteFromRealm();
                    }


                    Folder sampleFolder1 = backgroundRealm.where(Folder.class).equalTo("id", FOLDER_ID_1).findFirst();
                    if (sampleFolder1 != null){
                        sampleFolder1.deleteFromRealm();
                    }

                    Folder sampleFolder2 = backgroundRealm.where(Folder.class).equalTo("id", FOLDER_ID_2).findFirst();
                    if (sampleFolder2 != null){
                        sampleFolder2.deleteFromRealm();
                    }

                    Folder sampleFolder3 = backgroundRealm.where(Folder.class).equalTo("id", FOLDER_ID_3).findFirst();
                    if (sampleFolder3 != null){
                        sampleFolder3.deleteFromRealm();
                    }

                    Folder sampleFolder4 = backgroundRealm.where(Folder.class).equalTo("id", FOLDER_ID_4).findFirst();
                    if (sampleFolder4 != null){
                        sampleFolder4.deleteFromRealm();
                    }

                    Folder sampleFolder5 = backgroundRealm.where(Folder.class).equalTo("id", FOLDER_ID_5).findFirst();
                    if (sampleFolder5 != null){
                        sampleFolder5.deleteFromRealm();
                    }

                    ProntoTag sampleTag1 = backgroundRealm.where(ProntoTag.class).equalTo("id", TAG_ID_1).findFirst();
                    if (sampleTag1 != null){
                        sampleTag1.deleteFromRealm();
                    }

                    ProntoTag sampleTag2 = backgroundRealm.where(ProntoTag.class).equalTo("id", TAG_ID_2).findFirst();
                    if (sampleTag2 != null){
                        sampleTag2.deleteFromRealm();
                    }

                    ProntoTag sampleTag3 = backgroundRealm.where(ProntoTag.class).equalTo("id", TAG_ID_3).findFirst();
                    if (sampleTag3 != null){
                        sampleTag3.deleteFromRealm();
                    }

                    ProntoTag sampleTag4 = backgroundRealm.where(ProntoTag.class).equalTo("id", TAG_ID_4).findFirst();
                    if (sampleTag4 != null){
                        sampleTag4.deleteFromRealm();
                    }

                    ProntoTag sampleTag5 = backgroundRealm.where(ProntoTag.class).equalTo("id", TAG_ID_5).findFirst();
                    if (sampleTag5 != null){
                        sampleTag5.deleteFromRealm();
                    }
                }
            });
        }
    }


}

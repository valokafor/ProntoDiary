package com.okason.diary.models;

import android.content.Context;

import com.okason.diary.R;
import com.okason.diary.models.realmentities.AttachmentEntity;
import com.okason.diary.models.realmentities.NoteEntity;
import com.okason.diary.models.realmentities.TagEntity;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by vokafor on 1/18/2017.
 */

public class SampleData {
    private final Context context;

    public SampleData(Context context) {
        this.context = context;
    }

    public static List<String> getSampleCategories() {
        List<String> folderNames = new ArrayList<>();

        folderNames.add("Family");
        folderNames.add("Work");
        folderNames.add("Productivity");
        folderNames.add("Finance");
        folderNames.add("Fitness");
        return folderNames;
    }


    public static List<String> getSampleTags() {
        List<String> tagNames = new ArrayList<>();

        tagNames.add("Urgent");
        tagNames.add("Funny");
        tagNames.add("Important");
        tagNames.add("Maybe");
        tagNames.add("Interesting");
        return tagNames;
    }




    public void getSampleNotesRealm() {


        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            NoteEntity note1 = realm.createObject(NoteEntity.class, UUID.randomUUID().toString());
            note1.setTitle("DisneyLand Trip");
            note1.setContent(context.getString(R.string.sample_text_disneyland));
            Calendar calendar1 = GregorianCalendar.getInstance();
            note1.setDateModified(calendar1.getTimeInMillis());

            TagEntity tagEntity1 = realm.createObject(TagEntity.class, UUID.randomUUID().toString());
            tagEntity1.setTagName("Social");

            TagEntity tagEntity2 = realm.createObject(TagEntity.class, UUID.randomUUID().toString());
            tagEntity2.setTagName("Funny");

            TagEntity tagEntity3 = realm.createObject(TagEntity.class, UUID.randomUUID().toString());
            tagEntity3.setTagName("Work");

            note1.getTags().add(tagEntity1);
            note1.getTags().add(tagEntity2);
            note1.getTags().add(tagEntity3);

            AttachmentEntity attachment1 = realm.createObject(AttachmentEntity.class, UUID.randomUUID().toString());
            attachment1.setCloudFilePath("https://randomuser.me/api/portraits/women/83.jpg");
            attachment1.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment1);

            AttachmentEntity attachment2 = realm.createObject(AttachmentEntity.class, UUID.randomUUID().toString());
            attachment2.setCloudFilePath("https://randomuser.me/api/portraits/women/45.jpg");
            attachment2.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment2);

            AttachmentEntity attachment3 = realm.createObject(AttachmentEntity.class, UUID.randomUUID().toString());
            attachment3.setCloudFilePath("https://randomuser.me/api/portraits/men/82.jpg");
            attachment3.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment3);

            AttachmentEntity attachment4 = realm.createObject(AttachmentEntity.class, UUID.randomUUID().toString());
            attachment4.setCloudFilePath("https://randomuser.me/api/portraits/men/59.jpg");
            attachment4.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment4);



            NoteEntity note2 = realm.createObject(NoteEntity.class, UUID.randomUUID().toString());
            note2.setTitle("Gym Work Out");
            note2.setContent("I went to the Gym today and I got a lot of exercises");

            //change the date to random time
            Calendar calendar2 = GregorianCalendar.getInstance();
            calendar2.add(Calendar.DAY_OF_WEEK, -1);
            calendar2.add(Calendar.MILLISECOND, 10005623);
            note2.setDateModified(calendar2.getTimeInMillis());

            TagEntity tagEntity4 = realm.createObject(TagEntity.class, UUID.randomUUID().toString());
            tagEntity4.setTagName("Friends");

            TagEntity tagEntity5 = realm.createObject(TagEntity.class, UUID.randomUUID().toString());
            tagEntity5.setTagName("Family");

            TagEntity tagEntity6 = realm.createObject(TagEntity.class, UUID.randomUUID().toString());
            tagEntity6.setTagName("Kids");

            note2.getTags().add(tagEntity4);
            note2.getTags().add(tagEntity5);
            note2.getTags().add(tagEntity6);



            realm.commitTransaction();

        }
    }

    private List<AttachmentEntity> getSampleAttachments() {
        List<AttachmentEntity> attachments = new ArrayList<>();



        return attachments;
    }
}

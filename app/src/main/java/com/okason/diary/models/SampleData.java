package com.okason.diary.models;

import android.content.Context;

import com.okason.diary.R;
import com.okason.diary.models.realmentities.Attachment;
import com.okason.diary.models.realmentities.Note;
import com.okason.diary.models.realmentities.Tag;
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
            Note note1 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note1.setTitle("DisneyLand Trip");
            note1.setContent(context.getString(R.string.sample_text_disneyland));
            Calendar calendar1 = GregorianCalendar.getInstance();
            note1.setDateModified(calendar1.getTimeInMillis());

            Tag tag1 = realm.createObject(Tag.class, UUID.randomUUID().toString());
            tag1.setTagName("Social");

            Tag tag2 = realm.createObject(Tag.class, UUID.randomUUID().toString());
            tag2.setTagName("Funny");

            Tag tag3 = realm.createObject(Tag.class, UUID.randomUUID().toString());
            tag3.setTagName("Work");

            note1.getTags().add(tag1);
            note1.getTags().add(tag2);
            note1.getTags().add(tag3);

            Attachment attachment1 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment1.setCloudFilePath("https://randomuser.me/api/portraits/women/83.jpg");
            attachment1.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment1);

            Attachment attachment2 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment2.setCloudFilePath("https://randomuser.me/api/portraits/women/45.jpg");
            attachment2.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment2);

            Attachment attachment3 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment3.setCloudFilePath("https://randomuser.me/api/portraits/men/82.jpg");
            attachment3.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment3);

            Attachment attachment4 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment4.setCloudFilePath("https://randomuser.me/api/portraits/men/59.jpg");
            attachment4.setMime_type(Constants.MIME_TYPE_IMAGE);
            note1.getAttachments().add(attachment4);



            Note note2 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note2.setTitle("Gym Work Out");
            note2.setContent("I went to the Gym today and I got a lot of exercises");

            //change the date to random time
            Calendar calendar2 = GregorianCalendar.getInstance();
            calendar2.add(Calendar.DAY_OF_WEEK, -1);
            calendar2.add(Calendar.MILLISECOND, 10005623);
            note2.setDateModified(calendar2.getTimeInMillis());

            Tag tag4 = realm.createObject(Tag.class, UUID.randomUUID().toString());
            tag4.setTagName("Friends");

            Tag tag5 = realm.createObject(Tag.class, UUID.randomUUID().toString());
            tag5.setTagName("Family");

            Tag tag6 = realm.createObject(Tag.class, UUID.randomUUID().toString());
            tag6.setTagName("Kids");

            note2.getTags().add(tag4);
            note2.getTags().add(tag5);
            note2.getTags().add(tag6);



            realm.commitTransaction();

        }
    }

    private List<Attachment> getSampleAttachments() {
        List<Attachment> attachments = new ArrayList<>();



        return attachments;
    }
}

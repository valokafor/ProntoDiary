package com.okason.diary.models;

import android.content.Context;

import com.okason.diary.R;
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
            Journal journal1 = realm.createObject(Journal.class, UUID.randomUUID().toString());
            journal1.setTitle("DisneyLand Trip");
            journal1.setContent(context.getString(R.string.sample_text_disneyland));
            Calendar calendar1 = GregorianCalendar.getInstance();
            journal1.setDateModified(calendar1.getTimeInMillis());

            ProntoTag prontoTag1 = realm.createObject(ProntoTag.class, UUID.randomUUID().toString());
            prontoTag1.setTagName("Social");

            ProntoTag prontoTag2 = realm.createObject(ProntoTag.class, UUID.randomUUID().toString());
            prontoTag2.setTagName("Funny");

            ProntoTag prontoTag3 = realm.createObject(ProntoTag.class, UUID.randomUUID().toString());
            prontoTag3.setTagName("Work");

            journal1.getProntoTags().add(prontoTag1);
            journal1.getProntoTags().add(prontoTag2);
            journal1.getProntoTags().add(prontoTag3);

            Attachment attachment1 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment1.setCloudFilePath("https://randomuser.me/api/portraits/women/83.jpg");
            attachment1.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment1);

            Attachment attachment2 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment2.setCloudFilePath("https://randomuser.me/api/portraits/women/45.jpg");
            attachment2.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment2);

            Attachment attachment3 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment3.setCloudFilePath("https://randomuser.me/api/portraits/men/82.jpg");
            attachment3.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment3);

            Attachment attachment4 = realm.createObject(Attachment.class, UUID.randomUUID().toString());
            attachment4.setCloudFilePath("https://randomuser.me/api/portraits/men/59.jpg");
            attachment4.setMime_type(Constants.MIME_TYPE_IMAGE);
            journal1.getAttachments().add(attachment4);



            Journal journal2 = realm.createObject(Journal.class, UUID.randomUUID().toString());
            journal2.setTitle("Gym Work Out");
            journal2.setContent("I went to the Gym today and I got a lot of exercises");

            //change the date to random time
            Calendar calendar2 = GregorianCalendar.getInstance();
            calendar2.add(Calendar.DAY_OF_WEEK, -1);
            calendar2.add(Calendar.MILLISECOND, 10005623);
            journal2.setDateModified(calendar2.getTimeInMillis());

            ProntoTag prontoTag4 = realm.createObject(ProntoTag.class, UUID.randomUUID().toString());
            prontoTag4.setTagName("Friends");

            ProntoTag prontoTag5 = realm.createObject(ProntoTag.class, UUID.randomUUID().toString());
            prontoTag5.setTagName("Family");

            ProntoTag prontoTag6 = realm.createObject(ProntoTag.class, UUID.randomUUID().toString());
            prontoTag6.setTagName("Kids");

            journal2.getProntoTags().add(prontoTag4);
            journal2.getProntoTags().add(prontoTag5);
            journal2.getProntoTags().add(prontoTag6);



            realm.commitTransaction();

        }
    }

    private List<Attachment> getSampleAttachments() {
        List<Attachment> attachments = new ArrayList<>();



        return attachments;
    }
}

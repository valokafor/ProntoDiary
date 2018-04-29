package com.okason.diary.models;

import com.okason.diary.models.realmentities.NoteEntity;
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


    public static List<Journal> getSampleNotes() {

        List<Journal> journals = new ArrayList<>();

        Journal journal1 = new Journal();
        journal1.setTitle("Sample Journal with Image");
        journal1.setContent("We went to Disneyland today and the kids had lots of fun!");
        Calendar calendar1 = GregorianCalendar.getInstance();
        journal1.setDateModified(calendar1.getTimeInMillis());
        Attachment attachment = new Attachment();
        attachment.setMime_type(Constants.MIME_TYPE_IMAGE);
        attachment.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff" +
                ".appspot.com/o/category_images%2Fbusiness_1.jpg?alt=media&token=80699be7-d2e1-4000-81b0-9730946d1915");
        journal1.getAttachments().add(attachment);
        journals.add(journal1);


        //create the dummy note
        Journal journal2 = new Journal();
        journal2.setTitle("Sample Journal with Video");
        journal2.setContent("I went to the Gym today and I got a lot of exercises. Please delete this sample journal entry");

        //change the date to random time
        Calendar calendar2 = GregorianCalendar.getInstance();
        calendar2.add(Calendar.DAY_OF_WEEK, -1);
        calendar2.add(Calendar.MILLISECOND, 10005623);
        journal2.setDateModified(calendar2.getTimeInMillis());
        Attachment attachment1 = new Attachment();
        attachment1.setMime_type(Constants.MIME_TYPE_VIDEO);
        attachment1.setCloudFilePath("http://techslides.com/demos/sample-videos/small.mp4");
        journal2.getAttachments().add(attachment1);
        journals.add(journal2);


        //create the dummy note
        Journal journal3 = new Journal();
        journal3.setTitle("Sample Journal with Multiple Images");
        journal3.setContent("I will like to write a blog post about how to make money online. Please delete this sample journal entry");


        //change the date to random time
        Calendar calendar3 = GregorianCalendar.getInstance();
        calendar3.add(Calendar.DAY_OF_WEEK, -2);
        calendar3.add(Calendar.MILLISECOND, 8962422);
        journal3.setDateModified(calendar3.getTimeInMillis());
        Attachment attachment2 = new Attachment();
        attachment2.setMime_type(Constants.MIME_TYPE_IMAGE);
        attachment2.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot." +
                "com/o/category_images%2Ffitness.jpg?alt=media&token=99fb2aa3-49e7-4e46-aaf3-5b2a42a9dea5");
        journal3.getAttachments().add(attachment2);
        Attachment attachment6 = new Attachment();
        attachment6.setMime_type(Constants.MIME_TYPE_IMAGE);
        attachment6.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Fwisdom.jpg?" +
                "alt=media&token=f61796d4-a268-4bfa-89ec-22af937bb5bb");
        journal3.getAttachments().add(attachment6);
        Attachment attachment7 = new Attachment();
        attachment7.setMime_type(Constants.MIME_TYPE_IMAGE);
        attachment7.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Ftechnology.jpg?" +
                "alt=media&token=3ed5a259-2d98-4d10-a1ff-d1547c72088f");
        journal3.getAttachments().add(attachment7);
        journals.add(journal3);


        //create the dummy note
        Journal journal4 = new Journal();
        journal4.setTitle("Sample Journal with File Attachment");
        journal4.setContent("Today I found a recipe to make cup cake from www.google. Please delete this sample journal entry");

        //pad the date with random number of days and minute
        //so all the journals do not have the same time stamp
        Calendar calendar4 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.DAY_OF_WEEK, -4);
        calendar4.add(Calendar.MILLISECOND, 49762311);
        journal4.setDateModified(calendar4.getTimeInMillis());
        Attachment attachment3 = new Attachment();
        attachment3.setMime_type(Constants.MIME_TYPE_FILES);
        attachment3.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Fpdf-sample.pdf?" +
                "alt=media&token=e16daf5b-c172-4823-a917-348a3a68edb8");
        journal4.getAttachments().add(attachment3);
        //journals.add(journal4);


        //create the dummy note
        Journal journal5 = new Journal();
        journal5.setTitle("Notes from Networking Event");
        journal5.setContent("Today I attended a developer's networking event and it was great");

        //pad the date with two days
        //pad the date with random number of days and minute
        //so all the journals do not have the same time stamp
        Calendar calendar5 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.MONTH, -2);
        calendar5.add(Calendar.MILLISECOND, 2351689);
        journal5.setDateModified(calendar5.getTimeInMillis());
        Attachment attachment4 = new Attachment();
        attachment4.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot" +
                ".com/o/category_images%2Flove_1.jpg?alt=media&token=7e869f5b-530e-495f-b976-470576280f1f");
        journal5.getAttachments().add(attachment4);
       // journals.add(journal5);

        return journals;
    }


    public static void getSampleNotesRealm() {


        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            NoteEntity note1 = realm.createObject(NoteEntity.class, UUID.randomUUID().toString());
            note1.setTitle("DisneyLand Trip");
            note1.setContent("We went to Disneyland today and the kids had lots of fun!");
            Calendar calendar1 = GregorianCalendar.getInstance();
            note1.setDateModified(calendar1.getTimeInMillis());


            NoteEntity note2 = realm.createObject(NoteEntity.class, UUID.randomUUID().toString());
            note2.setTitle("Gym Work Out");
            note2.setContent("I went to the Gym today and I got a lot of exercises");

            //change the date to random time
            Calendar calendar2 = GregorianCalendar.getInstance();
            calendar2.add(Calendar.DAY_OF_WEEK, -1);
            calendar2.add(Calendar.MILLISECOND, 10005623);
            note2.setDateModified(calendar2.getTimeInMillis());


            realm.commitTransaction();

        }
    }
}

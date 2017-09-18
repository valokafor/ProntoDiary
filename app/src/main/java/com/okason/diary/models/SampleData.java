package com.okason.diary.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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


    public static List<Note> getSampleNotes() {

        List<Note> notes = new ArrayList<>();

        Note note1 = new Note();
        note1.setTitle("DisneyLand Trip");
        note1.setContent("We went to Disneyland today and the kids had lots of fun!");
        Calendar calendar1 = GregorianCalendar.getInstance();
        note1.setDateModified(calendar1.getTimeInMillis());
        Attachment attachment = new Attachment();
        attachment.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff" +
                ".appspot.com/o/category_images%2Fbusiness_1.jpg?alt=media&token=80699be7-d2e1-4000-81b0-9730946d1915");
        note1.getAttachments().add(attachment);
        notes.add(note1);


        //create the dummy note
        Note note2 = new Note();
        note2.setTitle("Gym Work Out");
        note2.setContent("I went to the Gym today and I got a lot of exercises");

        //change the date to random time
        Calendar calendar2 = GregorianCalendar.getInstance();
        calendar2.add(Calendar.DAY_OF_WEEK, -1);
        calendar2.add(Calendar.MILLISECOND, 10005623);
        note2.setDateModified(calendar2.getTimeInMillis());
        Attachment attachment1 = new Attachment();
        attachment1.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot." +
                "com/o/category_images%2Fbusiness_2.jpg?alt=media&token=b09c3341-06b1-4bc9-97e9-678a5a94b0a8");
        note2.getAttachments().add(attachment1);
        notes.add(note2);


        //create the dummy note
        Note note3 = new Note();
        note3.setTitle("Blog Post Idea");
        note3.setContent("I will like to write a blog post about how to make money online");


        //change the date to random time
        Calendar calendar3 = GregorianCalendar.getInstance();
        calendar3.add(Calendar.DAY_OF_WEEK, -2);
        calendar3.add(Calendar.MILLISECOND, 8962422);
        note3.setDateModified(calendar3.getTimeInMillis());
        Attachment attachment2 = new Attachment();
        attachment2.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot." +
                "com/o/category_images%2Ffitness.jpg?alt=media&token=99fb2aa3-49e7-4e46-aaf3-5b2a42a9dea5");
        note3.getAttachments().add(attachment2);
        notes.add(note3);


        //create the dummy note
        Note note4 = new Note();
        note4.setTitle("Cupcake Recipe");
        note4.setContent("Today I found a recipe to make cup cake from www.google.");

        //pad the date with random number of days and minute
        //so all the notes do not have the same time stamp
        Calendar calendar4 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.DAY_OF_WEEK, -4);
        calendar4.add(Calendar.MILLISECOND, 49762311);
        note4.setDateModified(calendar4.getTimeInMillis());
        Attachment attachment3 = new Attachment();
        attachment3.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff" +
                ".appspot.com/o/category_images%2Ffriendship_2.jpg?alt=media&token=a2f270b8-9c42-4d6c-8a6e-0e6430868ce6");
        note4.getAttachments().add(attachment3);
        notes.add(note4);


        //create the dummy note
        Note note5 = new Note();
        note5.setTitle("Notes from Networking Event");
        note5.setContent("Today I attended a developer's networking event and it was great");

        //pad the date with two days
        //pad the date with random number of days and minute
        //so all the notes do not have the same time stamp
        Calendar calendar5 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.MONTH, -2);
        calendar5.add(Calendar.MILLISECOND, 2351689);
        note5.setDateModified(calendar5.getTimeInMillis());
        Attachment attachment4 = new Attachment();
        attachment4.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot" +
                ".com/o/category_images%2Flove_1.jpg?alt=media&token=7e869f5b-530e-495f-b976-470576280f1f");
        note5.getAttachments().add(attachment4);
        notes.add(note5);

        return notes;
    }
}

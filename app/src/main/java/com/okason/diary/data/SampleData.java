package com.okason.diary.data;

import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.addnote.AddNoteContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by Valentine on 4/26/2017.
 */

public class SampleData {
    public static List<String> getSampleCategories() {
        List<String> categoryNames = new ArrayList<>();

        categoryNames.add("Family");
        categoryNames.add("Word");
        categoryNames.add("Productivity");
        categoryNames.add("Personal");
        categoryNames.add("Finance");
        categoryNames.add("Fitness");
        categoryNames.add("Blog Posts");
        categoryNames.add("Social Media");


        return categoryNames;

    }


    public static void addSampleNotes() {

        try (Realm realm = Realm.getDefaultInstance()){

            Random random = new Random();
            AddNoteContract.Repository repository = new NoteRealmRepository();

            List<Note> notes = new ArrayList<>();
            //create the dummy note
            realm.beginTransaction();
            Note note1 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note1.setTitle("Sample Note - DisneyLand Trip");
            note1.setContent("We went to Disneyland today and the kids had lots of fun!.Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean.");
            Calendar calendar1 = GregorianCalendar.getInstance();
            note1.setDateCreated(calendar1.getTimeInMillis());
            realm.commitTransaction();

            Attachment sampleAttachment1 = getAttachments().get(random.nextInt(getAttachments().size()));
            repository.addAttachment(note1.getId(), sampleAttachment1);
            String sampleFolderName1 = getSampleCategories().get(random.nextInt(getSampleCategories().size()));
            repository.setFolder(note1.getId(), sampleFolderName1 );


            //create the dummy note
            realm.beginTransaction();
            Note note2 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note2.setTitle("Sample Note - Gym Work Out");
            note2.setContent("I went to the Gym today and I got a lot of exercises. A small river named Duden flows by their place and supplies it with the necessary regelialia. It is a paradisematic country, in which roasted parts of sentences fly into your mouth. ");

            //change the date to random time
            Calendar calendar2 = GregorianCalendar.getInstance();
            calendar2.add(Calendar.DAY_OF_WEEK, -1);
            calendar2.add(Calendar.MILLISECOND, 10005623);
            note2.setDateCreated(calendar2.getTimeInMillis());
            realm.commitTransaction();

            Attachment sampleAttachment2 = getAttachments().get(random.nextInt(getAttachments().size()));
            repository.addAttachment(note2.getId(), sampleAttachment2);
            String sampleFolderName2 = getSampleCategories().get(random.nextInt(getSampleCategories().size()));
            repository.setFolder(note2.getId(), sampleFolderName2 );


            //create the dummy note
            realm.beginTransaction();
            Note note3 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note3.setTitle("Sample - Blog Post Idea");
            note3.setContent("You can delete, I will like to write a blog post about how to make money online. Even the all-powerful Pointing has no control about the blind texts it is an almost unorthographic life One day however a small line of blind text by the name of Lorem Ipsum decided to leave for the far World of Grammar.");


            //change the date to random time
            Calendar calendar3 = GregorianCalendar.getInstance();
            calendar3.add(Calendar.DAY_OF_WEEK, -2);
            calendar3.add(Calendar.MILLISECOND, 8962422);
            note3.setDateCreated(calendar3.getTimeInMillis());
            realm.commitTransaction();


            Attachment sampleAttachment3 = getAttachments().get(random.nextInt(getAttachments().size()));
            repository.addAttachment(note3.getId(), sampleAttachment3);
            String sampleFolderName3 = getSampleCategories().get(random.nextInt(getSampleCategories().size()));
            repository.setFolder(note3.getId(), sampleFolderName3 );


            //create the dummy note
            realm.beginTransaction();
            Note note4 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note4.setTitle("Delicious Cupcake Recipe");
            note4.setContent("You can delete, Today I found a recipe to make cup cake from www.google. he Big Oxmox advised her not to do so, because there were thousands of bad Commas, wild Question Marks and devious Semikoli, but the Little Blind Text didn’t listen. She packed her seven versalia, put her initial into the belt and made herself on the way.");

            //pad the date with random number of days and minute
            //so all the notes do not have the same time stamp
            Calendar calendar4 = GregorianCalendar.getInstance();
            calendar4.add(Calendar.DAY_OF_WEEK, -4);
            calendar4.add(Calendar.MILLISECOND, 49762311);
            note4.setDateCreated(calendar4.getTimeInMillis());
            realm.commitTransaction();

            Attachment sampleAttachment4 = getAttachments().get(random.nextInt(getAttachments().size()));
            repository.addAttachment(note4.getId(), sampleAttachment4);
            String sampleFolderName4 = getSampleCategories().get(random.nextInt(getSampleCategories().size()));
            repository.setFolder(note4.getId(), sampleFolderName4 );


            //create the dummy note
            realm.beginTransaction();
            Note note5 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note5.setTitle("Sample Notes from Networking Event");
            note5.setContent("You can delete, Today I attended a developer's networking event and it was great. When she reached the first hills of the Italic Mountains, she had a last view back on the skyline of her hometown Bookmarksgrove, the headline of Alphabet Village and the subline of her own road, the Line Lane. ");

            //pad the date with two days
            //pad the date with random number of days and minute
            //so all the notes do not have the same time stamp
            Calendar calendar5 = GregorianCalendar.getInstance();
            calendar4.add(Calendar.MONTH, -2);
            calendar5.add(Calendar.MILLISECOND, 2351689);
            note5.setDateCreated(calendar5.getTimeInMillis());
            realm.commitTransaction();

            Attachment sampleAttachment5 = getAttachments().get(random.nextInt(getAttachments().size()));
            repository.addAttachment(note5.getId(), sampleAttachment5);
            String sampleFolderName5 = getSampleCategories().get(random.nextInt(getSampleCategories().size()));
            repository.setFolder(note5.getId(), sampleFolderName5 );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static List<Attachment> getAttachments(){
        List<Attachment> attachments = new ArrayList<>();

        Attachment attachment1 = new Attachment();
        attachment1.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Ffreedom_1.jpg");
        attachments.add(attachment1);

        Attachment attachment2 = new Attachment();
        attachment2.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/sample%2Fsample.pdf");
        attachments.add(attachment2);

        Attachment attachment3 = new Attachment();
        attachment3.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/sample%2FSampleVideo_1280x720_5mb.mp4");
        attachments.add(attachment3);

        Attachment attachment4 = new Attachment();
        attachment4.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/sample%2FSampleVideo_1280x720_2mb.mp4");
        attachments.add(attachment4);

        Attachment attachment5 = new Attachment();
        attachment5.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Fhope_2.jpg");
        attachments.add(attachment5);

        Attachment attachment6 = new Attachment();
        attachment6.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Ffriendship_2.jpg");
        attachments.add(attachment6);

        Attachment attachment7 = new Attachment();
        attachment7.setCloudFilePath("https://firebasestorage.googleapis.com/v0/b/prontoquote-3e2ff.appspot.com/o/category_images%2Flife.jpg");
        attachments.add(attachment7);

        return attachments;
    }




}

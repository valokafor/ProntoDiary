package com.okason.diary.data;

import com.okason.diary.models.NoteRealmModel;
import com.okason.diary.models.Tag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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

    public static List<Tag> getSampleTag() {
        List<Tag> tags = new ArrayList<>();

        tags.add(new Tag("Funny"));
        tags.add(new Tag("Word"));
        tags.add(new Tag("Product"));
        tags.add(new Tag("Friend"));
        tags.add(new Tag("Finance"));
        tags.add(new Tag("Health"));
        tags.add(new Tag("Faith"));
        tags.add(new Tag("Twitter"));


        return tags;

    }


    public static void getSampleNotes() {


        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            NoteRealmModel note1 = realm.createObject(NoteRealmModel.class, UUID.randomUUID().toString());
            note1.setTitle("DisneyLand Trip");
            note1.setContent("We went to Disneyland today and the kids had lots of fun!");
            Calendar calendar1 = GregorianCalendar.getInstance();
            note1.setDateModified(calendar1.getTimeInMillis());


            NoteRealmModel note2 = realm.createObject(NoteRealmModel.class, UUID.randomUUID().toString());
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

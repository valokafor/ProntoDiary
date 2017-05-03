package com.okason.diary.data;

import com.okason.diary.models.viewModel.NoteViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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


    public static List<NoteViewModel> getSampleNotes() {

        List<NoteViewModel> notes = new ArrayList<>();
        //create the dummy note
        NoteViewModel note1 = new NoteViewModel();
        note1.setTitle("Sample NoteViewModel - DisneyLand Trip");
        note1.setContent("We went to Disneyland today and the kids had lots of fun!");
        Calendar calendar1 = GregorianCalendar.getInstance();
        note1.setDateModified(calendar1.getTimeInMillis());
        notes.add(note1);


        //create the dummy note
        NoteViewModel note2 = new NoteViewModel();
        note2.setTitle("Sample NoteViewModel - Gym Work Out");
        note2.setContent("I went to the Gym today and I got a lot of exercises");

        //change the date to random time
        Calendar calendar2 = GregorianCalendar.getInstance();
        calendar2.add(Calendar.DAY_OF_WEEK, -1);
        calendar2.add(Calendar.MILLISECOND, 10005623);
        note2.setDateModified(calendar2.getTimeInMillis());
        notes.add(note2);


        //create the dummy note
        NoteViewModel note3 = new NoteViewModel();
        note3.setTitle("Sample - Blog Post Idea");
        note3.setContent("You can delete, I will like to write a blog post about how to make money online");


        //change the date to random time
        Calendar calendar3 = GregorianCalendar.getInstance();
        calendar3.add(Calendar.DAY_OF_WEEK, -2);
        calendar3.add(Calendar.MILLISECOND, 8962422);
        note3.setDateModified(calendar3.getTimeInMillis());
        notes.add(note3);


        //create the dummy note
        NoteViewModel note4 = new NoteViewModel();
        note4.setTitle("Cupcake Recipe");
        note4.setContent("You can delete, Today I found a recipe to make cup cake from www.google.");

        //pad the date with random number of days and minute
        //so all the notes do not have the same time stamp
        Calendar calendar4 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.DAY_OF_WEEK, -4);
        calendar4.add(Calendar.MILLISECOND, 49762311);
        note4.setDateModified(calendar4.getTimeInMillis());
        notes.add(note4);


        //create the dummy note
        NoteViewModel note5 = new NoteViewModel();
        note5.setTitle("Sample Notes from Networking Event");
        note5.setContent("You can delete, Today I attended a developer's networking event and it was great");

        //pad the date with two days
        //pad the date with random number of days and minute
        //so all the notes do not have the same time stamp
        Calendar calendar5 = GregorianCalendar.getInstance();
        calendar4.add(Calendar.MONTH, -2);
        calendar5.add(Calendar.MILLISECOND, 2351689);
        note5.setDateModified(calendar5.getTimeInMillis());
        notes.add(note5);

        return notes;
    }
}

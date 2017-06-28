package com.okason.diary.data;

import com.okason.diary.models.Note;
import com.okason.diary.models.TodoList;

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


    public static List<TodoList> getSampleTodoListItems(){
        List<TodoList> sampleTodos = new ArrayList<TodoList>();

        TodoList todoList1 = new TodoList();
        todoList1.setTitle("Personal");
        sampleTodos.add(todoList1);

        TodoList todoList2 = new TodoList();
        todoList2.setTitle("Work Related");
        sampleTodos.add(todoList2);

        TodoList todoList3 = new TodoList();
        todoList3.setTitle("Kitchen Remodelling Project");
        sampleTodos.add(todoList3);

        return sampleTodos;
    }




    public static void getSampleNotes() {


        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            Note note1 = realm.createObject(Note.class, UUID.randomUUID().toString());
            note1.setTitle("DisneyLand Trip");
            note1.setContent("We went to Disneyland today and the kids had lots of fun!");
            Calendar calendar1 = GregorianCalendar.getInstance();
            note1.setDateModified(calendar1.getTimeInMillis());


            Note note2 = realm.createObject(Note.class, UUID.randomUUID().toString());
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

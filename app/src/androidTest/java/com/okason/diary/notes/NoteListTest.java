package com.okason.diary.notes;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;

import com.okason.diary.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

/**
 * Created by Valentine on 5/3/2017.
 */

public class NoteListTest {

    private static final int POSTION_TO_CLICK = 3;

    public void clickOnDeleteButtonShouldShowDeleteConfirmationDialog(){
        //First scroll to the position of the Note that needs to be deleted and click
        onView(ViewMatchers.withId(R.id.note_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(POSTION_TO_CLICK, click()));
    }


}

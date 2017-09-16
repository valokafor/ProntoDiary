package com.okason.diary.ui.todolist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okason.diary.R;
import com.okason.diary.models.Task;
import com.okason.diary.utils.Constants;

public class AddSubTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Only start the Add Sub Task Fragment if a valid Task object was passed in
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(Constants.SERIALIZED_TASK)) {
                String serializedTask = getIntent().getStringExtra(Constants.SERIALIZED_TASK);
                if (!TextUtils.isEmpty(serializedTask)){
                    Gson gson = new Gson();
                    Task task = gson.fromJson(serializedTask, new TypeToken<Task>(){}.getType());
                    if (task != null) {
                        AddSubTaskFragment fragment = AddSubTaskFragment.newInstance(serializedTask);
                        openFragment(fragment, task.getTitle());
                    } else {
                        finish();
                    }
                }

            } else {
                finish();
            }
        }
    }

    private void openFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(screenTitle)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                int count = getFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    finish();
                } else {
                    getFragmentManager().popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }



}

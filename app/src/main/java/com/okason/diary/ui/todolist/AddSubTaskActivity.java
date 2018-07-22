package com.okason.diary.ui.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.okason.diary.R;
import com.okason.diary.data.RealmManager;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.utils.Constants;

import io.realm.Realm;

public class AddSubTaskActivity extends AppCompatActivity {

    private Realm realm;
    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm = RealmManager.setUpRealm();
        taskDao = new TaskDao(realm);

        //Only start the Add Sub ProntoTask Fragment if a valid ProntoTask object was passed in
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(Constants.TASK_ID)) {
                String taskId = getIntent().getStringExtra(Constants.TASK_ID);
                if (!TextUtils.isEmpty(taskId)) {
                    ProntoTask prontoTask = taskDao.getTaskById(taskId);
                    if (prontoTask != null) {
                        AddSubTaskFragment fragment = AddSubTaskFragment.newInstance(taskId);
                        openFragment(fragment, prontoTask.getTitle());
                    } else {
                        finish();
                    }
                } else {
                    finish();
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
                    startActivity(new Intent(AddSubTaskActivity.this, TodoListActivity.class));
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
            startActivity(new Intent(AddSubTaskActivity.this, TodoListActivity.class));
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

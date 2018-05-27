package com.okason.diary.ui.todolist;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.okason.diary.R;
import com.okason.diary.data.TaskDao;

import io.realm.Realm;

public class TodoListActivity extends AppCompatActivity {

    private final static String TAG = "TodoListActivity";
    private ViewPager mViewPager;
    private TaskListPagerAdapter pagerAdapter;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private Realm realm;
    private TaskDao taskDao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        realm = Realm.getDefaultInstance();
        taskDao = new TaskDao(realm);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViewPager();
    }

    public void setupViewPager() {
        Log.d(TAG, "setupViewPager called");
        tabLayout = findViewById(R.id.sliding_tabs);
        pagerAdapter = new TaskListPagerAdapter(getSupportFragmentManager(), this, taskDao);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public void openFragment(Fragment fragment, String screenTitle){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(screenTitle)
                .commit();
        getSupportActionBar().setTitle(screenTitle);
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}

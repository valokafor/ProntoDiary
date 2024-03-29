package com.okason.diary.ui.todolist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.okason.diary.R;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.utils.Constants;

import io.realm.RealmResults;

/**
 * Created by valokafor on 12/28/17.
 */

public class TaskListPagerAdapter extends FragmentStatePagerAdapter {
    private final Context context;
    private final TaskDao taskDao;
    private RealmResults<ProntoTask> lowPriorityProntoTasks, mediumPriorityProntoTasks, highPriorityProntoTasks;

    public TaskListPagerAdapter(FragmentManager fm, Context context, TaskDao taskDao) {
        super(fm);
        this.context = context;
        this.taskDao = taskDao;
        lowPriorityProntoTasks = taskDao.getAllTasksForPriority(Constants.PRIORITY_LOW);
        mediumPriorityProntoTasks = taskDao.getAllTasksForPriority(Constants.PRIORITY_MEDIUM);
        highPriorityProntoTasks = taskDao.getAllTasksForPriority(Constants.PRIORITY_HIGH);

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            case 0:
                fragment = TaskListFragment.newInstance(Constants.PRIORITY_HIGH);
                break;
            case 1:
                fragment = TaskListFragment.newInstance(Constants.PRIORITY_MEDIUM);
                break;
            case 2:
                fragment = TaskListFragment.newInstance(Constants.PRIORITY_LOW);
                break;
            default:
                fragment = new TaskListFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        int size = 0
;        switch (position) {
            case 0:
                size =  highPriorityProntoTasks.size();
                title = context.getString(R.string.label_high_priority) + " (" + size + ")";
                break;
            case 1:
                size =  mediumPriorityProntoTasks.size();
                title = context.getString(R.string.label_medium_priority) + " (" + size + ")";
                break;
            case 2:
                size =  lowPriorityProntoTasks.size();
                title = context.getString(R.string.label_low_priotity) + " (" + size + ")";
                break;
            default:
                title = context.getString(R.string.label_high_priority);
        }
        return title;
    }
}

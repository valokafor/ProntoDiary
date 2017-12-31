package com.okason.diary.ui.todolist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.okason.diary.R;
import com.okason.diary.utils.Constants;

/**
 * Created by valokafor on 12/28/17.
 */

public class TaskListPagerAdapter extends FragmentStatePagerAdapter {
    private final Context context;

    public TaskListPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
        switch (position) {
            case 0:
                title = context.getString(R.string.label_high_priority);
                break;
            case 1:
                title = context.getString(R.string.label_medium_priority);
                break;
            case 2:
                title = context.getString(R.string.label_low_priotity);
                break;
            default:
                title = context.getString(R.string.label_high_priority);
        }
        return title;
    }
}

package com.okason.diary.ui.settings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.okason.diary.ui.tag.TagListFragment;


/**
 * Created by vokafor on 12/11/2016.
 */

public class ProfileViewPagerAdapter extends FragmentStatePagerAdapter {
    public ProfileViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;
        switch (position){
            case 0:
                selectedFragment = new ProfileFragment();
                break;
            case 1:
                selectedFragment = new TagListFragment();
                break;
//            case 2:
//                selectedFragment = new LocationListFragment();
//                break;
            default:
                selectedFragment = new TagListFragment();

        }
        return selectedFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Profile";
                break;
            case 1:
                title = "Tags";
                break;
//            case 2:
//                title = "Location";
//                break;

        }

        return title;
    }
}

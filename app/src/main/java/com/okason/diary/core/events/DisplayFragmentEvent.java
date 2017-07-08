package com.okason.diary.core.events;

import android.support.v4.app.Fragment;

/**
 * Created by valokafor on 7/7/17.
 */

public class DisplayFragmentEvent {
    private final Fragment fragment;
    private final String title;

    public DisplayFragmentEvent(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }
}

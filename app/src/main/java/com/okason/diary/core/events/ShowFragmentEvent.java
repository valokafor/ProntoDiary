package com.okason.diary.core.events;

/**
 * Created by Valentine on 4/22/2017.
 */

public class ShowFragmentEvent {
    private final String title;
    private final String tag;

    public ShowFragmentEvent(String title, String tag) {
        this.title = title;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }
}

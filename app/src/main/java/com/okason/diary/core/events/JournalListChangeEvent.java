package com.okason.diary.core.events;

import com.okason.diary.models.Journal;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class JournalListChangeEvent {
    private final List<Journal> journalList;

    public JournalListChangeEvent(List<Journal> journalList) {
        this.journalList = journalList;
    }

    public List<Journal> getJournalList() {
        return journalList;
    }
}

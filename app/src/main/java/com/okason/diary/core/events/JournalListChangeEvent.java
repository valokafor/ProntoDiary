package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.Note;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class JournalListChangeEvent {
    private final List<Note> journalList;

    public JournalListChangeEvent(List<Note> journalList) {
        this.journalList = journalList;
    }

    public List<Note> getJournalList() {
        return journalList;
    }
}

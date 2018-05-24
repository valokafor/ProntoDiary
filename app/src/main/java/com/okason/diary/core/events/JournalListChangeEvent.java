package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.NoteEntity;

import java.util.List;

/**
 * Created by valokafor on 10/13/17.
 */

public class JournalListChangeEvent {
    private final List<NoteEntity> journalList;

    public JournalListChangeEvent(List<NoteEntity> journalList) {
        this.journalList = journalList;
    }

    public List<NoteEntity> getJournalList() {
        return journalList;
    }
}

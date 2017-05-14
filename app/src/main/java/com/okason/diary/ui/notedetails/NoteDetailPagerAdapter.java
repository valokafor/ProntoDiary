package com.okason.diary.ui.notedetails;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.okason.diary.models.Note;
import com.okason.diary.ui.notes.NoteListContract;
import com.okason.diary.utils.date.TimeUtils;

import java.util.List;

/**
 * Created by Valentine on 5/12/2017.
 */

public class NoteDetailPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Note> noteList;
    private final String startNoteId;
    private final NoteListContract.Repository mRepository;


    public NoteDetailPagerAdapter(FragmentManager fm, String startNoteId, NoteListContract.Repository mRepository) {
        super(fm);
        this.startNoteId = startNoteId;
        this.mRepository = mRepository;
        this.noteList = this.mRepository.getAllNotes();
    }


    @Override
    public Fragment getItem(int position) {
        Note selectedNote = noteList.get(position);
        return NoteDetailFragment.newInstance(selectedNote.getId());
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Note selectedNote = noteList.get(position);
        return TimeUtils.getDueDate(selectedNote.getDateCreated());

    }
}

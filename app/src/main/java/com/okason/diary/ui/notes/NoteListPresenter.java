package com.okason.diary.ui.notes;

import android.os.Handler;
import android.os.Looper;

import com.okason.diary.data.SampleData;
import com.okason.diary.models.viewModel.NoteViewModel;

import java.util.List;

/**
 * Created by Valentine on 4/15/2017.
 */

public class NoteListPresenter implements NoteListContract.Actions{
    private final NoteListContract.View mView;
    private  NoteListContract.Repository mRepository;

    private boolean isDualScreen = false;

    public NoteListPresenter(NoteListContract.View mView) {
        this.mView = mView;
        this.mRepository = mRepository;
    }


    @Override
    public void loadNotes(boolean forceUpdate) {
        mView.setProgressIndicator(true);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                List<NoteViewModel> notes = SampleData.getSampleNotes();
                mView.setProgressIndicator(false);
                if (notes != null && notes.size() > 0){
                    mView.showEmptyText(false);
                    mView.showNotes(notes);
                }else {
                    mView.showEmptyText(true);
                }
            }
        }, 500);

    }

    @Override
    public void deleteNote(NoteViewModel note) {

    }
}

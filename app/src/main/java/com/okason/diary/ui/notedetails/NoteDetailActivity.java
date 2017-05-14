package com.okason.diary.ui.notedetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.okason.diary.R;
import com.okason.diary.core.events.EditNoteButtonClickedEvent;
import com.okason.diary.data.NoteRealmRepository;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.ui.notes.NoteListContract;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteDetailActivity extends AppCompatActivity {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private NoteListContract.Repository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRepository = new NoteRealmRepository();



        //Get the id of the Note that was clicked to start this Fragment
        if (getIntent() != null && getIntent().hasExtra(Constants.NOTE_ID)){
            String noteId = getIntent().getStringExtra(Constants.NOTE_ID);
            int position = mRepository.getNotePosition(noteId);
            setupViewPager(position, noteId);

        }else {
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditNoteButtonClickedEvent(EditNoteButtonClickedEvent event){
        Intent editNoteIntent = new Intent(NoteDetailActivity.this, AddNoteActivity.class);
        editNoteIntent.putExtra(Constants.NOTE_ID, event.getClickedNote().getId());
        startActivity(editNoteIntent);
    }

    private void setupViewPager(int positionOfSelectedNote, String startNoteId) {
        NoteDetailPagerAdapter adapter = new NoteDetailPagerAdapter(getSupportFragmentManager(), startNoteId, mRepository);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(positionOfSelectedNote, true);
    }


    /**
     * Creates an Intent that is used to start this Activity
     * @param context - this context
     * @param noteId - the id of the Note that will be show first
     * @return
     */
    public static Intent getStartIntent(final Context context, final String noteId) {
        Intent intent = new Intent(context, NoteDetailActivity.class);
        intent.putExtra(Constants.NOTE_ID, noteId);
        return intent;
    }


}

package com.okason.diary.ui.addnote;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.events.ItemDeletedEvent;
import com.okason.diary.models.Note;
import com.okason.diary.ui.folder.AddFolderDialogFragment;
import com.okason.diary.ui.folder.SelectFolderDialogFragment;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteEditorFragment extends Fragment implements
   AddNoteContract.View{

    private final static String LOG_TAG = "NoteEditorFragment";

    private View mRootView;
    private AddNoteContract.Action mPresenter;
    private SelectFolderDialogFragment selectFolderDialogFragment;
    private AddFolderDialogFragment addFolderDialogFragment;

    @BindView(R.id.edit_text_category)
    EditText mCategory;
    @BindView(R.id.edit_text_title) EditText mTitle;
    @BindView(R.id.edit_text_note) EditText mContent;
    @BindView(R.id.image_attachment)
    ImageView mImageAttachment;
    @BindView(R.id.sketch_attachment) ImageView mSketchAttachment;



    public NoteEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPassedInNote();
    }

    private String getPassedInNote() {
        if (getArguments() != null && getArguments().containsKey(Constants.NOTE_ID)){
            String noteId = getArguments().getString(Constants.NOTE_ID);
            return noteId;
        }
        return "";

    }

    public static NoteEditorFragment newInstance(String noteId){
        NoteEditorFragment fragment = new NoteEditorFragment();
        if (!TextUtils.isEmpty(noteId)){
            Bundle args = new Bundle();
            args.putString(Constants.NOTE_ID, noteId);
            fragment.setArguments(args);
        }

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_editor, container, false);
        ButterKnife.bind(this, mRootView);
        mPresenter = new AddNotePresenter(this);

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String title = mTitle.getText().toString();
                mPresenter.onTitleChange(title);

            }
        });

        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = mContent.getText().toString();
                mPresenter.onNoteContentChange(content);

            }
        });
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getPassedInNote())){
            mPresenter.getCurrentNote(getPassedInNote());
        }
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id){


        }
        return super.onOptionsItemSelected(item);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemDeletedEvent(ItemDeletedEvent event){
        if (event.isShouldUpdatedList()){
            //When a Note is Deleted, go to the Note List
            startActivity(new Intent(getActivity(), NoteListActivity.class));
        }
    }



    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }


    @Override
    public void populateNote(Note note) {

    }



}

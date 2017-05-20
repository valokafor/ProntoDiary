package com.okason.diary.ui.notedetails;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.events.ItemDeletedEvent;
import com.okason.diary.core.listeners.OnAttachmentClickedListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.attachment.AttachmentListAdapter;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDetailFragment extends Fragment implements NoteDetailContract.View{

    private View mRootView;
    @BindView(R.id.edit_text_title)
    EditText mTitle;

    @BindView(R.id.edit_text_note) EditText mContent;

    @BindView(R.id.edit_text_category) EditText mCategory;

    @BindView(R.id.image_attachment)
    ImageView mImageAttachment;

    @BindView(R.id.sketch_attachment) ImageView mSketchAttachment;

    @BindView(R.id.creation)
    TextView dateCreated;

    @BindView(R.id.last_modification) TextView dateModified;

    @BindView(R.id.timestap_layout)
    LinearLayout mTimeStampLayout;

    @BindView(R.id.attachment_list_recyclerview) RecyclerView attachmentRecyclerView;

    @BindView(R.id.attachment_container)
    FrameLayout attachmentContainer;

    private AttachmentListAdapter attachmentListAdapter;

    private NoteDetailContract.Action mPresenter;



    public NoteDetailFragment() {
        // Required empty public constructor
    }

    public static NoteDetailFragment newInstance(String noteId){
        NoteDetailFragment fragment = new NoteDetailFragment();

        Bundle args = new Bundle();
        args.putString(Constants.NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_editor, container, false);
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get the Note id that was passed in and use it to create the presenter
        String noteId = "";
        if (getArguments() != null && getArguments().containsKey(Constants.NOTE_ID)) {
            noteId = getArguments().getString(Constants.NOTE_ID);
        }
        mPresenter = new NoteDetailPresenter(this, noteId);


        //Disable Edittexts
        displayReadOnlyViews();

        //Show the timestamp footer layout in details
        mTimeStampLayout.setVisibility(View.VISIBLE);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        mPresenter.showNoteDetails();
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

    /**
     * This event will be fired when a Note is deleted
     * If deleted successfuly, go back to the Note List
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemDeletedEvent(ItemDeletedEvent event){
       if (event.getResult().equals(Constants.RESULT_OK)){
           startActivity(new Intent(getActivity(), NoteListActivity.class));
       }else {
           makeToast(event.getResult());
       }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_edit:
                mPresenter.onEditNoteClick();
                break;
            case R.id.action_delete:
                mPresenter.onDeleteNoteButtonClicked();
                break;
            case R.id.action_share:
                displayShareIntent();
                break;
            case R.id.action_play:
               // onPlayAudioButtonClicked();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void displayReadOnlyViews() {
        mCategory.setFocusable(false);
        mTitle.setFocusable(false);
        mContent.setFocusable(false);
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
    public void displayNote(Note note) {
        String categoryName = null;
        try {
            categoryName = note.getFolder().getFolderName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mCategory.setText(categoryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mContent.setText(note.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTitle.setText(note.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (note.getAttachments() != null && note.getAttachments().size() > 0){
            initViewAttachments(note.getAttachments());
        }

        String created = null;
        try {
            created = getString(R.string.creation) + " " + TimeUtils.getTimeAgo(note.getDateCreated());
        } catch (Exception e) {
            created = getString(R.string.creation) + " " + TimeUtils.getTimeAgo(System.currentTimeMillis());
        }
        String modified = null;
        try {
            modified = getString(R.string.last_update) + " "  + TimeUtils.getTimeAgo(note.getDateModified());
        } catch (Exception e) {
            modified = getString(R.string.last_update) + " "  + TimeUtils.getTimeAgo(System.currentTimeMillis());
        }
        dateCreated.setText(created);
        dateModified.setText(modified);

    }

    @Override
    public void showDeleteConfirmation(Note note) {
        final String titleOfNoteTobeDeleted = note.getTitle();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(getString(R.string.warning_are_you_sure));
        alertDialog.setCustomTitle(titleView);


        alertDialog.setMessage(getString(R.string.delete_prompt) + " " + titleOfNoteTobeDeleted + " ?");
        alertDialog.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteNote();
                displayPreviousActivity();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    @Override
    public void displayPreviousActivity() {

    }

    private void initViewAttachments(final List<Attachment> attachmentList){

        attachmentContainer.setVisibility(View.VISIBLE);
        attachmentRecyclerView = (RecyclerView) mRootView.findViewById(R.id.attachment_list_recyclerview);

        attachmentListAdapter = new AttachmentListAdapter(attachmentList, getActivity());
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        attachmentRecyclerView.setLayoutManager(layoutManager);
        attachmentRecyclerView.setHasFixedSize(true);

        attachmentListAdapter.setListener(new OnAttachmentClickedListener() {
            @Override
            public void onAttachmentClicked(Attachment clickedAttachment) {
                //If clicked Attachment is of type Document
                //Launch an Intent to show it, otherwise start Gallery Activity
                if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)){
                    //show file
                }else {
                    Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
                    galleryIntent.putExtra(Constants.NOTE_ID, mPresenter.getCurrentNoteId());
                    galleryIntent.putExtra(Constants.SELECTED_ID, clickedAttachment.getId());
                    startActivity(galleryIntent);
                }
            }
        });
        attachmentRecyclerView.setAdapter(attachmentListAdapter);

    }

    public void displayShareIntent() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mTitle.getText().toString());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, mContent.getText().toString());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }
}

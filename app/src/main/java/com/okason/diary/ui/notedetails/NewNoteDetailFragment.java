package com.okason.diary.ui.notedetails;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.data.NoteDao;
import com.okason.diary.models.realmentities.NoteEntity;
import com.okason.diary.models.realmentities.TagEntity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewNoteDetailFragment extends Fragment {

    //Todo - Make Category clickable
    //Todo - Make Tags clickable, and when clicked open Tag Activity and display the Notes that belong to that tag
    //Todo - Remove Audio entry if no audio
    //Todo - make note detail full screen if no attachement
    //Todo - open full screen dialog showing Note text only with close button and cancel
    //Todo - add more fonts

    private View mRootView;
    @BindView(R.id.image_view_top)
    ImageView topImageView;
    @BindView(R.id.text_view_title)
    TextView titleTextView;
    @BindView(R.id.text_view_date)
    TextView dateTextView;
    @BindView(R.id.text_view_folder)
    TextView folderTextView;
    @BindView(R.id.text_view_summary)
    TextView noteSummary;
    @BindView(R.id.linear_layout_date)
    LinearLayout dateLinearLayout;
    @BindView(R.id.linear_layout_folder)
    LinearLayout folderLinearLayout;
    @BindView(R.id.linear_layout_tags)
    LinearLayout tagsLinearLayout;
    @BindView(R.id.linear_layout_audio)
    LinearLayout audioLinearLayout;
    @BindView(R.id.linear_layout_attachment)
    LinearLayout attachmentLinearLayout;
    @BindView(R.id.image_view_thumbnail_1)
    ImageView imageViewThumbnail2;
    @BindView(R.id.image_view_thumbnail_2)
    ImageView imageViewThumbnail3;
    @BindView(R.id.image_view_thumbnail_3)
    ImageView imageViewThumbnail1;
    @BindView(R.id.linear_layout_click)
    LinearLayout clickLinearLayout;
    @BindView(R.id.divider_2) View divider_2;

    private String currentNoteId;
    private NoteEntity currentNote;
    private Realm realm;


    public NewNoteDetailFragment() {
        // Required empty public constructor
    }


    public static NewNoteDetailFragment newInstance(String noteId){
        NewNoteDetailFragment fragment = new NewNoteDetailFragment();
        if (!TextUtils.isEmpty(noteId)){
            Bundle args = new Bundle();
            args.putString(Constants.NOTE_ID, noteId);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currentNoteId = getPassedInNoteId();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_note_detail, container, false);
        ButterKnife.bind(this, mRootView);
        realm = Realm.getDefaultInstance();
        if (!TextUtils.isEmpty(currentNoteId)){
            currentNote = new NoteDao(realm).getNoteEntityById(currentNoteId);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentNote != null){
            populateScreen();
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
               // EventBus.getDefault().post(new EditNoteButtonClickedEvent(mCurrentNote.getId()));
                break;
            case R.id.action_delete:
               // showDeleteConfirmation(mCurrentNote);
                break;
            case R.id.action_share:
               // displayShareIntent(mCurrentNote);
                break;
            case R.id.action_play:
                // onPlayAudioButtonClicked();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void populateScreen() {
        if (currentNote != null){
            if (currentNote.getAttachments() != null && currentNote.getAttachments().size() > 0){

            }else {
                attachmentLinearLayout.setVisibility(View.GONE);
                noteSummary.setMaxLines(4);
                divider_2.setVisibility(View.GONE);
            }
        }

        String dateString = getDateString(currentNote.getDateCreated(), currentNote.getDateModified());
        dateTextView.setText(dateString);

        //Display Folder name and make that folder name clickable
        if (currentNote.getFolder() != null && !TextUtils.isEmpty(currentNote.getFolder().getFolderName())){
            folderTextView.setText(currentNote.getFolder().getFolderName());
        } else {
            folderTextView.setText(getString(R.string.general));
        }
        folderTextView.setPaintFlags(folderTextView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        folderTextView.setClickable(true);
        folderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to Category Detail screen
                makeToast("Not implemented yet");
            }
        });

        //Display Tag names and make each Tag name clickable
        if (currentNote.getTags() != null && currentNote.getTags().size() > 0){
            for (int i = 0; i < currentNote.getTags().size(); i++){
                TagEntity tag = currentNote.getTags().get(i);
                TextView textView = new TextView(getActivity());
                int viewId = textView.generateViewId();
                textView.setId(viewId);
                textView.setText("#" + tag.getTagName());
                textView.setPaintFlags(folderTextView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 15, 0);
                textView.setLayoutParams(params);


                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Go to Tag Detail
                        makeToast(tag.getTagName() + "Not implemented yet");
                    }
                });
                tagsLinearLayout.addView(textView);

            }

        }else {
            tagsLinearLayout.setVisibility(View.GONE);
        }

    }

    private String getDateString(long dateCreated, long dateModified) {
        String createDate = TimeUtils.getReadableDateWithoutTime(dateCreated);
        String modifiedDate = TimeUtils.getReadableDateWithoutTime(dateModified);
        String result = createDate + " (" + getString(R.string.last_modified) + ") " + modifiedDate;
        return result;
    }

    @Override
    public void onDestroy() {
        if (realm != null && !realm.isClosed()){
            realm.close();
        }
        super.onDestroy();
    }

    private String getPassedInNoteId() {
        if (getArguments() != null && getArguments().containsKey(Constants.NOTE_ID)){
            String noteId = getArguments().getString(Constants.NOTE_ID);
            return noteId;
        }
        return "";

    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }
}

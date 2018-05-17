package com.okason.diary.ui.notedetails;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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

import com.bumptech.glide.Glide;
import com.okason.diary.R;
import com.okason.diary.data.NoteDao;
import com.okason.diary.models.realmentities.AttachmentEntity;
import com.okason.diary.models.realmentities.NoteEntity;
import com.okason.diary.models.realmentities.TagEntity;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.IntentChecker;
import com.okason.diary.utils.date.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewNoteDetailFragment extends Fragment implements View.OnClickListener {

    //Todo - Make Category clickable
    //Todo - Make Tags clickable, and when clicked open Tag Activity and display the Notes that belong to that tag
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
    ImageView imageViewThumbnail1;
    @BindView(R.id.image_view_thumbnail_2)
    ImageView imageViewThumbnail2;
    @BindView(R.id.image_view_thumbnail_3)
    ImageView imageViewThumbnail3;
    @BindView(R.id.linear_layout_click)
    LinearLayout clickLinearLayout;
    @BindView(R.id.divider_2) View divider_2;

    @BindView(R.id.card_view_thumbnail_1)
    CardView cardViewContainer1;
    @BindView(R.id.card_view_thumbnail_2)
    CardView cardViewContainer2;
    @BindView(R.id.card_view_thumbnail_3)
    CardView cardViewContainer3;


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
                noteSummary.setMaxLines(4);
                showHideImageViews(currentNote.getAttachments());


            }else {
                attachmentLinearLayout.setVisibility(View.GONE);
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
        noteSummary.setText(currentNote.getContent());
        noteSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteDetailDialogFragment fragment = NoteDetailDialogFragment.newInstance(currentNote.getId());
                fragment.show(getActivity().getSupportFragmentManager(), "Dialog");
            }
        });


    }

    private void showHideImageViews(RealmList<AttachmentEntity> attachments) {
        switch (attachments.size()){
            case 1:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.GONE);
                cardViewContainer3.setVisibility(View.GONE);
                clickLinearLayout.setVisibility(View.GONE);
                displayImage(attachments.get(0).getCloudFilePath(), imageViewThumbnail1);
                imageViewThumbnail1.setOnClickListener(this);
                break;
            case 2:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.VISIBLE);
                cardViewContainer3.setVisibility(View.GONE);
                clickLinearLayout.setVisibility(View.GONE);
                displayImage(attachments.get(0).getCloudFilePath(), imageViewThumbnail1);
                displayImage(attachments.get(1).getCloudFilePath(), imageViewThumbnail2);
                imageViewThumbnail2.setOnClickListener(this);
                break;
            case 3:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.VISIBLE);
                cardViewContainer3.setVisibility(View.VISIBLE);
                clickLinearLayout.setVisibility(View.GONE);
                displayImage(attachments.get(0).getCloudFilePath(), imageViewThumbnail1);
                displayImage(attachments.get(1).getCloudFilePath(), imageViewThumbnail2);
                displayImage(attachments.get(2).getCloudFilePath(), imageViewThumbnail3);
                imageViewThumbnail3.setOnClickListener(this);
                break;
            default:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.VISIBLE);
                cardViewContainer3.setVisibility(View.VISIBLE);
                clickLinearLayout.setVisibility(View.VISIBLE);
                displayImage(attachments.get(0).getCloudFilePath(), imageViewThumbnail1);
                displayImage(attachments.get(1).getCloudFilePath(), imageViewThumbnail2);
                displayImage(attachments.get(2).getCloudFilePath(), imageViewThumbnail3);
                clickLinearLayout.setOnClickListener(this);
                break;


        }
    }

    private void displayImage(String imageUrl, ImageView imageViewThumbnail){
        Glide.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.default_image)
                .centerCrop()
                .into(imageViewThumbnail);

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

    @Override
    public void onClick(View view) {
        AttachmentEntity clickedAttachment = null;
        int selectedPosition = -1;
        switch (view.getId()){
            case R.id.image_view_thumbnail_1:
                clickedAttachment = currentNote.getAttachments().get(0);
                selectedPosition = 0;
                break;
            case R.id.image_view_thumbnail_2:
                clickedAttachment = currentNote.getAttachments().get(1);
                selectedPosition = 1;
                break;
            case R.id.image_view_thumbnail_3:
                clickedAttachment = currentNote.getAttachments().get(2);
                selectedPosition = 2;
                break;
            default:
                clickedAttachment = currentNote.getAttachments().get(0);
                selectedPosition = 0;
                break;

        }


        //If clicked Attachment is of type Document
        //Launch an Intent to show it, otherwise start Gallery Activity
        if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)){
            //show file
            Uri uri = Uri.parse(clickedAttachment.getUri());
            String fileType = "";
            String name = FileHelper.getNameFromUri(getActivity(), uri);
            String extension = FileHelper.getFileExtension(name).toLowerCase();

            if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")){
                fileType = "image/jpeg";
            }else if (extension.equals(".pdf")){
                fileType = "application/pdf";
            }else {
                fileType = "plain/text";
            }

            Intent fileViewIntent = new Intent(Intent.ACTION_VIEW);
            fileViewIntent.setDataAndType(uri, fileType);
            fileViewIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            fileViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent
                    .FLAG_GRANT_WRITE_URI_PERMISSION);
            if (IntentChecker.isAvailable(getActivity().getApplicationContext(), fileViewIntent, null)) {
                startActivity(Intent.createChooser(fileViewIntent, getResources().getText(R.string.open_with)));
            } else {
                makeToast(getString(R.string.feature_not_available_on_this_device));
            }

        }else {
            Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
            galleryIntent.putExtra(Constants.NOTE_ID, currentNote.getId());
            galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getFilePath());
            startActivity(galleryIntent);
        }
    }




}

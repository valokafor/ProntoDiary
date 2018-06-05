package com.okason.diary.ui.notedetails;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.okason.diary.BuildConfig;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.GlideApp;
import com.okason.diary.core.events.EditNoteButtonClickedEvent;
import com.okason.diary.data.JournalDao;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.ui.folder.FolderActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.FileUtility;
import com.okason.diary.utils.IntentChecker;
import com.okason.diary.utils.SettingsHelper;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDetailFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "NoteDetail";

    //Todo - add more fonts

    private View mRootView;
    @BindView(R.id.image_view_top) ImageView topImageView;
    @BindView(R.id.text_view_title) TextView titleTextView;
    @BindView(R.id.text_view_date) TextView dateTextView;
    @BindView(R.id.text_view_folder) TextView folderTextView;
    @BindView(R.id.text_view_summary) TextView noteSummary;
    @BindView(R.id.text_view_attachment_count) TextView imageCountTextView;
    @BindView(R.id.linear_layout_date) LinearLayout dateLinearLayout;
    @BindView(R.id.linear_layout_folder) LinearLayout folderLinearLayout;
    @BindView(R.id.linear_layout_tags) LinearLayout tagsLinearLayout;
    @BindView(R.id.linear_layout_audio) LinearLayout audioLinearLayout;
    @BindView(R.id.linear_layout_attachment) LinearLayout attachmentLinearLayout;
    @BindView(R.id.image_view_thumbnail_1) ImageView imageViewThumbnail1;
    @BindView(R.id.image_view_thumbnail_2) ImageView imageViewThumbnail2;
    @BindView(R.id.image_view_thumbnail_3) ImageView imageViewThumbnail3;
    @BindView(R.id.linear_layout_click) LinearLayout clickLinearLayout;
    @BindView(R.id.divider_2) View divider_2;
    @BindView(R.id.text_view_audio) TextView playAudioTextView;
    @BindView(R.id.image_view_audio_icon) ImageButton playAudioImageButton;
    @BindView(R.id.card_view_thumbnail_1) CardView cardViewContainer1;
    @BindView(R.id.card_view_thumbnail_2) CardView cardViewContainer2;
    @BindView(R.id.card_view_thumbnail_3) CardView cardViewContainer3;


    private String currentNoteId;
    private Journal currentJournal;
    private Realm realm;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseStorage mFirebaseStorage;
    private MediaPlayer mPlayer = null;
    private boolean isAudioPlaying = false;


    public NoteDetailFragment() {
        // Required empty public constructor
    }


    public static NoteDetailFragment newInstance(String noteId){
        NoteDetailFragment fragment = new NoteDetailFragment();
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
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();
        if (!TextUtils.isEmpty(currentNoteId)){
            currentJournal = new JournalDao(realm).getJournalById(currentNoteId);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentJournal != null){
            populateScreen();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null){
            mPlayer.release();
            mPlayer = null;
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
               EventBus.getDefault().post(new EditNoteButtonClickedEvent(currentJournal.getId()));
                break;
            case R.id.action_delete:
                showDeleteConfirmation(currentJournal);
                break;
            case R.id.action_share:
                displayShareIntent(currentJournal);
                break;
            case R.id.action_play:
                // onPlayAudioButtonClicked();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void populateScreen() {
        if (currentJournal != null){
            if (currentJournal.getAttachments() != null && currentJournal.getAttachments().size() > 0){
                showHideImageViews(currentJournal.getAttachments());
                topImageView.setVisibility(View.VISIBLE);
                displayImage(currentJournal.getAttachments().get(0).getFilePath(), topImageView);
            }else {
                attachmentLinearLayout.setVisibility(View.GONE);
                divider_2.setVisibility(View.GONE);
                topImageView.setVisibility(View.GONE);
            }
        }

        String dateString = getDateString(currentJournal.getDateCreated(), currentJournal.getDateModified());
        dateTextView.setText(dateString);

        //Display Folder name and make that folder name clickable
        if (currentJournal.getFolder() != null && !TextUtils.isEmpty(currentJournal.getFolder().getFolderName())){
            folderTextView.setText(currentJournal.getFolder().getFolderName());
        } else {
            folderTextView.setText(getString(R.string.general));
        }
        folderTextView.setPaintFlags(folderTextView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        folderTextView.setClickable(true);
        folderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to Folder screen
                onFolderClicked(currentJournal.getFolder());
            }
        });

        //Show or Hide Audio Player icon
        if (containsAudioRecording(currentJournal)){
            audioLinearLayout.setVisibility(View.VISIBLE);
        } else {
            audioLinearLayout.setVisibility(View.GONE);
        }

        //Display ProntoTag names and make each ProntoTag name clickable
        if (currentJournal.getTags() != null && currentJournal.getTags().size() > 0){
            for (int i = 0; i < currentJournal.getTags().size(); i++){
                ProntoTag prontoTag = currentJournal.getTags().get(i);
                TextView textView = new TextView(getActivity());
                int viewId = textView.generateViewId();
                textView.setId(viewId);
                textView.setText("#" + prontoTag.getTagName());
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
                        //Go to ProntoTag Detail
                        onTagClicked(prontoTag);
                    }
                });
                tagsLinearLayout.addView(textView);

            }

        }else {
            tagsLinearLayout.setVisibility(View.GONE);
        }
        noteSummary.setText(currentJournal.getContent());
        noteSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteDetailDialogFragment fragment = NoteDetailDialogFragment.newInstance(currentJournal.getId());
                fragment.show(getActivity().getSupportFragmentManager(), "Dialog");
            }
        });
        if (currentJournal.getContent().length() > 300 && SettingsHelper.getHelper(getContext()).shouldShowNoteDetailExplainer()){
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .title(R.string.note_detail)
                    .content(R.string.explain_note_detail)
                    .positiveText(R.string.label_yes)
                    .negativeText(R.string.label_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            SettingsHelper.getHelper(getContext()).onNoteDetailExplainerShown(false);
                        }
                    })
                    .show();
        }






    }

    private void onTagClicked(ProntoTag clickedProntoTag) {
        Intent tagIntent = new Intent(getActivity(), NoteListActivity.class);
        tagIntent.putExtra(Constants.TAG_FILTER, clickedProntoTag.getTagName());
        startActivity(tagIntent);
    }

    private void onFolderClicked(Folder clickedFolder) {
        Intent folderIntent = new Intent(getActivity(), FolderActivity.class);
        folderIntent.putExtra(Constants.FOLDER_ID, clickedFolder.getId());
        startActivity(folderIntent);
    }

    private boolean containsAudioRecording(Journal currentJournal) {
        for (Attachment attachment: currentJournal.getAttachments()){
            if (attachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)){
                return true;
            }
        }
        return false;
    }

    private void showHideImageViews(RealmList<Attachment> attachments) {

        imageViewThumbnail1.setOnClickListener(this);

        imageViewThumbnail1.setOnClickListener(this);
        imageViewThumbnail2.setOnClickListener(this);
        imageViewThumbnail3.setOnClickListener(this);
        clickLinearLayout.setOnClickListener(this);

        switch (attachments.size()){
            case 1:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.GONE);
                cardViewContainer3.setVisibility(View.GONE);
                clickLinearLayout.setVisibility(View.GONE);
                displayImage(attachments.get(0).getFilePath(), imageViewThumbnail1);
                break;
            case 2:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.VISIBLE);
                cardViewContainer3.setVisibility(View.GONE);
                clickLinearLayout.setVisibility(View.GONE);
                displayImage(attachments.get(0).getFilePath(), imageViewThumbnail1);
                displayImage(attachments.get(1).getFilePath(), imageViewThumbnail2);
                break;
            case 3:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.VISIBLE);
                cardViewContainer3.setVisibility(View.VISIBLE);
                clickLinearLayout.setVisibility(View.GONE);
                displayImage(attachments.get(0).getFilePath(), imageViewThumbnail1);
                displayImage(attachments.get(1).getFilePath(), imageViewThumbnail2);
                displayImage(attachments.get(2).getFilePath(), imageViewThumbnail3);
                imageViewThumbnail3.setOnClickListener(this);
                break;
            default:
                cardViewContainer1.setVisibility(View.VISIBLE);
                cardViewContainer2.setVisibility(View.VISIBLE);
                cardViewContainer3.setVisibility(View.VISIBLE);
                clickLinearLayout.setVisibility(View.VISIBLE);
                displayImage(attachments.get(0).getFilePath(), imageViewThumbnail1);
                displayImage(attachments.get(1).getFilePath(), imageViewThumbnail2);
                displayImage(attachments.get(2).getFilePath(), imageViewThumbnail3);
                String countText = attachments.size() - 3 + "";
                imageCountTextView.setText(countText);
                break;


        }
    }

    private void displayImage(String imageUrl, ImageView imageViewThumbnail){
        GlideApp.with(getContext())
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
        Attachment clickedAttachment = null;
        switch (view.getId()){
            case R.id.image_view_thumbnail_1:
                displayImage(currentJournal.getAttachments().get(0).getFilePath(), topImageView);
                break;
            case R.id.image_view_thumbnail_2:
                displayImage(currentJournal.getAttachments().get(1).getFilePath(), topImageView);
                break;
            case R.id.image_view_thumbnail_3:
                displayImage(currentJournal.getAttachments().get(2).getFilePath(), topImageView);
                break;
            default:
                clickedAttachment = currentJournal.getAttachments().get(0);
                goToGallery(clickedAttachment);
                break;
        }



    }

    private void goToGallery(Attachment clickedAttachment) {
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
            galleryIntent.putExtra(Constants.NOTE_ID, currentJournal.getId());
            galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getFilePath());
            startActivity(galleryIntent);
        }
    }

    private void showSnackbar(final String text) {
        View container = mRootView.findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(mRootView, text, Snackbar.LENGTH_LONG).show();
        }
    }

    public void showDeleteConfirmation(Journal journal) {
        final String titleOfNoteTobeDeleted = journal.getTitle();
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
                deleteNote(currentJournal);
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

    public void displayPreviousActivity() {
        getActivity().onBackPressed();

    }

    private void deleteNote(Journal journal) {
        if (!TextUtils.isEmpty(journal.getId())) {
            new JournalDao(realm).deleteJournal(journal.getId());
        }
        displayPreviousActivity();
    }


    public void displayShareIntent(final Journal journal) {
        final String titleText = journal.getTitle();

        final String contentText = titleText
                + System.getProperty("line.separator")
                + journal.getContent();

        if (journal.getAttachments().size() == 0){
            shareTextOnly(titleText, contentText);
        } else if (journal.getAttachments().size() == 1) {
            shareTextAndOneAttachment(titleText, contentText, journal);
        } else if (journal.getAttachments().size() > 1) {
            shareTextAndMultipleAttachment(titleText, contentText, journal);
        }

    }

    private void shareTextAndMultipleAttachment(String titleText, String contentText, Journal journal) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uris = new ArrayList<>();



        // A check to decide the mime type of attachments to share is done here
        HashMap<String, Boolean> mimeTypes = new HashMap<>();
        for (Attachment attachment : journal.getAttachments()) {
            if (attachment.getFilePath().contains("http")){
                continue;
            }
            Uri uri = Uri.parse(attachment.getUri());
            uris.add(uri);
            mimeTypes.put(attachment.getMime_type(), true);
        }
        // If many mime types are present a general type is assigned to intent
        if (mimeTypes.size() > 1) {
            shareIntent.setType("*/*");
        } else {
            shareIntent.setType((String) mimeTypes.keySet().toArray()[0]);
        }

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

    }

    private void shareTextAndOneAttachment(final String titleText, final String contentText, Journal journal) {
        final Intent shareIntent = new Intent();

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(journal.getAttachments().get(0).getMime_type());


        String cloudString = journal.getAttachments().get(0).getFilePath();
        StorageReference storageReference = mFirebaseStorage.getReferenceFromUrl(cloudString);
        try {
            final File localFile = FileUtility.createImageFile(Constants.MIME_TYPE_IMAGE_EXT);
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri singleUri  = FileProvider.getUriForFile(getContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            localFile);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, singleUri);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, titleText);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, contentText);
                    startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_message_chooser)));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "File Download failed: " + e.getLocalizedMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void shareTextOnly(String titleText, String contentText) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, titleText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentText);
        PackageManager packageManager = getActivity().getPackageManager();
        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_message_chooser)));
        } else {
            Log.d(TAG, getString(R.string.no_application_found));
        }
    }

    @OnClick(R.id.linear_layout_audio)
    public void onAudioIconClicked(View view){
        for (Attachment attachment: currentJournal.getAttachments()){
            if (attachment.getMime_type().equals(Constants.MIME_TYPE_AUDIO)){
                startPlaying(attachment);
            }
        }
    }





    private void startPlaying(Attachment attachment) {
        if (isAudioPlaying) {
            mPlayer.stop();
            mPlayer.release();
            isAudioPlaying = false;
            playAudioTextView.setText(getString(R.string.start_listening));
            playAudioImageButton.setImageResource(R.drawable.ic_action_headset);

        } else {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(attachment.getFilePath());
                mPlayer.prepare();
                mPlayer.start();
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (!mp.isPlaying()) {
                            isAudioPlaying = false;
                            playAudioTextView.setText(getString(R.string.start_listening));
                            playAudioImageButton.setImageResource(R.drawable.ic_action_headset);
                        }
                    }
                });
                isAudioPlaying = true;
                playAudioTextView.setText(getString(R.string.stop_listening));
                playAudioImageButton.setImageResource(R.drawable.ic_action_pause);
            } catch (IOException e) {
                Log.e(TAG, "Play audio failed " + e.getLocalizedMessage());
            }
        }

    }


}

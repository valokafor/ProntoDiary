package com.okason.diary.ui.addnote;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okason.diary.BuildConfig;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.events.AttachingFileCompleteEvent;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.events.TagListChangeEvent;
import com.okason.diary.core.events.UpdateTagLayoutEvent;
import com.okason.diary.core.listeners.OnAttachmentClickedListener;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.core.services.FileUploadIntentService;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.Tag;
import com.okason.diary.ui.attachment.AttachmentListAdapter;
import com.okason.diary.ui.attachment.AttachmentTask;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.ui.folder.AddFolderDialogFragment;
import com.okason.diary.ui.folder.SelectFolderDialogFragment;
import com.okason.diary.ui.sketch.SketchActivity;
import com.okason.diary.ui.tag.AddTagDialogFragment;
import com.okason.diary.ui.tag.SelectTagDialogFragment;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.FileUtility;
import com.okason.diary.utils.IntentChecker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteEditorFragment extends Fragment{

    private final static String LOG_TAG = "NoteEditorFragment";

    private View mRootView;

    private Journal currentJournal = null;
    private List<Attachment> attachmentList;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;



    private SelectFolderDialogFragment selectFolderDialogFragment;
    private SelectTagDialogFragment selectTagDialogFragment;
    private AddFolderDialogFragment addFolderDialogFragment;
    private AddTagDialogFragment addTagDialogFragment;

    private AttachmentListAdapter attachmentListAdapter;
    private DataAccessManager journalManager;




    private Uri attachmentUri;
    private String mLocalAudioFilePath = null;
    private String mLocalImagePath = null;
    private String mLocalVideoPath = null;
    private String mLocalSketchPath = null;
    private Calendar mReminderTime;

    private boolean dataChanged = false;
    private boolean isInEditMode = false;
    private String existingTitleText = "";
    private String existingContentText = "";



    @BindView(R.id.edit_text_category)
    EditText mCategory;

    @BindView(R.id.edit_text_title)
    EditText mTitle;

    @BindView(R.id.edit_text_note)
    EditText mContent;

    @BindView(R.id.image_attachment)
    ImageView mImageAttachment;

    @BindView(R.id.sketch_attachment)
    ImageView mSketchAttachment;

    @BindView(R.id.attachment_container)
    FrameLayout attachmentContainer;

    @BindView(R.id.attachment_list_recyclerview)
    RecyclerView attachmentRecyclerView;

    @BindView(R.id.creation)
    TextView dateCreated;

    @BindView(R.id.last_modification) TextView dateModified;

    @BindView(R.id.timestap_layout)
    LinearLayout mTimeStampLayout;

    private final int EXTERNAL_PERMISSION_REQUEST = 1;
    private final int RECORD_AUDIO_PERMISSION_REQUEST = 2;
    private final int IMAGE_CAPTURE_REQUEST = 3;
    private final int SKETCH_CAPTURE_REQUEST = 4;
    private final int VIDEO_CAPTURE_REQUEST = 5;
    private final int FILE_PICK_REQUEST = 6;
    private final int PICTURE_PICK_REQUEST = 7;
    private final int ACCESS_LOCATION_PERMISSION_REQUEST = 8;

    private SharedPreferences prefs;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private long audioRecordingTimeStart;
    private long audioRecordingTime;
    private MaterialDialog mDialog;



    public NoteEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void getPassedInJournal() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.SERIALIZED_JOURNAL)){
            String serializedNote = args.getString(Constants.SERIALIZED_JOURNAL, "");
            if (!serializedNote.isEmpty()){
                Gson gson = new Gson();
                currentJournal = gson.fromJson(serializedNote, new TypeToken<Journal>(){}.getType());
                if (currentJournal != null & !TextUtils.isEmpty(currentJournal.getId())){
                    isInEditMode = true;
                }
            }
        }

    }

    public static NoteEditorFragment newInstance(String content){
        NoteEditorFragment fragment = new NoteEditorFragment();
        if (!TextUtils.isEmpty(content)){
            Bundle args = new Bundle();
            args.putString(Constants.SERIALIZED_JOURNAL, content);
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        attachmentList = new ArrayList<>();

        //Gets the root of out Datbase
        if (firebaseUser != null) {
            journalManager = new DataAccessManager(firebaseUser.getUid());
            getPassedInJournal();

        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());




        return mRootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (isInEditMode){
            populateNote(currentJournal);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_attachment:
                showSelectAttachmentDialog();
                break;
            case R.id.action_delete:
                if (isInEditMode && currentJournal != null) {
                    promptForDelete(currentJournal);
                } else {
                    promptForDiscard();
                }
                break;
            case R.id.action_share:
                displayShareIntent(currentJournal);
                break;
            case android.R.id.home:
                if (dataChanged){
                    addNote();
                } else  if (mContent.getText().equals(existingContentText) || mTitle.getText().equals(existingTitleText)){
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), getString(R.string.saving_journal), Toast.LENGTH_SHORT);
                    addNote();
                }
                break;
            case R.id.action_tag:
                journalManager.getAllTags();
                break;


        }
        return true;
    }

    /**
     * This methods shows a dialog for the user to select one or more tags
     * For this journal. The selected tags are delivered back to this Fragment via
     * A Listener. Tags are saved to the Journal as a Map of String and Boolean
     */
    private void showSelectTag(List<Tag> tagList) {
        selectTagDialogFragment = SelectTagDialogFragment.newInstance();
        List<String> existingTags = new ArrayList<>();
        if (isInEditMode) {
            for (Map.Entry<String, Boolean> entry : currentJournal.getTags().entrySet()) {
                String key = entry.getKey();
                boolean value = entry.getValue();
                if (value){
                    existingTags.add(key);
                }
            }
        }
        selectTagDialogFragment.setTags(existingTags);
        selectTagDialogFragment.setDataAccessManager(journalManager);
        selectTagDialogFragment.setAllTags(tagList);



        selectTagDialogFragment.setListener(new OnTagSelectedListener() {
            @Override
            public void onTagChecked(Tag selectedTag) {
                if (currentJournal == null){
                    currentJournal = new Journal();
                }
                if (!currentJournal.getTags().containsKey(selectedTag.getTagName())){
                    currentJournal.getTags().put(selectedTag.getTagName(), true);
                }
                initTagLayout(currentJournal.getTags());


            }

            @Override
            public void onTagUnChecked(Tag unSelectedTag) {
                if (currentJournal != null){
                    if (currentJournal.getTags().containsKey(unSelectedTag.getTagName())){
                        currentJournal.getTags().remove(unSelectedTag.getTagName());
                        //currentJournal.getTags().put(unSelectedTag.getTagName(), false);
                    }
                }
                initTagLayout(currentJournal.getTags());
            }


            @Override
            public void onAddTagButtonClicked() {
                showAddNewTagDialog();
            }

            @Override
            public void onTagClicked(Tag clickedTag) {

            }

            @Override
            public void onEditTagButtonClicked(Tag clickedTag) {

            }

            @Override
            public void onDeleteTagButtonClicked(Tag clickedTag) {

            }
        });
        selectTagDialogFragment.show(getActivity().getFragmentManager(), "Dialog");
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttachmentAddedToNote(AttachingFileCompleteEvent event) {
        hideProgressDialog();
        updateAttachmentList(event.getAttachment());
        dataChanged = true;
    }

    private void updateAttachmentList(Attachment attachment) {
        attachmentList.add(attachment);
        initViewAttachments(attachmentList);

    }


    /**
     * This event will be called when a user adds a new Folder
     * @param event - containing the new Folder
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewCategory(FolderAddedEvent event){
        addFolderDialogFragment.dismiss();

        if (event.getFolder() != null){
            mCategory.setText(event.getFolder().getFolderName());
            if (currentJournal == null){
                currentJournal = new Journal();
            }
            currentJournal.setFolder(event.getFolder());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatedTagLayout(UpdateTagLayoutEvent event){
        List<Tag> tags = event.getTags();
        initTagLayout(currentJournal.getTags());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTagListChange(TagListChangeEvent event){
        showSelectTag(event.getTaglList());
    }




    @OnClick(R.id.edit_text_category)
    public void showSelectFolder(){
        showChooseFolderDialog();
    }

    private void showChooseFolderDialog() {
        selectFolderDialogFragment = selectFolderDialogFragment.newInstance();
        selectFolderDialogFragment.setDataAccessManager(journalManager);
        selectFolderDialogFragment.setCategorySelectedListener(new OnFolderSelectedListener() {
            @Override
            public void onCategorySelected(Folder selectedCategory) {
                selectFolderDialogFragment.dismiss();
                mCategory.setText(selectedCategory.getFolderName());
                if (currentJournal == null){
                    currentJournal = new Journal();
                }
                currentJournal.setFolder(selectedCategory);
            }

            @Override
            public void onEditCategoryButtonClicked(Folder selectedCategory) {

            }

            @Override
            public void onDeleteCategoryButtonClicked(Folder selectedCategory) {

            }

            @Override
            public void onAddCategoryButtonClicked() {
                showAddNewFolderDialog();
            }
        });
        selectFolderDialogFragment.show(getActivity().getFragmentManager(), "Dialog");
    }


    public void showAddNewFolderDialog() {
        if (selectFolderDialogFragment != null) {
            selectFolderDialogFragment.dismiss();
        }
        addFolderDialogFragment = AddFolderDialogFragment.newInstance("");
        addFolderDialogFragment.show(getActivity().getFragmentManager(), "Dialog");

    }

    public void showAddNewTagDialog() {
        if (selectTagDialogFragment != null) {
            selectTagDialogFragment.dismiss();
        }
        addTagDialogFragment = AddTagDialogFragment.newInstance("");
        addTagDialogFragment.show(getActivity().getFragmentManager(), "Dialog");

    }


    private void makeToast(String message) {
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }


    /**
     * This method populates the screen with existing data
     * @param journal - Journal to be shown
     */
    public void populateNote(Journal journal) {

        mTitle.setHint(R.string.placeholder_journal_title);
        mContent.setHint(R.string.placeholder_journal_text);

        try {
            mContent.setText(journal.getContent());
            existingContentText = journal.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTitle.setText(journal.getTitle());
            existingTitleText = journal.getTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mCategory.setText(journal.getFolder().getFolderName());
        } catch (Exception e) {
            mCategory.setText(Constants.DEFAULT_CATEGORY);
        }

        mContent.requestFocus();
        try {
            attachmentList = journal.getAttachments();
            if (attachmentList != null && attachmentList.size() == 1){
                mImageAttachment.setVisibility(View.VISIBLE);
                attachmentContainer.setVisibility(View.GONE);
                Glide.with(getContext())
                        .load(attachmentList.get(0).getFilePath())
                        .centerCrop()
                        .crossFade()
                        .placeholder(R.drawable.image_broken)
                        .into(mImageAttachment);
            }else {
                initViewAttachments(attachmentList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!journal.getTags().isEmpty()){
            initTagLayout(journal.getTags());
        }



    }

    private void initTagLayout(Map<String, Boolean> tags) {
        if (tags != null && tags.size() > 0){
            mTimeStampLayout.setVisibility(View.VISIBLE);
            dateCreated.setVisibility(View.VISIBLE);
            String tagText = "";
            for (String tag : tags.keySet()) {
                tagText = tagText + "#" + tag + ", ";
            }

            dateCreated.setText(tagText);
            dateCreated.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_dark));
            dateCreated.setTypeface(dateCreated.getTypeface(), Typeface.BOLD);
        }else {
            mTimeStampLayout.setVisibility(View.GONE);
            dateCreated.setVisibility(View.GONE);
        }
    }


    public void showProgressDialog() {
        mDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.please_wait)
                .titleColorRes(R.color.primary_dark)
                .content(R.string.processing_image)
                .progress(true, 0)
                .show();
    }


    public void hideProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }

    }




    public String getTitle() {
        return mTitle.getText().toString();
    }

    public String getContent() {
        return mContent.getText().toString();
    }


    /**
     * Shows a horizontal layout at the top of the screen that displays
     * thunmnail of attachments
     *
     * @param attachmentList - the list of attachments for this Journal
     */
    private void initViewAttachments(final List<Attachment> attachmentList) {
        mImageAttachment.setVisibility(View.GONE);

        attachmentContainer.setVisibility(View.VISIBLE);
        attachmentRecyclerView = (RecyclerView) mRootView.findViewById(R.id.attachment_list_recyclerview);
        attachmentListAdapter = null;
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
                if (clickedAttachment.getMime_type().equals(Constants.MIME_TYPE_FILES)) {
                    //show file
                    Uri uri = Uri.parse(clickedAttachment.getUri());
                    String fileType = "";
                    String name = FileHelper.getNameFromUri(getActivity(), uri);
                    String extension = FileHelper.getFileExtension(name).toLowerCase();

                    if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")) {
                        fileType = "image/jpeg";
                    } else if (extension.equals(".pdf")) {
                        fileType = "application/pdf";
                    } else {
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

                } else {
                    Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
                    Gson gson = new Gson();
                    String attachmentJson = gson.toJson(attachmentList);
                    galleryIntent.putExtra(Constants.SERIALIZED_ATTACHMENT_ID, attachmentJson);
                    galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getLocalFilePath());
                    galleryIntent.putExtra(Constants.NOTE_TITLE, mTitle.getText());
                    startActivity(galleryIntent);
                }
            }
        });
        attachmentRecyclerView.setAdapter(attachmentListAdapter);
        //Scroll to the last attachment on the list
        int lastPosition = attachmentList.size() - 1;
        attachmentRecyclerView.scrollToPosition(lastPosition);

    }

    private void showSelectAttachmentDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View layout = (View) inflater.inflate(R.layout.attachment_dialog, null);
        alertDialog.setView(layout);

        View titleView = (View) inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView) titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(getString(R.string.select_attachment));
        alertDialog.setCustomTitle(titleView);
        final Dialog dialog = alertDialog.create();
        dialog.show();

        TextView cameraSelection = (TextView) layout.findViewById(R.id.camera);
        cameraSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (isStoragePermissionGrantedForImage()) {
                        takePhoto();
                    }
                } else {
                    makeToast(getString(R.string.feature_not_available_on_this_device));
                }
                dialog.dismiss();
            }
        });


        TextView videoSelection = (TextView) layout.findViewById(R.id.video);
        videoSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGrantedForPickingFile()) {
                    takeVideo();
                }
                dialog.dismiss();
            }
        });

        TextView fileSelection = (TextView) layout.findViewById(R.id.files);
        fileSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGrantedForPickingFile()) {
                    pickFile();
                }
                dialog.dismiss();
            }
        });

        TextView pictureSelection = (TextView) layout.findViewById(R.id.picture);
        pictureSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGrantedForPickingPicture()) {
                    pickPicture();
                }
                dialog.dismiss();
            }
        });


        TextView sketchSelection = (TextView) layout.findViewById(R.id.sketch);
        sketchSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGrantedForSketch()) {
                    Intent sketchIntent = new Intent(getActivity(), SketchActivity.class);
                    startActivityForResult(sketchIntent, SKETCH_CAPTURE_REQUEST);
                }
                dialog.dismiss();
            }
        });

        TextView recordSelection = (TextView) layout.findViewById(R.id.recording);
        recordSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = getActivity().getPackageManager();
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
                    if (isRecordPermissionGranted()) {
                        if (isRecordPermissionGranted()) {
                            promptToStartRecording();
                        }
                    }
                } else {
                    makeToast(getContext().getString(R.string.error_no_mic));
                }
                dialog.dismiss();
            }
        });

        TextView locationSelection = (TextView) layout.findViewById(R.id.location);


    }

    private void pickFile() {
        Intent filesIntent;
        filesIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filesIntent.addCategory(Intent.CATEGORY_OPENABLE);
        filesIntent.setType("*/*");
        startActivityForResult(filesIntent, FILE_PICK_REQUEST);

    }

    private void pickPicture() {
        Intent filesIntent;
        filesIntent = new Intent(Intent.ACTION_PICK);
        filesIntent.setType("image/*");
        startActivityForResult(filesIntent, PICTURE_PICK_REQUEST);

    }


    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isStoragePermissionGrantedForImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG, "Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_CAPTURE_REQUEST);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG, "Permission is granted  API < 23");
            return true;
        }
    }


    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isRecordPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQUEST);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    //Checks whether the user has granted the app permission to
    //access location info
    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_PERMISSION_REQUEST);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isStoragePermissionGrantedForPickingFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG, "Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_PICK_REQUEST);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG, "Permission is granted  API < 23");
            return true;
        }
    }


    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isStoragePermissionGrantedForPickingPicture() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG, "Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICTURE_PICK_REQUEST);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG, "Permission is granted  API < 23");
            return true;
        }
    }

    private boolean isStoragePermissionGrantedForSketch() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG, "Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SKETCH_CAPTURE_REQUEST);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG, "Permission is granted  API < 23");
            return true;
        }
    }


    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = FileUtility.createImageFile(Constants.MIME_TYPE_IMAGE_EXT);

        } catch (IOException ex) {
            // Error occurred while creating the File
            makeToast(getString(R.string.unable_to_save_file));
            Log.d(LOG_TAG, ex.getLocalizedMessage());
        }
        // Continue only if the File was successfully created
        mLocalImagePath = photoFile.getAbsolutePath();
        if (photoFile != null) {
            Uri fileUri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            attachmentUri = fileUri;
            Log.d(LOG_TAG, "takePhoto Uri: " + fileUri);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST);
        }

    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        File recordFile = null;
        try {
            recordFile = FileUtility.createImageFile(Constants.MIME_TYPE_AUDIO_EXT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mLocalAudioFilePath = recordFile.getAbsolutePath();
        if (recordFile != null) {
            Uri fileUri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    recordFile);
            attachmentUri = fileUri;

            mRecorder.setOutputFile(mLocalAudioFilePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(96000);
            mRecorder.setAudioSamplingRate(44100);

            try {
                audioRecordingTimeStart = Calendar.getInstance().getTimeInMillis();
                mRecorder.prepare();
                mRecorder.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
                makeToast("Unable to record " + e.getLocalizedMessage());
            }

        }
        ;

    }

//
//    public File createImageFile(String extension) throws IOException {
//        // Create an image file name
//        String timeStamp = TimeUtils.getDatetimeSuffix(System.currentTimeMillis());
//        String imageFileName = "Image_" + timeStamp + "_";
//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                extension,         /* suffix */
//                storageDir      /* directory */
//        );
//
//        return image;
//    }


    private void takeVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File videoFile = null;
        try {
            videoFile = FileUtility.createImageFile(Constants.MIME_TYPE_VIDEO_EXT);

        } catch (IOException ex) {
            // Error occurred while creating the File
            makeToast(getString(R.string.unable_to_save_file));
            Log.d(LOG_TAG, ex.getLocalizedMessage());
        }
        // Continue only if the File was successfully created
        mLocalVideoPath = videoFile.getAbsolutePath();
        if (videoFile != null) {
            Uri fileUri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    videoFile);
            attachmentUri = fileUri;
            Log.d(LOG_TAG, "Video Uri: " + fileUri);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
        }
        ;

        String maxVideoSizeStr = "".equals(prefs.getString("settings_max_video_size",
                "")) ? "0" : prefs.getString("settings_max_video_size", "");
        int maxVideoSize = Integer.parseInt(maxVideoSizeStr);
        long limit = Long.valueOf(maxVideoSize * 1024 * 1024);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, limit);
        startActivityForResult(takeVideoIntent, VIDEO_CAPTURE_REQUEST);
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mLocalAudioFilePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }


    private void stopRecording() {
        if (mRecorder != null) {
            audioRecordingTime = Calendar.getInstance().getTimeInMillis() - audioRecordingTimeStart;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            Attachment attachment = new Attachment(attachmentUri, mLocalAudioFilePath, Constants.MIME_TYPE_AUDIO);
            attachmentList.add(attachment);

        }

        makeToast("Recording added");


    }


    public void promptToStartRecording() {
        String title = getContext().getString(R.string.start_recording);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View) inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView) titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);


        alertDialog.setPositiveButton(getString(R.string.start), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startRecording();
                promptToStopRecording();
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

    public void promptToStopRecording() {
        String title = getContext().getString(R.string.stop_recording);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View) inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView) titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);


        alertDialog.setPositiveButton(getString(R.string.stop), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopRecording();
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Attachment attachment;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_CAPTURE_REQUEST:
                    attachment = new Attachment(attachmentUri, mLocalImagePath, Constants.MIME_TYPE_IMAGE);
                    addPhotoToGallery(mLocalImagePath);
                    updateAttachmentList(attachment);
                    dataChanged = true;
                    break;
                case VIDEO_CAPTURE_REQUEST:
                    attachment = new Attachment(attachmentUri, mLocalVideoPath, Constants.MIME_TYPE_VIDEO);
                    updateAttachmentList(attachment);
                    dataChanged = true;
                    break;
                case FILE_PICK_REQUEST:
                    handleFilePickIntent(data);
                    break;
                case SKETCH_CAPTURE_REQUEST:
                    String sketchFilePath = data.getData().toString();
                    File sketchFile = new File(sketchFilePath);
                    Uri fileUri = FileProvider.getUriForFile(getContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            sketchFile);
                    if (!TextUtils.isEmpty(sketchFilePath)) {
                        attachment = new Attachment(fileUri, sketchFilePath, Constants.MIME_TYPE_SKETCH);
                        updateAttachmentList(attachment);
                        dataChanged = true;
                    } else {
                        makeToast(getString(R.string.error_sketch_is_empty));
                    }
                    break;
                case PICTURE_PICK_REQUEST:
                    handlePicturePickIntent(data);
                    break;


            }
        }
    }



    //Called when a file is picked
    private void handleFilePickIntent(Intent intent) {
        List<Uri> uris = new ArrayList<>();

        if (Build.VERSION.SDK_INT > 16 && intent.getClipData() != null) {
            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                uris.add(intent.getClipData().getItemAt(i).getUri());
            }
        } else {
            uris.add(intent.getData());
        }


        for (Uri uri : uris) {
            String name = FileHelper.getNameFromUri(getActivity(), uri);
            showProgressDialog();
            new AttachmentTask(this, uri, name).execute();
        }
    }

    //Called when a picture is picked
    private void handlePicturePickIntent(Intent intent) {
        List<Uri> uris = new ArrayList<>();

        if (Build.VERSION.SDK_INT > 16 && intent.getClipData() != null) {
            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                uris.add(intent.getClipData().getItemAt(i).getUri());
            }
        } else {
            uris.add(intent.getData());
        }


        for (Uri uri : uris) {
            String name = FileHelper.getNameFromUri(getActivity(), uri);
            showProgressDialog();
            new AttachmentTask(this, uri, name).execute();
        }
    }

    private void addPhotoToGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_PERMISSION_REQUEST:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (isRecordPermissionGranted()) {
//                        promptToStartRecording();
//                    }
//                } else {
//                    //permission was denied, disable backup
//                    makeToast("External storage access denied");
//                }
                break;
            case RECORD_AUDIO_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    promptToStartRecording();
                } else {
                    //permission was denied, disable backup
                    makeToast("Mic access denied");
                }
                break;
            case IMAGE_CAPTURE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted take picture
                    takePhoto();
                } else {
                    //permission was denied, disable backup
                    makeToast(getString(R.string.external_access_denied));
                }
                break;
            case FILE_PICK_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted take picture
                    pickFile();
                } else {
                    //permission was denied, disable backup
                    makeToast(getString(R.string.external_access_denied));
                }
                break;
            case PICTURE_PICK_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted take picture
                    pickPicture();
                } else {
                    //permission was denied, disable backup
                    makeToast(getString(R.string.external_access_denied));
                }
                break;
            case SKETCH_CAPTURE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted perform backup
                    Intent sketchIntent = new Intent(getActivity(), SketchActivity.class);
                    startActivityForResult(sketchIntent, SKETCH_CAPTURE_REQUEST);
                } else {
                    //permission was denied, disable backup
                    makeToast("External storage access denied");
                }
                break;


        }


    }


    public void displayShareIntent(Journal journal) {
        if (journal != null && !TextUtils.isEmpty(journal.getContent()) && !TextUtils.isEmpty(journal.getTitle())) {

            final String titleText = journal.getTitle();

            final String contentText = journal.getContent();

            if (journal.getAttachments().size() == 0){
                shareTextOnly(titleText, contentText);
            } else if (journal.getAttachments().size() == 1) {
                shareTextAndOneAttachment(titleText, contentText, journal);
            } else if (journal.getAttachments().size() > 1) {
                shareTextAndMultipleAttachment(titleText, contentText, journal);
            }

        } else {
            makeToast(getString(R.string.no_notes_found));
            return;

        }

    }

    private void shareTextOnly(String titleText, String contentText) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, titleText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentText);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_message_chooser)));

    }

    private void shareTextAndMultipleAttachment(String titleText, String contentText, Journal journal) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uris = new ArrayList<>();



        // A check to decide the mime type of attachments to share is done here
        HashMap<String, Boolean> mimeTypes = new HashMap<>();
        for (Attachment attachment : journal.getAttachments()) {
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


        shareIntent.putExtra(Intent.EXTRA_SUBJECT, titleText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentText);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_message_chooser)));

    }

    private void shareTextAndOneAttachment(final String titleText, final String contentText, Journal journal) {
        final Intent shareIntent = new Intent();

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(journal.getAttachments().get(0).getMime_type());
        Uri singleUri = Uri.parse(journal.getAttachments().get(0).getUri());

        shareIntent.putExtra(Intent.EXTRA_STREAM, singleUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, titleText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentText);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_message_chooser)));

    }

    private void resetFields() {
        mCategory.setText("");
        mTitle.setText("");
        mContent.setText("");
    }

    private void promptForDelete(Journal journal){
        String title = "Delete " + journal.getTitle();
        String message =  "Are you sure you want to delete journal " + journal.getTitle() + "?";


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.show();
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getActivity(), NoteListActivity.class));
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void promptForDiscard(){
        String title = "Discard Journal";
        String message =  "Are you sure you want to discard note ";


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetFields();
                startActivity(new Intent(getActivity(), NoteListActivity.class));
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void addNote() {

        if (currentJournal == null) {
            currentJournal = new Journal();
        }

        String content = mContent.getText().toString();
        String title = mTitle.getText().toString();

        if (TextUtils.isEmpty(content)){
            content = getString(R.string.missing_content);
        }

        if (TextUtils.isEmpty(title)){
            title = getString(R.string.missing_title);
        }

        currentJournal.setContent(content);
        currentJournal.setTitle(title);
        currentJournal.setDateModified(System.currentTimeMillis());

        if (attachmentList != null && attachmentList.size() > 0) {
            currentJournal.setAttachments(attachmentList);
        }

        if (TextUtils.isEmpty(currentJournal.getId())){
            journalManager.getJournalCloudPath().add(currentJournal)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                String key = task.getResult().getId();
                                currentJournal.setId(key);
                                journalManager.getJournalCloudPath().document(key).set(currentJournal);

                                if (currentJournal.getFolder() == null) {
                                    addJournalToDefaultFolder(currentJournal);
                                }

                                //After saving the Journal
                                //Kick off an Intent Service that uploads the attachments to cloud
                                Intent uploadIntent = new Intent(getActivity(),
                                        FileUploadIntentService.class);
                                uploadIntent.putExtra(Constants.JOURNAL_ID, key);
                                getActivity().startService(uploadIntent);
                            }
                        }
                    });
        }else {
            journalManager.getJournalCloudPath().document(currentJournal.getId()).set(currentJournal);

            if (currentJournal.getFolder() == null) {
                addJournalToDefaultFolder(currentJournal);
            }

            //After updating the Journal
            //Kick off an Intent Service that uploads the attachments to cloud
            Intent uploadIntent = new Intent(getActivity(),
                    FileUploadIntentService.class);
            uploadIntent.putExtra(Constants.JOURNAL_ID, currentJournal.getId());
            getActivity().startService(uploadIntent);
        }

        startActivity(new Intent(getActivity(), NoteListActivity.class));

    }

    private void addJournalToDefaultFolder(final Journal currentJournal) {
        journalManager.getFolderPath().whereEqualTo("folderName", Constants.DEFAULT_CATEGORY)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()){
                    final Folder folder = new Folder();
                    folder.setFolderName(Constants.DEFAULT_CATEGORY);
                    folder.setDateModified(System.currentTimeMillis());
                    folder.setDateCreated(System.currentTimeMillis());
                    journalManager.getFolderPath().add(folder).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                folder.setId(task.getResult().getId());
                                journalManager.getFolderPath().document(folder.getId()).set(folder);
                                currentJournal.setFolder(folder);
                                journalManager.getJournalCloudPath().document(currentJournal.getId()).set(currentJournal);
                            }
                        }
                    });
                }else {
                    Folder defaultFolder = documentSnapshots.getDocuments().get(0).toObject(Folder.class);
                    currentJournal.setFolder(defaultFolder);
                    journalManager.getJournalCloudPath().document(currentJournal.getId()).set(currentJournal);
                }
            }
        });

    }










}

package com.okason.diary.ui.addnote;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.TextView;

import com.okason.diary.BuildConfig;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.events.DatabaseOperationCompletedEvent;
import com.okason.diary.core.events.ItemDeletedEvent;
import com.okason.diary.core.listeners.OnAttachmentClickedListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.attachment.AttachmentListAdapter;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.ui.folder.AddFolderDialogFragment;
import com.okason.diary.ui.folder.SelectFolderDialogFragment;
import com.okason.diary.ui.sketch.SketchActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.IntentChecker;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private AttachmentListAdapter attachmentListAdapter;

    private Uri attachmentUri;
    private String mLocalAudioFilePath = null;
    private String mLocalImagePath = null;
    private String mLocalVideoPath = null;
    private String mLocalSketchPath = null;
    private Calendar mReminderTime;

    @BindView(R.id.edit_text_category)
    EditText mCategory;

    @BindView(R.id.edit_text_title) EditText mTitle;

    @BindView(R.id.edit_text_note) EditText mContent;

    @BindView(R.id.image_attachment)
    ImageView mImageAttachment;

    @BindView(R.id.sketch_attachment) ImageView mSketchAttachment;

    @BindView(R.id.attachment_container)
    FrameLayout attachmentContainer;

    @BindView(R.id.attachment_list_recyclerview)
    RecyclerView attachmentRecyclerView;

    private final int EXTERNAL_PERMISSION_REQUEST = 1;
    private final int RECORD_AUDIO_PERMISSION_REQUEST = 2;
    private final int IMAGE_CAPTURE_REQUEST = 3;
    private final int SKETCH_CAPTURE_REQUEST = 4;
    private final int VIDEO_CAPTURE_REQUEST = 5;
    private final int FILE_PICK_REQUEST = 6;

    private SharedPreferences prefs;




    public NoteEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_attachment:
                showSelectAttachmentDialog();
                break;


        }
        return super.onOptionsItemSelected(item);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemDeletedEvent(ItemDeletedEvent event){
        if (event.getResult().equals(Constants.RESULT_OK)){
            //When a Note is Deleted, go to the Note List
            startActivity(new Intent(getActivity(), NoteListActivity.class));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDatabaseOperationCompleteEvent(DatabaseOperationCompletedEvent event){
        if (event.isShouldUpdateUi()){
            mPresenter.updatedUI();
        }
        if (!TextUtils.isEmpty(event.getMessage())){
            makeToast(event.getMessage());
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

        mTitle.setHint(R.string.placeholder_journal_title);
        mContent.setHint(R.string.placeholder_journal_text);

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

        if (note.getFolder() != null){
            mCategory.setText(note.getFolder().getFolderName());
        }else {
            mCategory.setText(Constants.DEFAULT_CATEGORY);
        }
        mContent.requestFocus();
        if (note.getAttachments() != null && note.getAttachments().size() > 0){
            initViewAttachments(note.getAttachments());
        }

    }

    /**
     * Shows a horizontal layout at the top of the screen that displays
     * thunmnail of attachments
     * @param attachmentList - the list of attachments for this Note
     */
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
                    galleryIntent.putExtra(Constants.NOTE_ID, mPresenter.getCurrentNoteId());
                    galleryIntent.putExtra(Constants.SELECTED_ID, clickedAttachment.getId());
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

        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(getString(R.string.select_attachment));
        alertDialog.setCustomTitle(titleView);
        final Dialog dialog = alertDialog.create();
        dialog.show();

        TextView cameraSelection = (TextView) layout.findViewById(R.id.camera);
        cameraSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    if (isStoragePermissionGrantedForImage()){
                        takePhoto();
                    }
                }else {
                    makeToast(getString(R.string.feature_not_available_on_this_device));
                }
                dialog.dismiss();
            }
        });


        TextView  videoSelection = (TextView) layout.findViewById(R.id.video);
        videoSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGrantedForPickingFile()){
                    takeVideo();
                }
                dialog.dismiss();
            }
        });

        TextView fileSelection = (TextView) layout.findViewById(R.id.files);
        fileSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGrantedForPickingFile()){
                    pickFile();
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
                if (isStoragePermissionGrantedForSketch()) {
                    Intent sketchIntent = new Intent(getActivity(), SketchActivity.class);
                    startActivityForResult(sketchIntent, SKETCH_CAPTURE_REQUEST);
                }
                dialog.dismiss();
            }
        });



    }

    private void pickFile() {
        Intent filesIntent;
        filesIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filesIntent.addCategory(Intent.CATEGORY_OPENABLE);
        filesIntent.setType("*/*");
        startActivityForResult(filesIntent, FILE_PICK_REQUEST);

    }




    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isStoragePermissionGrantedForImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG,"Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_CAPTURE_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted  API < 23");
            return true;
        }
    }

    //Checks whether the user has granted the app permission to
    //access external storage
    private boolean isStoragePermissionGrantedForPickingFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG,"Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_PICK_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted  API < 23");
            return true;
        }
    }

    private boolean isStoragePermissionGrantedForSketch() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {
                Log.v(LOG_TAG,"Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SKETCH_CAPTURE_REQUEST);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted  API < 23");
            return true;
        }
    }


    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile(Constants.MIME_TYPE_IMAGE_EXT);

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
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST);
        };
    }

    public File createImageFile(String extension) throws IOException {
        // Create an image file name
        String timeStamp = TimeUtils.getDatetimeSuffix(System.currentTimeMillis());
        String imageFileName = "Image_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                extension,         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    private void takeVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File videoFile = null;
        try {
            videoFile = createImageFile(Constants.MIME_TYPE_VIDEO_EXT);

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
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
        };

        String maxVideoSizeStr = "".equals(prefs.getString("settings_max_video_size",
                "")) ? "0" : prefs.getString("settings_max_video_size", "");
        int maxVideoSize = Integer.parseInt(maxVideoSizeStr);
        long limit =  Long.valueOf(maxVideoSize * 1024 * 1024);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, limit);
        startActivityForResult(takeVideoIntent, VIDEO_CAPTURE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Attachment attachment;
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case IMAGE_CAPTURE_REQUEST:
                    attachment = new Attachment(attachmentUri, mLocalImagePath, Constants.MIME_TYPE_IMAGE);
                    addPhotoToGallery(mLocalImagePath);
                    mPresenter.onAttachmentAdded(attachment);
                    break;
                case VIDEO_CAPTURE_REQUEST:
                    attachment = new Attachment(attachmentUri, mLocalVideoPath, Constants.MIME_TYPE_VIDEO);
                    mPresenter.onAttachmentAdded(attachment);
                    break;
                case FILE_PICK_REQUEST:
                    handleFilePickIntent(data);
                    break;
                case SKETCH_CAPTURE_REQUEST:
                    String sketchFilePath = data.getData().toString();
                    if (!TextUtils.isEmpty(sketchFilePath)){
                        attachment = new Attachment(Uri.parse(sketchFilePath), sketchFilePath, Constants.MIME_TYPE_SKETCH);
                        mPresenter.onAttachmentAdded(attachment);
                    }else {
                        makeToast(getString(R.string.error_sketch_is_empty));
                    }
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
            mPresenter.onFileAttachmentSelected(uri, name);
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
                   // promptToStartRecording();
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

}

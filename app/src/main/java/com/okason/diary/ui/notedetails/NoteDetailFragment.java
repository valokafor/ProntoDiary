package com.okason.diary.ui.notedetails;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.okason.diary.BuildConfig;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.events.EditNoteButtonClickedEvent;
import com.okason.diary.core.events.ItemDeletedEvent;
import com.okason.diary.core.listeners.OnAttachmentClickedListener;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.ui.attachment.AttachmentListAdapter;
import com.okason.diary.ui.attachment.GalleryActivity;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.FileHelper;
import com.okason.diary.utils.FileUtility;
import com.okason.diary.utils.IntentChecker;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDetailFragment extends Fragment{
    private final static String TAG = "NoteDetailFragment";

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

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private DatabaseReference noteCloudReference;
    private FirebaseStorage mFirebaseStorage;


    private Note mCurrentNote = null;



    public NoteDetailFragment() {
        // Required empty public constructor
    }

    private Note getPassedInNote() {
        if (getArguments() != null && getArguments().containsKey(Constants.SERIALIZED_NOTE)) {
            String serializedNote = getArguments().getString(Constants.SERIALIZED_NOTE);
            if (!TextUtils.isEmpty(serializedNote)) {
                Gson gson = new Gson();
                Note note = gson.fromJson(serializedNote, Note.class);
                return note;
            }
        }
        return null;

    }

    public static NoteDetailFragment newInstance(String serializedNote) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        if (!TextUtils.isEmpty(serializedNote)) {
            Bundle args = new Bundle();
            args.putString(Constants.SERIALIZED_NOTE, serializedNote);
            fragment.setArguments(args);
        }

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        noteCloudReference =  mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_END_POINT);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mCurrentNote = getPassedInNote();
        displayNote(mCurrentNote);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Disable Edittexts
        displayReadOnlyViews();

        //Show the timestamp footer layout in details
        mTimeStampLayout.setVisibility(View.VISIBLE);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

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
                EventBus.getDefault().post(new EditNoteButtonClickedEvent(mCurrentNote));
                break;
            case R.id.action_delete:
                showDeleteConfirmation(mCurrentNote);
                break;
            case R.id.action_share:
                displayShareIntent(mCurrentNote);
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


    public void displayNote(Note note) {
        try {
            mCategory.setText(note.getFolderName());
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


        try {
            if (note.getAttachments() != null && note.getAttachments().size() > 0){
                initViewAttachments(note.getAttachments());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                deleteNote(mCurrentNote);
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


    public void showMessage(String message) {
        makeToast(message);
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
                    Gson gson = new Gson();
                    String serializedNote = gson.toJson(mCurrentNote);
                    galleryIntent.putExtra(Constants.SERIALIZED_NOTE, serializedNote);
                    galleryIntent.putExtra(Constants.FILE_PATH, clickedAttachment.getLocalFilePath());
                    startActivity(galleryIntent);
                }
            }
        });
        attachmentRecyclerView.setAdapter(attachmentListAdapter);
        //Scroll to the last attachment on the list
        int lastPosition = attachmentList.size() - 1;
        attachmentRecyclerView.scrollToPosition(lastPosition);

    }

    public void displayShareIntent(final Note note) {
        final String titleText = note.getTitle();

        final String contentText = titleText
                + System.getProperty("line.separator")
                + note.getContent();

        if (note.getAttachments().size() == 0){
            shareTextOnly(titleText, contentText);
        } else if (note.getAttachments().size() == 1) {
            shareTextAndOneAttachment(titleText, contentText, note);
        } else if (note.getAttachments().size() > 1) {
            shareTextAndMultipleAttachment(titleText, contentText, note);
        }

    }

    private void shareTextAndMultipleAttachment(String titleText, String contentText, Note note) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uris = new ArrayList<>();



        // A check to decide the mime type of attachments to share is done here
        HashMap<String, Boolean> mimeTypes = new HashMap<>();
        for (Attachment attachment : note.getAttachments()) {
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

    private void shareTextAndOneAttachment(final String titleText, final String contentText, Note note) {
        final Intent shareIntent = new Intent();

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(note.getAttachments().get(0).getMime_type());
        Uri singleUri = Uri.parse(note.getAttachments().get(0).getUri());
        shareIntent.putExtra(Intent.EXTRA_STREAM, singleUri);

        String cloudString = note.getAttachments().get(0).getCloudFilePath();
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
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_message_chooser)));

    }

    private void deleteNote(Note note) {
        if (!TextUtils.isEmpty(note.getId())) {
            noteCloudReference.child(note.getId()).removeValue();
        }
        displayPreviousActivity();
    }

}

package com.okason.diary.core.services;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Note;
import com.okason.diary.models.StorageRecord;
import com.okason.diary.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by valokafor on 6/8/17.
 */

@SuppressWarnings("VisibleForTests")
public class AttachmentUploadService extends AttachmentBaseService {
    private static final String TAG = "MyUploadService";

    /**
     * Intent Actions
     **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    /**
     * Intent Extras
     **/
    public static final String NOTE_ID = "note_id";
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    // [START declare_ref]
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;
    private StorageReference mAttachmentStorageReference;
    private DatabaseReference noteCloudReference;
    private DatabaseReference storageRecordCloudReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener;
    // [END declare_ref]

    @Override
    public void onCreate() {
        super.onCreate();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();


        // [START get_storage_ref]
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_BUCKET);
        mAttachmentStorageReference = mFirebaseStorageReference.child("attachments/" + mFirebaseUser.getUid());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        noteCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.NOTE_CLOUD_END_POINT);
        storageRecordCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.STORAGE_RECORD_CLOUD_END_POINT);


        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Note selectedNote = dataSnapshot.getValue(Note.class);
                try {
                    uploadFromUri(selectedNote);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Geting passed in note failed " + databaseError.getMessage());

            }
        };

        // [END get_storage_ref]
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);
        if (ACTION_UPLOAD.equals(intent.getAction())) {
            String noteId = intent.getStringExtra(NOTE_ID);
            noteCloudReference.child(noteId).addListenerForSingleValueEvent(mValueEventListener);
        }

        return START_REDELIVER_INTENT;
    }

    // [START upload_from_uri]
    private void uploadFromUri(final Note note) throws IOException {

        if (note != null && note.getAttachments().size() > 0) {
            taskStarted();
            showProgressNotification(getString(R.string.progress_uploading), 0, 0);

            final List<Attachment> updatedListOfAttachment = new ArrayList<>();

            for (final Attachment attachment : note.getAttachments()) {

                //If the attachment has already been uploaded to cloud
                //Skip
                if (!TextUtils.isEmpty(attachment.getCloudFilePath())){
                    continue;
                }


                // Upload file to Firebase Storage
                final Uri fileUri = Uri.parse(attachment.getUri());
                final String fileName = fileUri.getLastPathSegment();


                final UploadTask uploadTask;

                if (attachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)) {
                    //Compress Image
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                    byte[] data = byteArrayOutputStream.toByteArray();
                    uploadTask = mAttachmentStorageReference.child(fileName).putBytes(data);
                } else {
                    //Upload File
                    uploadTask = mAttachmentStorageReference.child(fileName).putFile(fileUri);

                }

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        showProgressNotification(getString(R.string.progress_uploading),
                                taskSnapshot.getBytesTransferred(),
                                taskSnapshot.getTotalByteCount());
                    }
                })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Upload succeeded
                                Log.d(TAG, "uploadFromUri:onSuccess");

                                // Get the public download URL
                                Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                                long size = taskSnapshot.getMetadata().getSizeBytes();
                                String downloadUrl = downloadUri.toString();

                                //Add the download URL to the attachment
                                attachment.setCloudFilePath(downloadUrl);
                                noteCloudReference.child(note.getId()).setValue(note);


                                //Create a Storage Record in Firebase
                                if (mFirebaseUser != null) {
                                    //Create a StorageRecord
                                    StorageRecord record = new StorageRecord();
                                    record.setDownloadUri(downloadUrl);
                                    record.setUid(mFirebaseUser.getUid());
                                    record.setFileSizes(size);
                                    String key = storageRecordCloudReference.push().getKey();
                                    record.setId(key);
                                    storageRecordCloudReference.child(key).setValue(record);
                                }


                                showUploadFinishedNotification(downloadUri, fileUri);
                                taskCompleted();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Upload failed
                                Log.w(TAG, "uploadFromUri:onFailure", exception);

                            }
                        });
            }

        }


    }


    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        // Hide the progress notification
        dismissProgressNotification();

        // Make Intent to MainActivity
        Intent intent = new Intent(this, NoteListActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;
        String caption = success ? getString(R.string.upload_success) : getString(R.string.upload_failure);
        showFinishedNotification(caption, intent, success);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPLOAD_COMPLETED);
        filter.addAction(UPLOAD_ERROR);

        return filter;
    }
}

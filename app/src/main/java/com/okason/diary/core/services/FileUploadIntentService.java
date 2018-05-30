package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okason.diary.data.NoteDao;
import com.okason.diary.models.Attachment;
import com.okason.diary.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import io.realm.Realm;


public class FileUploadIntentService extends IntentService {

    private final static String TAG = "FileUploadIntentService";
    private StorageReference firebaseStorageReference;
    private StorageReference attachmentReference;
    private Realm realm;
    private NoteDao noteDao;


    private String noteId;
    private String attachmentId;
    private Attachment attachment;



    public FileUploadIntentService() {
        super("FileUploadIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            noteId = intent.getStringExtra(Constants.JOURNAL_ID);
            attachmentId = intent.getStringExtra(Constants.ATTACHMENT_ID);


            firebaseStorageReference = FirebaseStorage.getInstance().getReference();
            attachmentReference = firebaseStorageReference.child("attachments");
            realm = Realm.getDefaultInstance();
            noteDao = new NoteDao(realm);

            attachment = noteDao.getAttachmentById(attachmentId);

            if (attachment != null){
                try {
                    uploadAttachment(attachment);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadAttachment(Attachment attachment) throws IOException {

        if (TextUtils.isEmpty(attachment.getCloudFilePath())){
            //upload this attachment to cloud

            String filePath = attachment.getLocalFilePath();
            String fileType = attachment.getMime_type();
            Uri fileToUpload = Uri.fromFile(new File(filePath));

            final String fileName = fileToUpload.getLastPathSegment();

            final UploadTask uploadTask;


            if (fileType.equals(Constants.MIME_TYPE_IMAGE)) {
                //Compress Image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), fileToUpload);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                uploadTask = attachmentReference.child(fileToUpload.getLastPathSegment()).putBytes(data);
            } else {
                //Upload File
                uploadTask = attachmentReference.child(fileToUpload.getLastPathSegment()).putFile(fileToUpload);

            }


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //makeToast("Unable to upload file to cloud" + e.getLocalizedMessage());
                    Log.d(TAG, "File upload failed " + e.getLocalizedMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    long size = taskSnapshot.getMetadata().getSizeBytes();
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    if (!TextUtils.isEmpty(downloadUrl)){
                        try (Realm realm = Realm.getDefaultInstance()){
                            new NoteDao(realm).updateAttachmentUrl(attachmentId, downloadUrl);
                        }
                    }

                }
            });

        }


    }


}

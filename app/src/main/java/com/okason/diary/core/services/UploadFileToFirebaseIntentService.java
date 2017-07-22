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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.StorageRecord;
import com.okason.diary.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.realm.Realm;

public class UploadFileToFirebaseIntentService extends IntentService {

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;
    private StorageReference mAttachmentStorageReference;
    private DatabaseReference storageRecordCloudReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;



    public UploadFileToFirebaseIntentService() {
        super("UploadFileToFirebaseIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();


        if (intent != null && intent.hasExtra(Constants.ATTACHMENT_ID) && mFirebaseUser != null){

            mFirebaseStorage = FirebaseStorage.getInstance();
            mFirebaseStorageReference = mFirebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_BUCKET);
            mAttachmentStorageReference = mFirebaseStorageReference.child("attachments/" + mFirebaseUser.getUid());

            mDatabase = FirebaseDatabase.getInstance().getReference();
            storageRecordCloudReference = mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.STORAGE_RECORD_CLOUD_END_POINT);

            final String attachmentId = intent.getStringExtra(Constants.ATTACHMENT_ID);

            try (Realm realm = Realm.getDefaultInstance()){
                if (!TextUtils.isEmpty(attachmentId)){
                    final Attachment savedAttachment = realm.where(Attachment.class).equalTo("id", attachmentId).findFirst();

                    if (savedAttachment != null){
                        Uri fileUri = Uri.parse(savedAttachment.getUri());

                        StorageReference attachmentRef = mAttachmentStorageReference.child(fileUri.getLastPathSegment());
                        final UploadTask uploadTask;

                        if (savedAttachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)) {
                            //Compress Image
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                            byte[] data = byteArrayOutputStream.toByteArray();
                            uploadTask = mAttachmentStorageReference.child(fileUri.getLastPathSegment()).putBytes(data);
                        } else {
                            //Upload File
                            uploadTask = mAttachmentStorageReference.child(fileUri.getLastPathSegment()).putFile(fileUri);

                        }

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the public download URL
                                @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                                @SuppressWarnings("VisibleForTests") final float size = taskSnapshot.getMetadata().getSizeBytes();
                                final String downloadUrl = downloadUri.toString();

                                try(Realm innerRealm = Realm.getDefaultInstance()){
                                    innerRealm.beginTransaction();
                                    Attachment updatedAttachment = innerRealm.where(Attachment.class).equalTo("id", attachmentId).findFirst();
                                    updatedAttachment.setCloudFilePath(downloadUrl);
                                    innerRealm.commitTransaction();
                                }


                                StorageRecord record = new StorageRecord();
                                record.setDownloadUri(downloadUrl);
                                record.setUid(mFirebaseUser.getUid());
                                record.setFileSizes((long) size);
                                String key = storageRecordCloudReference.push().getKey();
                                record.setId(key);
                                storageRecordCloudReference.child(key).setValue(record);

                            }
                        });

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Attachment ", e.getLocalizedMessage());

                            }
                        });

                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }





    }
}

package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.okason.diary.BuildConfig;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Journal;
import com.okason.diary.ui.addnote.DataAccessManager;
import com.okason.diary.utils.Constants;

import java.io.File;
import java.util.List;


/**
 * When a user creates an file in Device A, that file gets uploaded to
 * Firebase Storage so that it will become available in on Device B, etc
 * This Intent Services downloads that file Cloud so that subsequently
 * It will be served from local device and not from the cloud
 */
public class FileDownloadIntentService extends IntentService {

    private StorageReference firebaseStorageReference;
    private StorageReference attachmentReference;
    private DataAccessManager dataAccessManager;

    private final static String TAG = "FileDownloadService";

    public FileDownloadIntentService() {
        super("FileDownloadIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Get the passed in Journal Id
            String journalId = intent.getStringExtra(Constants.JOURNAL_ID);
            firebaseStorageReference = FirebaseStorage.getInstance().getReference();
            attachmentReference = firebaseStorageReference.child("attachments");

            if (!TextUtils.isEmpty(journalId)){
                dataAccessManager = new DataAccessManager(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (dataAccessManager != null){
                    dataAccessManager.getJournalCloudPath().document(journalId)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                final Journal journal = documentSnapshot.toObject(Journal.class);
                                if (journal != null && journal.getAttachments().size() > 0) {
                                    final List<Attachment> attachmentList = journal.getAttachments();
                                    for (int i = 0; i < attachmentList.size(); i ++){
                                        final Attachment attachment =  attachmentList.get(i);

                                        String fileName = attachment.getCloudFilePath();
                                        if (!TextUtils.isEmpty(fileName)){

                                            File rootPath = new File(
                                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Pronto Diary");
                                            if(!rootPath.exists()) {
                                                rootPath.mkdirs();
                                            }

                                            final File localFile = new File(rootPath, fileName);

                                            if (localFile != null){
                                                StorageReference cloudImageRef = attachmentReference.child(fileName);
                                                cloudImageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        //File has been successfully downloaded, now save the local path
                                                        Uri fileUri = FileProvider.getUriForFile(getApplicationContext(),
                                                                BuildConfig.APPLICATION_ID + ".provider",
                                                                localFile);
                                                        attachment.setUri(fileUri.getPath());


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "Error downloading image " + e.getLocalizedMessage());
                                                    }
                                                });

                                            }

                                        }

                                    }
                                    journal.setAttachments(attachmentList);
                                    dataAccessManager.getJournalCloudPath().document(journal.getId()).set(journal);

                                }
                            }

                        }
                    });
                }
            }


        }
    }




}

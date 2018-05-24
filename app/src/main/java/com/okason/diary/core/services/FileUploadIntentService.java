package com.okason.diary.core.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.storage.StorageReference;


public class FileUploadIntentService extends IntentService {

    private final static String TAG = "FileUploadIntentService";
    private StorageReference firebaseStorageReference;
    private StorageReference attachmentReference;



    public FileUploadIntentService() {
        super("FileUploadIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
//        if (intent != null) {
//            final String noteId = intent.getStringExtra(Constants.JOURNAL_ID);
//            firebaseStorageReference = FirebaseStorage.getInstance().getReference();
//            attachmentReference = firebaseStorageReference.child("attachments");
//
//            if (!TextUtils.isEmpty(noteId)){
//                dataAccessManager = new DataAccessManager(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                if (dataAccessManager != null){
//                    dataAccessManager.getJournalCloudPath().document(noteId)
//                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            if (documentSnapshot.exists()){
//                                Journal journal = documentSnapshot.toObject(Journal.class);
//                                if (journal != null){
//                                    try {
//                                        uploadAttachments(journal);
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//
//                        }
//
//                        private void uploadAttachments(final Journal journal) throws IOException {
//                            final List<Attachment> attachmentList = journal.getAttachments();
//                            for (final Attachment attachment: attachmentList){
//                                if (TextUtils.isEmpty(attachment.getCloudFilePath())){
//                                    //upload this attachment to cloud
//
//                                    String filePath = attachment.getLocalFilePath();
//                                    String fileType = attachment.getMime_type();
//                                    final long[] size = new long[1];
//
//
//                                    Uri fileToUpload = Uri.fromFile(new File(filePath));
//
//                                    final String fileName = fileToUpload.getLastPathSegment();
//
//                                    final UploadTask uploadTask;
//
//
//                                    if (fileType.equals(Constants.MIME_TYPE_IMAGE)) {
//                                        //Compress Image
//                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), fileToUpload);
//                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
//                                        byte[] data = byteArrayOutputStream.toByteArray();
//                                        uploadTask = firebaseStorageReference.child(fileToUpload.getLastPathSegment()).putBytes(data);
//                                    } else {
//                                        //Upload File
//                                        uploadTask = firebaseStorageReference.child(fileToUpload.getLastPathSegment()).putFile(fileToUpload);
//
//                                    }
//
//
//                                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            //makeToast("Unable to upload file to cloud" + e.getLocalizedMessage());
//                                            Log.d(TAG, "File upload failed " + e.getLocalizedMessage());
//                                        }
//                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                        @SuppressWarnings("VisibleForTests")
//                                        @Override
//                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                            long size = taskSnapshot.getMetadata().getSizeBytes();
//                                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
//                                            for (int i=0; i<attachmentList.size(); i++){
//                                                Attachment item = attachmentList.get(i);
//                                                if (item.getLocalFilePath().equals(attachment.getLocalFilePath())){
//                                                    attachmentList.get(i).setCloudFilePath(downloadUrl);
//                                                    break;
//                                                }
//                                                journal.setAttachments(attachmentList);
//                                                dataAccessManager.getJournalCloudPath().document(journal.getId()).set(journal);
//                                            }
//                                        }
//                                    });
//
//                                }
//                            }
//
//
//                        }
//                    });
//                }
//            }
//
//        }
    }


}

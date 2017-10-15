package com.okason.diary.ui.addnote;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okason.diary.NoteListActivity;
import com.okason.diary.core.events.FolderListChangeEvent;
import com.okason.diary.core.events.JournalListChangeEvent;
import com.okason.diary.core.events.TagListChangeEvent;
import com.okason.diary.models.Attachment;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.models.SampleData;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by valokafor on 10/13/17.
 */

public class DataAccessManager {
    private final String userId;
    private FirebaseFirestore database;
    private final static String TAG = "DataAccessManager";


    private CollectionReference journalCloudReference;
    private CollectionReference folderCloudReference;
    private CollectionReference tagCloudReference;
    private CollectionReference taskCloudReference;
    private StorageReference firebaseStorageReference;
    private StorageReference attachmentReference;

    public DataAccessManager(String userId) {
        this.userId = userId;
        database = FirebaseFirestore.getInstance();


        journalCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.NOTE_CLOUD_END_POINT);
        folderCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.FOLDER_CLOUD_END_POINT);
        tagCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TAG_CLOUD_END_POINT);
        taskCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TASK_CLOUD_END_POINT);

        String path = journalCloudReference.getPath();
        Log.d(NoteListActivity.TAG, "Cloud Journal Path: " + path);


        firebaseStorageReference = FirebaseStorage.getInstance().getReference();
        attachmentReference = firebaseStorageReference.child("attachments");
    }

    public CollectionReference getTagCloudPath() {
        return tagCloudReference;
    }

    public CollectionReference getJournalCloudPath() {
        return journalCloudReference;
    }

    public CollectionReference getTaskPath() {
        return taskCloudReference;
    }

    public CollectionReference getFolderPath() {
        return folderCloudReference;
    }




    public void deleteNote(String noteId) {
//        final Intent deleteNoteIntent = new Intent(getContext(), HandleNoteDeleteIntentService.class);
//        deleteNoteIntent.putExtra(Constants.JOURNAL_ID, note.getId());
//        journalCloudReference.child(note.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    getActivity().startService(deleteNoteIntent);
//                }else {
//                    makeToast("Unable to delete Note");
//                }
//            }
//        });
        journalCloudReference.document(noteId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getAllJournal("");
                }
            }
        });
    }


    public void getAllJournal(String tagName) {
        final List<Note> journals = new ArrayList<>();
        try {

            Query query;
            if (!TextUtils.isEmpty(tagName)) {
                query = journalCloudReference.whereEqualTo(tagName, true);
            } else {
                query = journalCloudReference;
            }
            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Note journal = snapshot.toObject(Note.class);
                                    if (journal != null) {
                                        journals.add(journal);
                                    }
                                }
                                EventBus.getDefault().post(new JournalListChangeEvent(journals));

                            } else {
                                EventBus.getDefault().post(new JournalListChangeEvent(journals));
                            }
                        }
                    });
        } catch (Exception e) {
            EventBus.getDefault().post(new JournalListChangeEvent(journals));
            Log.d(TAG, "Data access failure: " + e.getLocalizedMessage());
        }

    }

    public void getAllFolder() {
        final List<Folder> folders = new ArrayList<>();
        try {
            folderCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            Folder folder = snapshot.toObject(Folder.class);
                            if (folder != null) {
                                folders.add(folder);
                            }
                        }
                        EventBus.getDefault().post(new FolderListChangeEvent(folders));

                    } else {
                        EventBus.getDefault().post(new FolderListChangeEvent(folders));
                    }
                }
            });
        } catch (Exception e) {
            EventBus.getDefault().post(new FolderListChangeEvent(folders));
            Log.d(TAG, "Data access failure: " + e.getLocalizedMessage());
        }

    }

    public void getAllTags() {
        final List<Tag> tags = new ArrayList<>();
        try {
            tagCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            Tag tag = snapshot.toObject(Tag.class);
                            if (tag != null) {
                                tags.add(tag);
                            }
                        }
                        EventBus.getDefault().post(new TagListChangeEvent(tags));

                    } else {
                        EventBus.getDefault().post(new TagListChangeEvent(tags));
                    }
                }
            });
        } catch (Exception e) {
            EventBus.getDefault().post(new TagListChangeEvent(tags));
            Log.d(TAG, "Data access failure: " + e.getLocalizedMessage());
        }

    }



    public void uploadFileToCloud(final Attachment attachment) throws IOException {
        String filePath = attachment.getLocalFilePath();
        String fileType = attachment.getMime_type();
        final long[] size = new long[1];

//        final StorageMetadata metadata = new StorageMetadata.Builder()
//                .setContentType(fileType)
//                .build();

        Uri fileToUpload = Uri.fromFile(new File(filePath));

        final String fileName = fileToUpload.getLastPathSegment();

        StorageReference imageRef = firebaseStorageReference.child(fileName);
        final UploadTask uploadTask;

//
//        if (attachment.getMime_type().equals(Constants.MIME_TYPE_IMAGE)) {
//            //Compress Image
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileToUpload);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
//            byte[] data = byteArrayOutputStream.toByteArray();
//            uploadTask = firebaseStorageReference.child(fileToUpload.getLastPathSegment()).putBytes(data);
//        } else {
//            //Upload File
//            uploadTask = firebaseStorageReference.child(fileToUpload.getLastPathSegment()).putFile(fileToUpload);
//
//        }

//
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                makeToast("Unable to upload file to cloud" + e.getLocalizedMessage());
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @SuppressWarnings("VisibleForTests")
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                long size = taskSnapshot.getMetadata().getSizeBytes();
//                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
//                for (int i=0; i<attachmentList.size(); i++){
//                    Attachment item = attachmentList.get(i);
//                    if (item.getLocalFilePath().equals(attachment.getLocalFilePath())){
//                        attachmentList.get(i).setCloudFilePath(downloadUrl);
//                        break;
//                    }
//                }
//            }
//        });

    }

    public void addInitialNotesToFirebase() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<Folder> folders = new ArrayList<>();
        final List<com.okason.diary.models.Tag> tags = new ArrayList<>();

        List<String> sampleFolderNames = SampleData.getSampleCategories();
        for (String name : sampleFolderNames) {

            final Folder folder = new Folder();
            folder.setFolderName(name);
            folderCloudReference.add(folder).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String key = documentReference.getId();
                    folder.setId(key);
                    folderCloudReference.document(key).set(folder);
                    folders.add(folder);

                }
            });

            folders.add(folder);
        }

        List<String> sampleTagNames = SampleData.getSampleTags();
        for (String name : sampleTagNames) {

            final Tag tag = new Tag();
            tag.setTagName(name);
            tagCloudReference.add(tag).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String key = documentReference.getId();
                    tag.setId(key);
                    tagCloudReference.document(key).set(tag);


                }
            });

            tags.add(tag);
        }


        List<Note> sampleNotes = SampleData.getSampleNotes();
        for (int i = 0; i < sampleNotes.size(); i++) {

            final Note note = sampleNotes.get(i);
            Folder selectedFolder = folders.get(i);
            note.setFolder(selectedFolder);


            Tag selectedTag = tags.get(i);

            Map<String, Boolean> addedTags = new HashMap<>();
            addedTags.put(selectedTag.getTagName(), true);
            note.setTags(addedTags);

            journalCloudReference.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String key = documentReference.getId();
                    note.setId(key);
                    journalCloudReference.document(key).set(note);
                }
            });


        }


    }

    public void deleteFolder(String id) {

    }

    public void deleteTag(String id) {

    }


}

package com.okason.diary.ui.addnote;

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
import com.okason.diary.NoteListActivity;
import com.okason.diary.core.events.FolderListChangeEvent;
import com.okason.diary.core.events.JournalListChangeEvent;
import com.okason.diary.core.events.TagListChangeEvent;
import com.okason.diary.core.events.TaskChangedEvent;
import com.okason.diary.core.events.TaskListChangeEvent;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Journal;
import com.okason.diary.models.SampleData;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

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

    public DataAccessManager(String userId) {
        this.userId = userId;
        database = FirebaseFirestore.getInstance();


        journalCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.NOTE_CLOUD_END_POINT);
        folderCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.FOLDER_CLOUD_END_POINT);
        tagCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TAG_CLOUD_END_POINT);
        taskCloudReference = database.collection(Constants.USERS_CLOUD_END_POINT).document(userId).collection(Constants.TASK_CLOUD_END_POINT);

        String path = journalCloudReference.getPath();
        Log.d(NoteListActivity.TAG, "Cloud Journal Path: " + path);

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
//                    makeToast("Unable to delete Journal");
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
        final List<Journal> journals = new ArrayList<>();
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
                                    Journal journal = snapshot.toObject(Journal.class);
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

    public void getAllTasks() {
        final List<com.okason.diary.models.Task> tasks = new ArrayList<>();
        try {
            taskCloudReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            com.okason.diary.models.Task item = snapshot.toObject(com.okason.diary.models.Task.class);
                            if (item != null) {
                                tasks.add(item);
                            }
                        }
                        EventBus.getDefault().post(new TaskListChangeEvent(tasks));

                    } else {
                        EventBus.getDefault().post(new TaskListChangeEvent(tasks));
                    }
                }
            });
        } catch (Exception e) {
            EventBus.getDefault().post(new TaskListChangeEvent(tasks));
            Log.d(TAG, "Data access failure: " + e.getLocalizedMessage());
        }

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


        List<Journal> sampleJournals = SampleData.getSampleNotes();
        for (int i = 0; i < sampleJournals.size(); i++) {

            final Journal journal = sampleJournals.get(i);
            Folder selectedFolder = folders.get(i);
            journal.setFolder(selectedFolder);


            Tag selectedTag = tags.get(i);

            Map<String, Boolean> addedTags = new HashMap<>();
            addedTags.put(selectedTag.getTagName(), true);
            journal.setTags(addedTags);

            journalCloudReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String key = documentReference.getId();
                    journal.setId(key);
                    journalCloudReference.document(key).set(journal);
                }
            });


        }


    }

    public void deleteFolder(String folderId) {

        folderCloudReference.document(folderId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getAllFolder();
                }
            }
        });

    }

    public void deleteTag(String tagId) {
        tagCloudReference.document(tagId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getAllTags();
                }
            }
        });

    }




    public void addSubTask(final com.okason.diary.models.Task parentTask, String subTaskText) {
        SubTask subTask = new SubTask();
        subTask.setTitle(subTaskText);
        subTask.setDateCreated(System.currentTimeMillis());
        subTask.setDateModified(System.currentTimeMillis());
        subTask.setChecked(false);
        subTask.setParentTaskName(parentTask.getTitle());
        parentTask.getSubTask().add(subTask);
        taskCloudReference.document(parentTask.getId()).set(parentTask)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EventBus.getDefault().post(new TaskChangedEvent(parentTask));
                    }
                });
    }

    public void onMarkSubTaskAsComplete(com.okason.diary.models.Task parentTask, String subTaskTitle) {
        for (SubTask subTask: parentTask.getSubTask()){
            if (subTask.getTitle().equals(subTaskTitle)){
                subTask.setChecked(true);
                break;
            }
        }
        taskCloudReference.document(parentTask.getId()).set(parentTask);
    }

    public void onMarkSubTaskAsInComplete(com.okason.diary.models.Task parentTask, String subTaskTitle) {
        for (SubTask subTask: parentTask.getSubTask()){
            if (subTask.getTitle().equals(subTaskTitle)){
                subTask.setChecked(false);
                break;
            }
        }
        taskCloudReference.document(parentTask.getId()).set(parentTask);

    }

    public void deleteSubTask(final com.okason.diary.models.Task parentTask, String subTaskTitle) {
        for (int i = 0; i < parentTask.getSubTask().size(); i++){
            SubTask subTask = parentTask.getSubTask().get(i);
            if (subTask.getTitle().equals(subTaskTitle)){
                parentTask.getSubTask().remove(i);
                break;
            }
        }
        taskCloudReference.document(parentTask.getId()).set(parentTask)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EventBus.getDefault().post(new TaskChangedEvent(parentTask));
                    }
                });
    }

    public void deleteTask(com.okason.diary.models.Task clickedTask) {
        taskCloudReference.document(clickedTask.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getAllTasks();
                }
            }
        });

    }
}

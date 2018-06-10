package com.okason.diary.data;

import android.content.Intent;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.services.DataUploadIntentService;
import com.okason.diary.models.Folder;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.Reminder;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.dto.ProntoTaskDto;
import com.okason.diary.models.dto.SubTaskDto;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 5/21/18.
 */

public class TaskDao {
    private final Realm realm;

    public TaskDao(Realm realm) {
        this.realm = realm;
    }

    public ProntoTask createNewTask() {
        String taskId = UUID.randomUUID().toString();
        realm.beginTransaction();
        ProntoTask prontoTask = realm.createObject(ProntoTask.class, taskId);
        prontoTask.setDateCreated(System.currentTimeMillis());
        prontoTask.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return prontoTask;
    }

    public SubTask createNewSubTask(String subTaskName, String parentTaskId) {
        SubTask subTask = null;
        ProntoTask parentProntoTask = realm.where(ProntoTask.class).equalTo("id", parentTaskId).findFirst();
        if (parentProntoTask != null){
            realm.beginTransaction();
            String subTaskId = UUID.randomUUID().toString();
            subTask = realm.createObject(SubTask.class, subTaskId);
            subTask.setDateCreated(System.currentTimeMillis());
            subTask.setDateModified(System.currentTimeMillis());
            subTask.setTitle(subTaskName);
            subTask.setChecked(false);
            subTask.setTask(parentProntoTask);
            parentProntoTask.getSubTask().add(subTask);

            realm.commitTransaction();
        }
        return subTask;
    }




    public void updateTaskStatus(final ProntoTask prontoTask, final boolean completed) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    ProntoTask updatedProntoTask = backgroundRealm.where(ProntoTask.class).equalTo("id", prontoTask.getId()).findFirst();
                    if (updatedProntoTask != null) {
                        updatedProntoTask.setChecked(completed);
                        updatedProntoTask.setDateModified(System.currentTimeMillis());
                        for (SubTask subTask: updatedProntoTask.getSubTask()){
                            subTask.setChecked(completed);
                        }
                    }

                    //Send Intent to update Firestore data
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.TASK_ID, prontoTask.getId());
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            });
        }

    }

    public void updateSubTaskStatus(final String taskId, final String subTaskId, final boolean completed) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                ProntoTask updatedProntoTask = backgroundRealm.where(ProntoTask.class).equalTo("id", taskId).findFirst();
                SubTask updatedSubTask = null;
                for (SubTask subTask: updatedProntoTask.getSubTask()){
                    if (subTask.getId().equals(subTaskId)){
                        updatedSubTask = subTask;
                        break;
                    }
                }
                if (updatedSubTask != null){
                    updatedSubTask.setChecked(completed);
                    updatedProntoTask.setDateModified(System.currentTimeMillis());

                    //Send Intent to update Firestore data
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.TASK_ID, taskId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);

                }
            }
        });

    }

    public RealmResults<ProntoTask> getAllTask() {
        RealmResults<ProntoTask> prontoTaskResult = realm.where(ProntoTask.class).findAll();
        return prontoTaskResult;

    }

    public void deleteTask(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(ProntoTask.class).equalTo("id", taskId).findFirst().deleteFromRealm();

                Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                intent.putExtra(Constants.DELETE_EVENT, true);
                intent.putExtra(Constants.DELETE_EVENT_TYPE, Constants.TODO_LIST);
                intent.putExtra(Constants.ITEM_ID, taskId);
                DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
            }
        });
    }

    public void deleteSubTask(final String subTaskId, String parentId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(SubTask.class).equalTo("id", subTaskId).findFirst().deleteFromRealm();

                //Send Intent to update Firestore data
                Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                intent.putExtra(Constants.TASK_ID, parentId);
                DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
            }
        });
    }

    public ProntoTask getTaskById(String taskId) {
        try {
            ProntoTask selectedNoteEntity = realm.where(ProntoTask.class).equalTo("id", taskId).findFirst();
            return selectedNoteEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public void setFolder(String taskId, String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                ProntoTask prontoTask = backgroundRealm.where(ProntoTask.class).equalTo("id", taskId).findFirst();
                Folder folder = backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();
                if (prontoTask != null && folder != null) {
                    prontoTask.setFolder(folder);
                    folder.getTasks().add(prontoTask);
                    prontoTask.setDateModified(System.currentTimeMillis());
                }
            }
        });
    }


    public RealmResults<ProntoTask> getAllTasksForPriority(int priority) {
        RealmResults<ProntoTask> prontoTaskResult = realm.where(ProntoTask.class).equalTo("priority", priority).findAll();
        return prontoTaskResult;
    }

    public RealmResults<ProntoTask> searchTasks(String query) {
        RealmResults<ProntoTask> results = realm.where(ProntoTask.class).contains("title", query, Case.INSENSITIVE).findAll();
        return results;
    }

    public List<ProntoTask> filterTasks(String query) {
        List<ProntoTask> prontoTaskList = new ArrayList<>();
        for (ProntoTask prontoTask : getAllTask()){
            String title = prontoTask.getTitle().toLowerCase();
            query = query.toLowerCase();
            if (title.contains(query)){
                prontoTaskList.add(prontoTask);
            }else {
                for (SubTask subTask: prontoTask.getSubTask()){
                    String subTasktitle = subTask.getTitle().toLowerCase();
                    if (subTasktitle.contains(query)){
                        prontoTaskList.add(prontoTask);
                        break;
                    }
                }
            }
        }
        return prontoTaskList;
    }

    public void updateTask(String taskId, String taskName, String description, int priority, String folderId) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                ProntoTask updatedProntoTask = backgroundRealm.where(ProntoTask.class).equalTo("id", taskId).findFirst();
                Folder selectedFolder = backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();
                if (updatedProntoTask != null) {
                    updatedProntoTask.setTitle(taskName);
                    updatedProntoTask.setDescription(description);
                    updatedProntoTask.setDateModified(System.currentTimeMillis());
                    updatedProntoTask.setPriority(priority);
                    if (selectedFolder != null) {
                        updatedProntoTask.setFolder(selectedFolder);
                    }

                    //Send Intent to update Firestore data
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.TASK_ID, taskId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            }
        });

    }

    public void addReminder(String taskId, int reminderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                ProntoTask prontoTask = backgroundRealm.where(ProntoTask.class).equalTo("id", taskId).findFirst();
                Reminder reminder = backgroundRealm.where(Reminder.class).equalTo("id", reminderId).findFirst();
                if (prontoTask != null && reminder != null) {
                    prontoTask.setReminder(reminder);
                    reminder.setParentProntoTask(prontoTask);


                    //Send Intent to update Firestore data
                    Intent intent = new Intent(ProntoDiaryApplication.getAppContext(), DataUploadIntentService.class);
                    intent.putExtra(Constants.TASK_ID, taskId);
                    DataUploadIntentService.enqueueWork(ProntoDiaryApplication.getAppContext(), intent);
                }
            }
        });
    }

    public void addTaskFromCloud(ProntoTaskDto dto) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                ProntoTask prontoTask = getTaskById(dto.getId());

                if (prontoTask != null && !TimeUtils.isCloudModifiedDateAfterLocalModifiedDate(dto.getDateModified(),
                        prontoTask.getDateModified())){
                    return;
                }


                if (prontoTask == null) {

                    String taskId = UUID.randomUUID().toString();
                    prontoTask = realm.createObject(ProntoTask.class, taskId);
                }


                prontoTask.setDateCreated(dto.getDateCreated());
                prontoTask.setDateModified(dto.getDateModified());
                prontoTask.setTitle(dto.getTitle());
                if (dto.getFolder() != null){
                    Folder folder = realm.where(Folder.class).equalTo("folderName", dto.getFolder().getFolderName(), Case.INSENSITIVE).findFirst();
                    if (folder == null){
                        String id = UUID.randomUUID().toString();
                        folder = realm.createObject(Folder.class, id);
                        folder.setDateCreated(System.currentTimeMillis());
                        folder.setDateModified(System.currentTimeMillis());
                        folder.setFolderName(dto.getFolder().getFolderName());
                    }

                    if (folder != null){
                        prontoTask.setFolder(folder);
                        folder.getTasks().add(prontoTask);
                    }
                }
                if (dto.getSubTask() != null){
                    List<SubTaskDto> subTasks = dto.getSubTask();
                    if (subTasks.size() > 0){
                        for(SubTaskDto subTaskDto: subTasks){
                            String subTaskId = subTaskDto.getId();
                            SubTask subTask = new SubTask();
                            subTask.setId(subTaskId);
                            subTask.setDateCreated(System.currentTimeMillis());
                            subTask.setDateModified(System.currentTimeMillis());
                            subTask.setTitle(subTaskDto.getTitle());
                            subTask.setChecked(false);
                            subTask.setTask(prontoTask);
                            realm.copyToRealmOrUpdate(subTask);
                            prontoTask.getSubTask().add(subTask);
                        }
                    }

                }

            }
        });
    }
}

package com.okason.diary.data;

import com.okason.diary.models.realmentities.FolderEntity;
import com.okason.diary.models.realmentities.SubTaskEntity;
import com.okason.diary.models.realmentities.TaskEntity;

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

    public TaskEntity createNewTask(String taskName) {
        String taskId = UUID.randomUUID().toString();
        realm.beginTransaction();
        TaskEntity task = realm.createObject(TaskEntity.class, taskId);
        task.setDateCreated(System.currentTimeMillis());
        task.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return task;
    }

    public SubTaskEntity createNewSubTask(String subTaskName, String parentTaskId) {
        SubTaskEntity subTask = null;
        TaskEntity parentTask = realm.where(TaskEntity.class).equalTo("id", parentTaskId).findFirst();
        if (parentTask != null){
            realm.beginTransaction();
            String subTaskId = UUID.randomUUID().toString();
            subTask = realm.createObject(SubTaskEntity.class, subTaskId);
            subTask.setDateCreated(System.currentTimeMillis());
            subTask.setDateModified(System.currentTimeMillis());
            subTask.setTitle(subTaskName);
            subTask.setChecked(false);
            subTask.setParentTask(parentTask);
            parentTask.getSubTask().add(subTask);

            realm.commitTransaction();
        }
        return subTask;
    }

    public TaskEntity createNewTask(int priority, String taskName, long dueDateAndTime,  String repeat, long repeatEndDate, String folderId) {
        realm.beginTransaction();
        TaskEntity task = createNewTask(taskName);
        task.setDateModified(System.currentTimeMillis());
        task.setPriority(priority);
        task.setDueDateAndTime(dueDateAndTime);
        task.setRepeatEndDate(repeatEndDate);
        task.setRepeatFrequency(repeat);

        FolderEntity selectedFolder = realm.where(FolderEntity.class).equalTo("id", folderId).findFirst();
        if (selectedFolder != null){
            task.setFolder(selectedFolder);
            selectedFolder.getTodoLists().add(task);
        }

        realm.commitTransaction();

        return task;
    }

    public void updateTask(String taskId, int priority, String taskName, long dueDateAndTime, String repeat, long repeatEndDate) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                TaskEntity updatedTask = realm.where(TaskEntity.class).equalTo("id", taskId).findFirst();
                if (updatedTask != null) {
                    updatedTask.setTitle(taskName);
                    updatedTask.setDateModified(System.currentTimeMillis());
                    updatedTask.setPriority(priority);
                    updatedTask.setDueDateAndTime(dueDateAndTime);
                    updatedTask.setRepeatEndDate(repeatEndDate);
                    updatedTask.setRepeatFrequency(repeat);
                }
            }
        });

    }

    public void updateTaskStatus(final TaskEntity task, final boolean completed) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    TaskEntity updatedTask = backgroundRealm.where(TaskEntity.class).equalTo("id", task.getId()).findFirst();
                    if (updatedTask != null) {
                        updatedTask.setChecked(completed);
                        updatedTask.setDateModified(System.currentTimeMillis());
                        for (SubTaskEntity subTask: updatedTask.getSubTask()){
                            subTask.setChecked(completed);
                        }
                    }
                }
            });
        }

    }

    public void updateSubTaskStatus(final String taskId, final String subTaskId, final boolean completed) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                TaskEntity updatedTask = backgroundRealm.where(TaskEntity.class).equalTo("id", taskId).findFirst();
                SubTaskEntity updatedSubTask = null;
                for (SubTaskEntity subTask: updatedTask.getSubTask()){
                    if (subTask.getId().equals(subTaskId)){
                        updatedSubTask = subTask;
                        break;
                    }
                }
                if (updatedSubTask != null){
                    updatedSubTask.setChecked(completed);
                    updatedTask.setDateModified(System.currentTimeMillis());
                }
            }
        });

    }

    public RealmResults<TaskEntity> getAllTask() {
        RealmResults<TaskEntity> taskResult = realm.where(TaskEntity.class).findAll();
        return taskResult;

    }

    public void deleteTask(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(TaskEntity.class).equalTo("id", taskId).findFirst().deleteFromRealm();
            }
        });
    }

    public void deleteSubTask(final String subTaskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(SubTaskEntity.class).equalTo("id", subTaskId).findFirst().deleteFromRealm();
            }
        });
    }

    public TaskEntity getTaskById(String taskId) {
        try {
            TaskEntity selectedNoteEntity = realm.where(TaskEntity.class).equalTo("id", taskId).findFirst();
            return selectedNoteEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public void setFolder(String taskId, String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                TaskEntity task = backgroundRealm.where(TaskEntity.class).equalTo("id", taskId).findFirst();
                FolderEntity folder = backgroundRealm.where(FolderEntity.class).equalTo("id", folderId).findFirst();
                if (task != null && folder != null) {
                    task.setFolder(folder);
                    folder.getTodoLists().add(task);
                    task.setDateModified(System.currentTimeMillis());
                }
            }
        });
    }


    public RealmResults<TaskEntity> getAllTasksForPriority(int priority) {
        RealmResults<TaskEntity> taskResult = realm.where(TaskEntity.class).equalTo("priority", priority).findAll();
        return taskResult;
    }

    public RealmResults<TaskEntity> searchTasks(String query) {
        RealmResults<TaskEntity> results = realm.where(TaskEntity.class).contains("title", query, Case.INSENSITIVE).findAll();
        return results;
    }

    public List<TaskEntity> filterTasks(String query) {
        List<TaskEntity> taskList = new ArrayList<>();
        for (TaskEntity task: getAllTask()){
            String title = task.getTitle().toLowerCase();
            query = query.toLowerCase();
            if (title.contains(query)){
                taskList.add(task);
            }else {
                for (SubTaskEntity subTask: task.getSubTask()){
                    String subTasktitle = subTask.getTitle().toLowerCase();
                    if (subTasktitle.contains(query)){
                        taskList.add(task);
                        break;
                    }
                }
            }
        }
        return taskList;
    }
}

package com.okason.diary.data;

import com.okason.diary.models.realmentities.Folder;
import com.okason.diary.models.realmentities.SubTask;
import com.okason.diary.models.realmentities.Task;

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

    public Task createNewTask(String taskName) {
        String taskId = UUID.randomUUID().toString();
        realm.beginTransaction();
        Task task = realm.createObject(Task.class, taskId);
        task.setDateCreated(System.currentTimeMillis());
        task.setDateModified(System.currentTimeMillis());
        realm.commitTransaction();
        return task;
    }

    public SubTask createNewSubTask(String subTaskName, String parentTaskId) {
        SubTask subTask = null;
        Task parentTask = realm.where(Task.class).equalTo("id", parentTaskId).findFirst();
        if (parentTask != null){
            realm.beginTransaction();
            String subTaskId = UUID.randomUUID().toString();
            subTask = realm.createObject(SubTask.class, subTaskId);
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

    public Task createNewTask(int priority, String taskName, long dueDateAndTime, String repeat, long repeatEndDate, String folderId) {
        realm.beginTransaction();
        Task task = createNewTask(taskName);
        task.setDateModified(System.currentTimeMillis());
        task.setPriority(priority);
        task.setDueDateAndTime(dueDateAndTime);
        task.setRepeatEndDate(repeatEndDate);
        task.setRepeatFrequency(repeat);

        Folder selectedFolder = realm.where(Folder.class).equalTo("id", folderId).findFirst();
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
                Task updatedTask = backgroundRealm.where(Task.class).equalTo("id", taskId).findFirst();
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

    public void updateTaskStatus(final Task task, final boolean completed) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Task updatedTask = backgroundRealm.where(Task.class).equalTo("id", task.getId()).findFirst();
                    if (updatedTask != null) {
                        updatedTask.setChecked(completed);
                        updatedTask.setDateModified(System.currentTimeMillis());
                        for (SubTask subTask: updatedTask.getSubTask()){
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
                Task updatedTask = backgroundRealm.where(Task.class).equalTo("id", taskId).findFirst();
                SubTask updatedSubTask = null;
                for (SubTask subTask: updatedTask.getSubTask()){
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

    public RealmResults<Task> getAllTask() {
        RealmResults<Task> taskResult = realm.where(Task.class).findAll();
        return taskResult;

    }

    public void deleteTask(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(Task.class).equalTo("id", taskId).findFirst().deleteFromRealm();
            }
        });
    }

    public void deleteSubTask(final String subTaskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                backgroundRealm.where(SubTask.class).equalTo("id", subTaskId).findFirst().deleteFromRealm();
            }
        });
    }

    public Task getTaskById(String taskId) {
        try {
            Task selectedNoteEntity = realm.where(Task.class).equalTo("id", taskId).findFirst();
            return selectedNoteEntity;
        } catch (Exception e) {
            return null;
        }
    }

    public void setFolder(String taskId, String folderId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                Task task = backgroundRealm.where(Task.class).equalTo("id", taskId).findFirst();
                Folder folder = backgroundRealm.where(Folder.class).equalTo("id", folderId).findFirst();
                if (task != null && folder != null) {
                    task.setFolder(folder);
                    folder.getTodoLists().add(task);
                    task.setDateModified(System.currentTimeMillis());
                }
            }
        });
    }


    public RealmResults<Task> getAllTasksForPriority(int priority) {
        RealmResults<Task> taskResult = realm.where(Task.class).equalTo("priority", priority).findAll();
        return taskResult;
    }

    public RealmResults<Task> searchTasks(String query) {
        RealmResults<Task> results = realm.where(Task.class).contains("title", query, Case.INSENSITIVE).findAll();
        return results;
    }

    public List<Task> filterTasks(String query) {
        List<Task> taskList = new ArrayList<>();
        for (Task task: getAllTask()){
            String title = task.getTitle().toLowerCase();
            query = query.toLowerCase();
            if (title.contains(query)){
                taskList.add(task);
            }else {
                for (SubTask subTask: task.getSubTask()){
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
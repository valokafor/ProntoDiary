package com.okason.diary.data;

import com.okason.diary.models.Folder;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Task;
import com.okason.diary.ui.todolist.TaskContract;
import com.okason.diary.utils.reminder.Reminder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by valokafor on 7/7/17.
 */

public class TaskRealmRepository implements TaskContract.Repository {
    @Override
    public Task createNewTask(String taskName) {
        String taskId = UUID.randomUUID().toString();
        Task task;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            task = realm.createObject(Task.class, taskId);
            task.setDateCreated(System.currentTimeMillis());
            task.setDateModified(System.currentTimeMillis());
            task.setTitle(taskName);
            realm.commitTransaction();
            task = realm.copyFromRealm(task);
        }
        return task;

    }

    @Override
    public SubTask createNewSubTask(String subTaskName, String parentTaskId) {
        SubTask subTask = null;
        try(Realm realm = Realm.getDefaultInstance()){
            Task parentTask = realm.where(Task.class).equalTo("id", parentTaskId).findFirst();
            if (parentTask != null){
                realm.beginTransaction();
                String taskId = UUID.randomUUID().toString();
                subTask = realm.createObject(SubTask.class, taskId);
                subTask.setDateCreated(System.currentTimeMillis());
                subTask.setDateModified(System.currentTimeMillis());
                subTask.setTitle(subTaskName);
                subTask.setTask(parentTask);
                parentTask.getSubTask().add(subTask);
                realm.commitTransaction();
                subTask = realm.copyFromRealm(subTask);
            }
        }
        return subTask;
    }

    @Override
    public Task createNewTask(int priority, String taskName, long dueDateAndTime,  Reminder repeat, long repeatEndDate, String folderId) {
        Task task = createNewTask(taskName);
        Task updatedTask;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            updatedTask = realm.where(Task.class).equalTo("id", task.getId()).findFirst();
            updatedTask.setDateModified(System.currentTimeMillis());
            updatedTask.setPriority(priority);
            updatedTask.setDueDateAndTime(dueDateAndTime);
            updatedTask.setRepeatEndDate(repeatEndDate);
            updatedTask.setRepeatFrequency(repeat);

            Folder selectedFolder = realm.where(Folder.class).equalTo("id", folderId).findFirst();
            if (selectedFolder != null){
                updatedTask.setFolder(selectedFolder);
                selectedFolder.getTasks().add(updatedTask);
            }

            realm.commitTransaction();
            updatedTask = realm.copyFromRealm(updatedTask);
        }
        return updatedTask;
    }

    @Override
    public void updateTask(String taskId, int priority, String taskName, long dueDateAndTime, Reminder repeat, long repeatEndDate, String folderId) {

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            Task updatedTask = realm.where(Task.class).equalTo("id", taskId).findFirst();
            updatedTask.setTitle(taskName);
            updatedTask.setDateModified(System.currentTimeMillis());
            updatedTask.setPriority(priority);
            updatedTask.setDueDateAndTime(dueDateAndTime);
            updatedTask.setRepeatEndDate(repeatEndDate);
            updatedTask.setRepeatFrequency(repeat);

            Folder selectedFolder = realm.where(Folder.class).equalTo("id", folderId).findFirst();
            if (selectedFolder != null){
                updatedTask.setFolder(selectedFolder);
                selectedFolder.getTasks().add(updatedTask);
            }
            realm.commitTransaction();

        }

    }

    @Override
    public void updateTaskStatus(final Task task, final boolean completed) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm backgroundRealm) {
                    Task updatedTask = backgroundRealm.where(Task.class).equalTo("id", task.getId()).findFirst();
                    updatedTask.setChecked(completed);
                    updatedTask.setDateModified(System.currentTimeMillis());
                    for (SubTask subTask: updatedTask.getSubTask()){
                        subTask.setChecked(completed);
                    }
                }
            });
        }

    }

    /**
     * Saves changes to databas when a SubTask checkbox is selected
     * @param taskId - Parent Task Id
     * @param subTaskId - Sub Task Id
     * @param completed - Sub Task status
     */
    @Override
    public void updateSubTaskStatus(final String taskId, final String subTaskId, final boolean completed) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
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

    }


    @Override
    public List<Task> getAllTask() {
        List<Task> allTask = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Task> taskResult = realm.where(Task.class).findAll();
            if (taskResult != null && taskResult.size() > 0){
                allTask = realm.copyFromRealm(taskResult);
            }
        }
        return allTask;

    }

    @Override
    public void deleteTask(final String taskId) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(Task.class).equalTo("id", taskId).findFirst().deleteFromRealm();
                }
            });

        }

    }

    @Override
    public void deleteSubTask(String parentTaskId, final String subTaskId) {

        try (Realm realm = Realm.getDefaultInstance()){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(SubTask.class).equalTo("id", subTaskId).findFirst().deleteFromRealm();
                }
            });

        }

    }

    @Override
    public Task getTaskById(String id) {
        Task selectedTask;
        try (Realm realm = Realm.getDefaultInstance()){
            selectedTask = realm.where(Task.class).equalTo("id", id).findFirst();
            selectedTask = realm.copyFromRealm(selectedTask);
        }catch (Exception e){
            selectedTask = null;
        }
        return selectedTask;
    }

    @Override
    public int getAllTaskAndSubTaskCount() {
        List<Task> tasks = getAllTask();
        int count = 0;
        for (Task task: tasks){
            count += task.getTaskCount();
        }
        return count;
    }
}

package com.okason.diary.data;

import com.okason.diary.models.Folder;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Task;
import com.okason.diary.ui.todolist.TaskContract;

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
    public Task createNewTask(int priority, String taskName, long dueDateAndTime,  String repeat, String folderId) {
        Task task = createNewTask(taskName);
        Task updatedTask;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            updatedTask = realm.where(Task.class).equalTo("id", task.getId()).findFirst();
            updatedTask.setDateModified(System.currentTimeMillis());
            updatedTask.setPriority(priority);
            updatedTask.setDueDateAndTime(dueDateAndTime);
            updatedTask.setRecurrenceRule(repeat);

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
    public void updateTaskStatus(Task task, boolean completed) {

    }

    @Override
    public void removeSubTaskFromTask(SubTask subTask, Task task) {

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
    public void deleteTask() {

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

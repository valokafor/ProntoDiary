package com.okason.diary.ui.todolist;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.data.TaskRealmRepository;
import com.okason.diary.models.Folder;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Task;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.reminder.Reminder;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by valokafor on 6/27/17.
 */

public class TaskPresenter implements TaskContract.Actions {
    private TaskContract.View mView;
    private TaskContract.Repository repository;
    private Folder selectedFolder;
    private Task currentTask;

    public TaskPresenter(@Nullable TaskContract.View view) {
        mView = view;
        repository = new TaskRealmRepository();
    }


    @Override
    public void addSubTaskTask(String taskName) {
        if (currentTask != null){
           SubTask savedSubTask = repository.createNewSubTask(taskName, currentTask.getId());
            Log.d("Task Name", savedSubTask.getTitle());
        }

    }


    @Override
    public void onShowTaskDetail(Task task) {

    }

    @Override
    public void onMarkTaskAsComplete(Task task) {
        repository.updateTaskStatus(task, true);
    }

    @Override
    public void onMarkTaskAsInComplete(Task task) {
        repository.updateTaskStatus(task, false);
    }

    @Override
    public void onMarkSubTaskAsComplete(String taskId, String subTaskId) {
        repository.updateSubTaskStatus(taskId, subTaskId, true);
    }

    @Override
    public void onMarkSubTaskAsInComplete(String taskId, String subTaskId) {
        repository.updateSubTaskStatus(taskId, subTaskId, false);
    }

    @Override
    public void onEditTaskButtonClick(Task task) {

    }


    @Override
    public Task findTaskById(String id) {
        return null;
    }

    @Override
    public void deleteTask(Task task) {
        repository.deleteTask(task.getId());
    }

    @Override
    public void deleteSubTask(String taskId, String subTaskId) {
        repository.deleteSubTask(taskId, subTaskId);
    }

    @Override
    public void onTitleChange(String newTitle) {

    }

    @Override
    public void onFolderChange(String folderId) {

    }

    @Override
    public String getCurrentTaskId() {
        return currentTask.getId();
    }

    @Override
    public void setCurrentTaskId(String taskId) {
        if (!TextUtils.isEmpty(taskId)){
            currentTask = repository.getTaskById(taskId);
            if (currentTask != null && mView != null){
                mView.showTaskDetail(currentTask);

            }
        }
    }

    @Override
    public Task getCurrentTask() {
        return currentTask;
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onSaveAndExit(int priority, String taskName, long dueDate, Reminder repeat, long repeadEndDate, String folderId, boolean shouldAddSubTask) {

        if (currentTask == null) {
            currentTask = repository.createNewTask(priority, taskName, dueDate, repeat, repeadEndDate, folderId);
        } else {
            repository.updateTask(currentTask.getId(), priority, taskName, dueDate, repeat, repeadEndDate, folderId);
        }

        Calendar dueTime = new GregorianCalendar();
        dueTime.setTimeInMillis(currentTask.getDueDateAndTime());
        if (dueTime.after(Calendar.getInstance())){
            //Due Date is in the future, so schedule the first reminder

        }



        if (shouldAddSubTask){
            //Go To Add Subtask
            Intent addSubTaskIntent = new Intent(ProntoDiaryApplication.getAppContext(), AddSubTaskActivity.class);
            addSubTaskIntent.putExtra(Constants.TASK_ID, currentTask.getId());
            addSubTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ProntoDiaryApplication.getAppContext().startActivity(addSubTaskIntent);
        }else {
            //Go to Task List
            mView.goBackToParent();
        }

    }


    @Override
    public Folder getFolderById(String id) {
        return null;
    }
}

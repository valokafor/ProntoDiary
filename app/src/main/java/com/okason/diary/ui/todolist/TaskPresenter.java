package com.okason.diary.ui.todolist;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.DisplayFragmentEvent;
import com.okason.diary.data.TaskRealmRepository;
import com.okason.diary.models.Folder;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Task;

import org.greenrobot.eventbus.EventBus;

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

    }

    @Override
    public void onMarkTaskAsInComplete(Task task) {

    }

    @Override
    public void onEditTaskButtonClick(Task task) {

    }

    @Override
    public void onDeleteTaskButtonClick(Task task) {

    }

    @Override
    public Task findTaskById(String id) {
        return null;
    }

    @Override
    public void deleteTask() {

    }

    @Override
    public void onTitleChange(String newTitle) {

    }

    @Override
    public void onFolderChange(String folderId) {

    }

    @Override
    public String getCurrentTaskId() {
        return null;
    }

    @Override
    public void setCurrentTaskId(String taskId) {
        if (!TextUtils.isEmpty(taskId)){
            currentTask = repository.getTaskById(taskId);
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
    public void onSaveAndExit(int priority, String taskName, long dueDate, String repeat, long repeadEndDate, String folderId, boolean shouldAddSubTask) {
        Task savedTask = repository.createNewTask(priority, taskName, dueDate, repeat, repeadEndDate, folderId);

        Calendar dueTime = new GregorianCalendar();
        dueTime.setTimeInMillis(savedTask.getDueDateAndTime());
        if (dueTime.after(Calendar.getInstance())){
            //Due Date is in the future, so schedule the first reminder

        }



        if (shouldAddSubTask){
            //Go To Add Subtask
            AddSubTaskFragment fragment = AddSubTaskFragment.newInstance(savedTask.getId());
            EventBus.getDefault().post(new DisplayFragmentEvent(fragment, savedTask.getTitle()));
        }else {
            //Go to Task List
            EventBus.getDefault().post(new DisplayFragmentEvent(new TaskListFragment(),
                    ProntoDiaryApplication.getAppContext().getString(R.string.title_todo_list)));
        }

    }


    @Override
    public Folder getFolderById(String id) {
        return null;
    }
}

package com.okason.diary.ui.todolist;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.DisplayFragmentEvent;
import com.okason.diary.data.TaskRealmRepository;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Note;
import com.okason.diary.models.Task;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by valokafor on 6/27/17.
 */

public class TaskPresenter implements TaskContract.Actions {
    private final TaskContract.View mView;
    private TaskContract.Repository mRepository;
    private Folder selectedFolder;
    private Task currentTask;

    public TaskPresenter(TaskContract.View view) {
        mView = view;
        mRepository = new TaskRealmRepository();
    }


    @Override
    public void addSubTaskTask(String taskName) {

    }

    @Override
    public void onAddTaskButtonClick() {

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
    public Note getCurrentTask() {
        return null;
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onSaveAndExit(int priority, String taskName, long dueDate, String repeat, String folderId, boolean addDSubTasks) {
        Task savedTask = mRepository.createNewTask(priority, taskName, dueDate, repeat, folderId);

        if (addDSubTasks){
            //Go To Add Subtask
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

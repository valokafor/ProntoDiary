package com.okason.diary.ui.todolist;

import com.okason.diary.models.Folder;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Task;
import com.okason.diary.utils.reminder.Reminder;

import java.util.List;

/**
 * Created by valokafor on 6/26/17.
 */

public class TaskContract {

    public interface View{
        void showTaskDetail(Task task);
        void showEditTaskItem(Task todoList);
        void showMessage(String message);
        void goBackToParent();
    }


    public interface Actions{
        void addSubTaskTask(String taskName);
        void onShowTaskDetail(Task task);
        void onMarkTaskAsComplete(Task task);
        void onMarkTaskAsInComplete(Task task);
        void onMarkSubTaskAsComplete(String taskId, String subTaskId);
        void onMarkSubTaskAsInComplete(String taskId, String subTaskId);
        void onEditTaskButtonClick(Task task);
        Task findTaskById(String id);
        void deleteTask(Task task);
        void onTitleChange(String newTitle);
        void onFolderChange(String folderId);
        String getCurrentTaskId();
        void setCurrentTaskId(String taskId);
        Task getCurrentTask();
        void updateUI();
        void onSaveAndExit(int priority, String taskName, long dueDateAndTime, Reminder repeadFrequency, long repeatEndDate, String folderId, boolean addDSubTasks);
        Folder getFolderById(String id);
    }

    public interface Repository{
        Task createNewTask(String taskName);
        SubTask createNewSubTask(String subTaskName, String parentTaskId);
        Task createNewTask(int priority, String taskName, long dueDateAndTime, Reminder repeat, long repeatEndDate, String folderId);
        void updateTask(String taskId, int priority, String taskName, long dueDateAndTime, Reminder repeat, long repeatEndDate, String folderId);
        void updateTaskStatus(Task task, boolean completed);
        void updateSubTaskStatus(String taskId, String subTaskId, boolean completed);
        void removeSubTaskFromTask(SubTask subTask, Task task);
        List<Task> getAllTask();
        void deleteTask(String taskId);
        Task getTaskById(String id);
        int  getAllTaskAndSubTaskCount();

    }
}

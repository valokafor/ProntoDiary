package com.okason.diary.ui.todolist;

import com.okason.diary.models.Task;
import com.okason.diary.models.TodoList;

import java.util.List;

/**
 * Created by valokafor on 6/26/17.
 */

public class TodoListContract {

    public interface View{
        void showTodoList(List<TodoList> todoLists);
        void showAddTodoListItem();
        void showTaskDetail(Task task);
        void showEditToDoListItem(TodoList todoList);
        void markTaskAsComplete(Task task);
        void showMoveTaskToNewListDialog(Task task);
        void showDatabaseError(String error);
        void showDatabaseSuccessMessage(String message);
        void showEmptyText(boolean showText);
    }


    public interface Actions{
        List<TodoList> getTodoListItems();
        void loadTodoItemList();
        void addTask(String taskName, TodoList todoList);
        void addTodoList(String todoListName);
        void onAddTodoListButtonClick();
        void onShowTaskDetail(Task task);
        void onMarkTaskAsComplete(Task task);
        void onMarkTaskAsInComplete(Task task);
        void onEditTodoListButtonClick(TodoList todoList);
        void onDeleteTaskButtonClick(Task task, TodoList todoList);
        void onDeleteTodoListButtonClick(TodoList todoList);
        void onSelectMoveTaskToNewList(Task task);
        TodoList findTodoListById(String id);
        void moveTaskToAnotherTodoList(Task task, TodoList todoList);
    }

    public interface Repository{
        Task createNewTask(String taskName);
        TodoList createNewTodoListItem(String todoListName);
        void addTaskAsync(Task task, TodoList parent);
        void updateTaskStatus(Task task, boolean completed);
        void removeTaskFromTodoList(Task task, TodoList todoList);
        void updateTodoListItemAsync(TodoList todoList);
        void deleteTodoListItem(String id);
        List<TodoList> getAllTodoListItems();
        void getAllTodoListAsync();
        void deleteAllTodoItems();
        TodoList getTodoListById(String id);

    }
}

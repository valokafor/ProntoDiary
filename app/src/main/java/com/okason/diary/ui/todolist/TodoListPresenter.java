package com.okason.diary.ui.todolist;

import com.okason.diary.models.Task;
import com.okason.diary.models.TodoList;

import java.util.List;

/**
 * Created by valokafor on 6/27/17.
 */

public class TodoListPresenter implements TodoListContract.Actions {
    private final TodoListContract.View mView;
    private TodoListContract.Repository mRepository;

    public TodoListPresenter(TodoListContract.View view) {
        mView = view;
     //   mRepository = new TodoListRealmRepository();
    }


    @Override
    public List<TodoList> getTodoListItems() {
        return mRepository.getAllTodoListItems();
    }

    @Override
    public void loadTodoItemList() {

    }

    @Override
    public void addTask(String taskName, TodoList todoList) {
        Task newTask = mRepository.createNewTask(taskName);
        mRepository.addTaskAsync(newTask, todoList);
    }

    @Override
    public void addTodoList(String todoListName) {
        mRepository.createNewTask(todoListName);
    }

    @Override
    public void onAddTodoListButtonClick() {
        mView.showAddTodoListItem();
    }

    @Override
    public void onShowTaskDetail(Task task) {
        mView.showTaskDetail(task);
    }

    @Override
    public void onMarkTaskAsComplete(Task task) {
        mRepository.updateTaskStatus(task, true);
    }

    @Override
    public void onMarkTaskAsInComplete(Task task) {
        mRepository.updateTaskStatus(task, false);
    }


    @Override
    public void onEditTodoListButtonClick(TodoList todoList) {
        mView.showEditToDoListItem(todoList);
    }

    @Override
    public void onDeleteTaskButtonClick(Task task, TodoList todoList) {
        mRepository.removeTaskFromTodoList(task, todoList);
    }

    @Override
    public void onDeleteTodoListButtonClick(TodoList todoList) {
        mRepository.deleteTodoListItem(todoList.getId());
    }

    @Override
    public void onSelectMoveTaskToNewList(Task task) {
        mView.showMoveTaskToNewListDialog(task);
    }

    @Override
    public TodoList findTodoListById(String id) {
        return mRepository.getTodoListById(id);
    }

    @Override
    public void moveTaskToAnotherTodoList(Task task, TodoList todoList) {
        mView.showMoveTaskToNewListDialog(task);
    }


}

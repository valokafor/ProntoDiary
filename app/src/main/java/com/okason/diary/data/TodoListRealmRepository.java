package com.okason.diary.data;

import com.okason.diary.models.Task;
import com.okason.diary.models.TodoList;
import com.okason.diary.ui.todolist.TodoListContract;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by valokafor on 6/27/17.
 */

public class TodoListRealmRepository implements TodoListContract.Repository {
    @Override
    public Task createNewTask(String taskName) {
        return null;
    }

    @Override
    public TodoList createNewTodoListItem(String todoListName) {
        String todoListId = UUID.randomUUID().toString();
        TodoList todoList;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            todoList = realm.createObject(TodoList.class, todoListId);
            todoList.setDateCreated(System.currentTimeMillis());
            todoList.setDateModified(System.currentTimeMillis());
            todoList.setTitle(todoListName);
            realm.commitTransaction();
            todoList = realm.copyFromRealm(todoList);
        }
        return todoList;

    }

    @Override
    public void addTaskAsync(Task task, TodoList parent) {

    }

    @Override
    public void updateTaskStatus(Task task, boolean completed) {

    }

    @Override
    public void removeTaskFromTodoList(Task task, TodoList todoList) {

    }

    @Override
    public void updateTodoListItemName(String todoListId, String todoListName) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.beginTransaction();
            TodoList selectedTodoList = realm.where(TodoList.class).equalTo("id", todoListId).findFirst();
            if (selectedTodoList != null){
                selectedTodoList.setTitle(todoListName);
                selectedTodoList.setDateModified(System.currentTimeMillis());
            }
            realm.commitTransaction();
        }

    }



    @Override
    public void deleteTodoListItem(String id) {

    }

    @Override
    public List<TodoList> getAllTodoListItems() {
        return null;
    }

    @Override
    public void getAllTodoListAsync() {

    }

    @Override
    public void deleteAllTodoItems() {

    }

    @Override
    public TodoList getTodoListById(String id) {
        TodoList selectedTodoList;
        try (Realm realm = Realm.getDefaultInstance()){
            selectedTodoList = realm.where(TodoList.class).equalTo("id", id).findFirst();
            selectedTodoList = realm.copyFromRealm(selectedTodoList);
        }catch (Exception e){
            selectedTodoList = null;
        }
        return selectedTodoList;
    }
}

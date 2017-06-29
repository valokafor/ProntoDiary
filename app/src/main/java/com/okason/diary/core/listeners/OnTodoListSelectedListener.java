package com.okason.diary.core.listeners;

import com.okason.diary.models.TodoList;

/**
 * Created by valokafor on 6/28/17.
 */

public interface OnTodoListSelectedListener {
    void onTodoListClick(TodoList clickedTodo);
}

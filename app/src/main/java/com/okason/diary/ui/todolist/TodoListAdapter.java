package com.okason.diary.ui.todolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.models.Task;
import com.okason.diary.models.TodoList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 6/26/17.
 */

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    private final List<TodoList> mTodoLists;

    public TodoListAdapter(List<TodoList> todoLists) {
        mTodoLists = todoLists;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View todoListView = inflater.inflate(R.layout.custom_row_todo_list, parent, false);
        return new ViewHolder((FrameLayout) todoListView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TodoList todoList = mTodoLists.get(position);

        String title = todoList.getTitle();

        holder.titleTextView.setText(title);

        StringBuilder stringBuilder = new StringBuilder(40);

        int numberOfTasks = 0;
        String tasksLabel = ProntoDiaryApplication.getAppContext().getString(R.string.label_task);
        numberOfTasks = todoList.getTaskList().size();

        if (numberOfTasks > 1){
            tasksLabel = tasksLabel + "s";
        }
        tasksLabel = numberOfTasks + " "  + tasksLabel;

        stringBuilder.append(tasksLabel);

        int completedTasks = 0;
        int pendingTasks = 0;

        if (todoList.getTaskList().size() > 0){
            for (Task task: todoList.getTaskList()){
                if (task.isChecked()){
                    completedTasks++;
                }else {
                    pendingTasks++;
                }
            }

            stringBuilder.append(" (");
            stringBuilder.append(completedTasks);
            stringBuilder.append(" ");
            stringBuilder.append(ProntoDiaryApplication.getAppContext().getString(R.string.label_done));
            stringBuilder.append(",");
            stringBuilder.append(pendingTasks);
            stringBuilder.append(ProntoDiaryApplication.getAppContext().getString(R.string.label_pending));
            stringBuilder.append(")");

        }

        String statusText = stringBuilder.toString();
        holder.taskCountTextView.setText(statusText);



    }

    @Override
    public int getItemCount() {
        if (mTodoLists != null){
            return mTodoLists.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public FrameLayout container;

        @BindView(R.id.text_view_todo_title)
        TextView titleTextView;

        @BindView(R.id.text_view_todo_task_count)
        TextView taskCountTextView;
        @BindView(R.id.image_view_detail)
        ImageView showMore;


        public ViewHolder(FrameLayout container) {
            super(container);
            ButterKnife.bind(this, container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();


        }
    }


}

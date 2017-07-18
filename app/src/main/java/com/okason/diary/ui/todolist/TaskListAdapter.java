package com.okason.diary.ui.todolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.listeners.TaskItemListener;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Task;
import com.okason.diary.utils.date.DateHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 6/26/17.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private final List<Task> mTasks;
    private final Context mContext;
    private TaskItemListener mTaskItemListener;

    public TaskListAdapter(List<Task> todoLists, Context context) {
        mTasks = todoLists;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View todoListView = inflater.inflate(R.layout.custom_row_layout_task_list, parent, false);
        return new ViewHolder((FrameLayout) todoListView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Task task = mTasks.get(position);

        String title = task.getTitle();

        holder.titleTextView.setText(title);

        StringBuilder stringBuilder = new StringBuilder(40);

        int numberOfTasks = 0;
        String tasksLabel = ProntoDiaryApplication.getAppContext().getString(R.string.label_sub_task);
        numberOfTasks = task.getSubTask().size();

        if (numberOfTasks > 1){
            tasksLabel = tasksLabel + "s";
        }
        tasksLabel = numberOfTasks + " "  + tasksLabel;

        stringBuilder.append(tasksLabel);

        int completedTasks = 0;
        int pendingTasks = 0;

        if (task.getSubTask().size() > 0){
            for (SubTask subTask: task.getSubTask()){
                if (subTask.isChecked()){
                    completedTasks++;
                }else {
                    pendingTasks++;
                }
            }

            stringBuilder.append(" (");
            stringBuilder.append(completedTasks);
            stringBuilder.append(" ");
            stringBuilder.append(ProntoDiaryApplication.getAppContext().getString(R.string.label_done));
            stringBuilder.append(", ");
            stringBuilder.append(pendingTasks);
            stringBuilder.append(" " + ProntoDiaryApplication.getAppContext().getString(R.string.label_pending));
            stringBuilder.append(")");

        }

        String statusText = stringBuilder.toString();
        holder.taskCountTextView.setText(statusText);
        String time = DateHelper.getTimeShort(mContext, task.getDueDateAndTime());
        holder.dueTimeTextView.setText(time);



    }

    @Override
    public int getItemCount() {
        if (mTasks != null){
            return mTasks.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public FrameLayout container;

        @BindView(R.id.text_view_title)
        TextView titleTextView;

        @BindView(R.id.text_view_task_count)
        TextView taskCountTextView;

        @BindView(R.id.text_view_time)
        TextView dueTimeTextView;

        @BindView(R.id.text_view_options)
        TextView showMore;


        public ViewHolder(FrameLayout container) {
            super(container);
            ButterKnife.bind(this, container);

        }


    }


}

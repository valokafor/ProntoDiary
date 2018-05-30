package com.okason.diary.ui.todolist;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.TaskItemListener;
import com.okason.diary.models.Task;
import com.okason.diary.utils.date.DateHelper;
import com.okason.diary.utils.date.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 6/26/17.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private final List<Task> mTasks;
    private final Context mContext;
    private final TaskItemListener taskItemListener;

    public TaskListAdapter(List<Task> todoLists, Context context, TaskItemListener taskItemListener) {
        mTasks = todoLists;
        mContext = context;
        this.taskItemListener = taskItemListener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View todoListView = inflater.inflate(R.layout.custom_row_layout_task_list, parent, false);
        return new ViewHolder((FrameLayout) todoListView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Task task = mTasks.get(position);

        boolean isChecked = task.isChecked();
        if (isChecked){
            holder.titleTextView.setTypeface(holder.titleTextView.getTypeface(), Typeface.ITALIC);
            holder.checkBox.setChecked(true);
            holder.titleTextView.setPaintFlags( holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


        String title = task.getTitle();

        holder.titleTextView.setText(title);

        String statusText = TimeUtils.getSubTaskStatus(task);
        holder.taskCountTextView.setText(statusText);
        String time = DateHelper.getTimeShort(mContext, task.getReminder().getDateAndTime());
        holder.dueTimeTextView.setText(time);

        holder.showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.showMore);
                popupMenu.inflate(R.menu.menu_task_options);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_edit:
                                //Edit the Task
                                taskItemListener.onEditTaskButtonClicked(task);
                                break;
                            case R.id.action_delete:
                                //Prompt for delete
                                taskItemListener.onDeleteTaskButtonClicked(task);
                                break;
                            case R.id.action_add_sub_task:
                                //Go to add Sub Task
                                taskItemListener.onAddSubTasksButtonClicked(task);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });



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

        @BindView(R.id.checkbox)
        CheckBox checkBox;

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

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        final Task checkedTask = mTasks.get(getLayoutPosition());
                        taskItemListener.onTaskChecked(checkedTask);

                    } else {
                        final Task uncheckedTask = mTasks.get(getLayoutPosition());
                        taskItemListener.onTaskUnChecked(uncheckedTask);
                    }

                }
            });

        }


    }


}

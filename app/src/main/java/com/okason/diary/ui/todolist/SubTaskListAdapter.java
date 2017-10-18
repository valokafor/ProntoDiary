package com.okason.diary.ui.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.SubTaskItemListener;
import com.okason.diary.models.SubTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valokafor on 7/17/17.
 */

public class SubTaskListAdapter extends RecyclerView.Adapter<SubTaskListAdapter.ViewHolder> {
  //  private final Task parentTask;
    private final SubTaskItemListener taskItemListener;
    private final List<SubTask> subTasks;

    public SubTaskListAdapter(List<SubTask> subTasks, SubTaskItemListener taskItemListener) {
        this.taskItemListener = taskItemListener;
        this.subTasks = subTasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.custom_row_layout_sub_task_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (subTasks != null && subTasks.size() > 0){
            SubTask selectedSubTask = subTasks.get(position);

            holder.taskTitle.setText(selectedSubTask.getTitle());

            boolean isChecked = selectedSubTask.isChecked();
            holder.checkBox.setChecked(isChecked);
        }

    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.image_button_drag)
        ImageButton reorderButton;
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.text_view_title)
        TextView taskTitle;
        @BindView(R.id.image_button_delete)
        ImageButton deleteButton;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        final SubTask checkedTask = subTasks.get(getLayoutPosition());
                        taskItemListener.onSubTaskChecked(checkedTask.getTitle());

                    } else {
                        final SubTask uncheckedTask = subTasks.get(getLayoutPosition());
                        taskItemListener.onSubTaskUnChecked(uncheckedTask.getTitle());
                    }
                }
            });


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    final SubTask deletedSubTask = subTasks.get(position);
                    taskItemListener.onSubTaskDeleted(deletedSubTask.getTitle());

                }
            });

            taskTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SubTask clickedTask = subTasks.get(getLayoutPosition());
                    taskItemListener.onEditSubTask(clickedTask);

                }
            });
        }
    }

}

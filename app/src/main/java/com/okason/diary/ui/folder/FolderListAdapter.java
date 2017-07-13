package com.okason.diary.ui.folder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Task;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Valentine on 5/26/2016.
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.ViewHolder>{
    private final static String LOG_CAT = FolderListAdapter.class.getSimpleName();
    private final static boolean DEBUG = true;

    private List<Folder> mFolders;
    private final OnFolderSelectedListener mListener;
    private final Context mContext;

    public FolderListAdapter(Context mContext, List<Folder> mFolders, OnFolderSelectedListener mListener) {
        this.mFolders = mFolders;
        this.mContext = mContext;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_folder_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Folder category = mFolders.get(position);
        String categoryName = category.getFolderName();
        holder.categoryName.setText(categoryName);

        int numNote = category.getNotes().size();
        String notes = numNote > 1 ? mContext.getString(R.string.label_journals) : mContext.getString(R.string.label_journal);
        holder.noteCountTextView.setText(numNote + " " + notes);


        String taskLabel = ProntoDiaryApplication.getAppContext().getString(R.string.zero_task);

        if (category.getTasks() != null && category.getTasks().size() > 0){
            int taskCount = 0;
            for (Task task: category.getTasks()){
                taskCount += task.getTaskCount();
            }
            taskLabel = taskCount > 1 ? taskCount
                    + " " + mContext.getString(R.string.label_tasks) : taskCount
                    + " " + mContext.getString(R.string.label_task) ;
        }

        holder.taskCountTextView.setText(taskLabel);
    }

    public void replaceData(List<Folder> categories){
        this.mFolders.clear();
        mFolders = categories;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return mFolders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_button_edit_category) ImageButton editCategory;
        @BindView(R.id.image_button_delete_category) ImageButton deleteCategory;
        @BindView(R.id.text_view_folder_name)  TextView categoryName;
        @BindView(R.id.text_view_note_count) TextView noteCountTextView;
        @BindView(R.id.text_view_task_count) TextView taskCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Folder categoryToBeEdited = mFolders.get(getLayoutPosition());
                    mListener.onEditCategoryButtonClicked(categoryToBeEdited);
                }
            });
            deleteCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Folder categoryToBeEdited = mFolders.get(getLayoutPosition());
                    mListener.onDeleteCategoryButtonClicked(categoryToBeEdited);
                }
            });

        }
    }
}

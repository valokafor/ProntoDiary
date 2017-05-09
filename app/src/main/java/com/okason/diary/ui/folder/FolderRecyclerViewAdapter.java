package com.okason.diary.ui.folder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.models.viewModel.FolderViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Valentine on 5/26/2016.
 */
public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder>{
    private final static String LOG_CAT = FolderRecyclerViewAdapter.class.getSimpleName();
    private final static boolean DEBUG = true;

    private List<FolderViewModel> mCategories;
    private final OnFolderSelectedListener mListener;
    private final Context mContext;

    public FolderRecyclerViewAdapter(Context mContext, List<FolderViewModel> mCategories, OnFolderSelectedListener mListener) {
        this.mCategories = mCategories;
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

        FolderViewModel category = mCategories.get(position);
        holder.categoryName.setText(category.getCategoryName());
        int numNote = category.getNotes().size();
        String notes = numNote > 1 ? "Notes" : "Note";
        holder.noteCountTextView.setText(numNote + " " + notes);
    }

    public void replaceData(List<FolderViewModel> categories){
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_button_edit_category) ImageButton editCategory;
        @BindView(R.id.image_button_delete_category) ImageButton deleteCategory;
        @BindView(R.id.text_view_category_name)  TextView categoryName;
        @BindView(R.id.text_view_note_count) TextView noteCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FolderViewModel categoryToBeEdited = mCategories.get(getLayoutPosition());
                    mListener.onEditCategoryButtonClicked(categoryToBeEdited);
                }
            });
            deleteCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FolderViewModel categoryToBeEdited = mCategories.get(getLayoutPosition());
                    mListener.onDeleteCategoryButtonClicked(categoryToBeEdited);
                }
            });

        }
    }
}

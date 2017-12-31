package com.okason.diary.ui.folder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.models.Folder;
import com.okason.diary.ui.addnote.DataAccessManager;

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
    private DataAccessManager dataAccessManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public FolderListAdapter(Context mContext, List<Folder> mFolders, OnFolderSelectedListener mListener) {
        this.mFolders = mFolders;
        this.mContext = mContext;
        this.mListener = mListener;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            dataAccessManager = new DataAccessManager(firebaseUser.getUid());
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_folder_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Folder folder = mFolders.get(position);
        String folderName = folder.getTitle();
        holder.folderName.setText(folderName);


        if (dataAccessManager != null) {
            dataAccessManager.getJournalCloudPath()
                    .whereEqualTo("folder.id", folder.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                int journalCount = task.getResult().size();
                                String label = journalCount > 1 ? mContext.getString(R.string.label_journals) : mContext.getString(R.string.label_journal);
                                holder.noteCountTextView.setText(journalCount + " " + label);
                                // notifyItemChanged(position);

                            }
                        }
                    });
        }

        if (dataAccessManager != null) {
            dataAccessManager.getTaskPath()
                    .whereEqualTo("folder.id", folder.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                int taskCount = task.getResult().size();
                                String taskLabel = taskCount > 1 ? "Tasks" : "Task";
                                holder.taskCountTextView.setText(taskCount + " " + taskLabel);
                            }
                        }
                    });
        }

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

        @BindView(R.id.image_button_edit_category) ImageButton editFolder;
        @BindView(R.id.image_button_delete_category) ImageButton deleteFolder;
        @BindView(R.id.text_view_folder_name)  TextView folderName;
        @BindView(R.id.text_view_note_count) TextView noteCountTextView;
        @BindView(R.id.text_view_task_count) TextView taskCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Folder categoryToBeEdited = mFolders.get(getLayoutPosition());
                    mListener.onEditCategoryButtonClicked(categoryToBeEdited);
                }
            });
            deleteFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Folder categoryToBeEdited = mFolders.get(getLayoutPosition());
                    mListener.onDeleteCategoryButtonClicked(categoryToBeEdited);
                }
            });

        }
    }
}

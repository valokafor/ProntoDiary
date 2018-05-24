package com.okason.diary.ui.folder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.models.realmentities.Folder;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectFolderDialogFragment extends DialogFragment {

    private List<Folder> mCategories;
    private SelectFolderAdapter mCategoryAdapter;
    private OnFolderSelectedListener mCategorySelectedListener;


    public void setCategorySelectedListener(OnFolderSelectedListener categorySelectedListener) {
        mCategorySelectedListener = categorySelectedListener;
    }

    public SelectFolderDialogFragment() {
        // Required empty public constructor
    }

    public void setFolders(List<Folder> folders) {
        this.mCategories = folders;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public static SelectFolderDialogFragment newInstance(){
        return new SelectFolderDialogFragment();
    }

    public void setCategories(List<Folder> categories) {
        mCategories = categories;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.folder_dialog_list, null);
        builder.setView(convertView);

        View titleView = (View)inflater.inflate(R.layout.dialog_title_layout, null);
        TextView addFolderTitle = (TextView) titleView.findViewById(R.id.text_view_dialog_title);
        addFolderTitle.setText(getString(R.string.select_folder));
        builder.setCustomTitle(titleView);


        ListView dialogList = (ListView) convertView.findViewById(R.id.dialog_listview);
        TextView emptyText = (TextView) convertView.findViewById(R.id.category_list_empty);
        dialogList.setEmptyView(emptyText);

        final ImageButton addCategoryButton = (ImageButton)titleView.findViewById(R.id.image_button_add);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategorySelectedListener.onAddCategoryButtonClicked();
            }
        });

        mCategoryAdapter = new SelectFolderAdapter(getActivity(), mCategories);
        dialogList.setAdapter(mCategoryAdapter);

        dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Folder mSelectedCategory = mCategories.get(position);
                if (mSelectedCategory != null){
                    mCategorySelectedListener.onCategorySelected(mSelectedCategory);
                }
            }
        });

        return builder.create();

    }



}

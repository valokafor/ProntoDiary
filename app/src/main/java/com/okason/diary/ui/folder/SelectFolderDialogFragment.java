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
import com.okason.diary.core.events.FolderListChangeEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.models.Folder;
import com.okason.diary.ui.addnote.DataAccessManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectFolderDialogFragment extends DialogFragment {

    private List<Folder> mCategories;
    private SelectFolderAdapter mCategoryAdapter;
    private OnFolderSelectedListener mCategorySelectedListener;
    private DataAccessManager dataAccessManager;


    public void setCategorySelectedListener(OnFolderSelectedListener categorySelectedListener) {
        mCategorySelectedListener = categorySelectedListener;
    }

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    public SelectFolderDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories = new ArrayList<>();


    }

    public static SelectFolderDialogFragment newInstance(){
        return new SelectFolderDialogFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFolderListChange(FolderListChangeEvent event){
        mCategories = event.getFolderlList();
        mCategoryAdapter.replaceData(mCategories);
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
        dataAccessManager.getAllFolder();

        return builder.create();

    }


}

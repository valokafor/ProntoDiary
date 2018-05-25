package com.okason.diary.ui.folder;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.data.FolderDao;
import com.okason.diary.models.realmentities.Folder;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFolderDialogFragment extends DialogFragment {
    private EditText mFolderEditText;
    private Folder mFolder = null;
    private Realm realm;
    private FolderDao folderDao;




    public AddFolderDialogFragment() {
        // Required empty public constructor
    }





    public static AddFolderDialogFragment newInstance(String folderId){
        AddFolderDialogFragment dialogFragment = new AddFolderDialogFragment();
        if (!TextUtils.isEmpty(folderId)) {
            Bundle args = new Bundle();
            args.putString(Constants.FOLDER_ID, folderId);
            dialogFragment.setArguments(args);
        }
        return dialogFragment;
    }

    /**
     * The method gets the Folder that was passed in
     */
    public void getCurrentFolder(){
        if (getArguments() != null && getArguments().containsKey(Constants.FOLDER_ID)){
            String folderId = getArguments().getString(Constants.FOLDER_ID);
            if (!TextUtils.isEmpty(folderId)){
                mFolder = folderDao.getFolderById(folderId);

            }
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        folderDao = new FolderDao(realm);
        final AlertDialog.Builder addFolderDialog = new AlertDialog.Builder(getActivity(), R.style.dialog);

        getCurrentFolder();
        if (savedInstanceState == null){


            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = inflater.inflate(R.layout.fragment_add_folder_dialog, null);
            addFolderDialog.setView(convertView);

            View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mFolder != null ? getString(R.string.title_edit_folder) : getString(R.string.title_add_folder));
            addFolderDialog.setCustomTitle(titleView);

            mFolderEditText = (EditText)convertView.findViewById(R.id.edit_text_add_category);


            addFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addFolderDialog.setPositiveButton(mFolder != null ? getString(R.string.label_update) : getString(R.string.label_add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });



            if (mFolder != null && !TextUtils.isEmpty(mFolder.getFolderName())){
                populateFields(mFolder);
                mFolderEditText.setSelection(mFolderEditText.getText().length());
            }


        }

        return addFolderDialog.create();
    }

    private void populateFields(Folder folder) {
        mFolderEditText.setText(folder.getFolderName());
    }

    private boolean requiredFieldCompleted(){
        if (TextUtils.isEmpty(mFolderEditText.getText().toString()))
        {
            mFolderEditText.setError(getString(R.string.required));
            mFolderEditText.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();


        if (d != null){
            Button positiveButton = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean readyToCloseDialog = false;
                    if (requiredFieldCompleted()) {
                        saveFolder();
                        readyToCloseDialog = true;
                    }
                    if (readyToCloseDialog)
                        dismiss();
                }
            });
        }
    }



    private void saveFolder() {
        final String categoryName = mFolderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(categoryName)) {
            if (mFolder == null){
                mFolder = folderDao.createNewFolder();
            }
            folderDao.updatedFolderTitle(mFolder.getId(), categoryName);
            EventBus.getDefault().post(new FolderAddedEvent(mFolder.getId()));
        }

    }



}

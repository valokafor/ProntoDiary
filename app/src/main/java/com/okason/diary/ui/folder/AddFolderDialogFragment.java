package com.okason.diary.ui.folder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.models.Folder;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFolderDialogFragment extends DialogFragment {
    private EditText mFolderEditText;
    private boolean mInEditMode = false;

    private String categoryId = "";

    private Folder mFolder = null;
    private DatabaseReference mDatabase;
    private DatabaseReference folderCloudReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;




    public AddFolderDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        folderCloudReference =  mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.FOLDER_CLOUD_END_POINT);

    }




    public static AddFolderDialogFragment newInstance(String jsonFolder){
        AddFolderDialogFragment dialogFragment = new AddFolderDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SERIALIZED_FOLDER, jsonFolder);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    /**
     * The method gets the Folder that was passed in, in the form of serialized String
     */
    public void getCurrentFolder(){
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.SERIALIZED_FOLDER)){
            String jsonFolder = args.getString(Constants.SERIALIZED_FOLDER);
            if (!TextUtils.isEmpty(jsonFolder)){
                Gson gson = new Gson();
                mFolder = gson.fromJson(jsonFolder, Folder.class);
            }
            if (mFolder != null){
                mInEditMode = true;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder addFolderDialog = new AlertDialog.Builder(getActivity());

        getCurrentFolder();
        if (savedInstanceState == null){


            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = inflater.inflate(R.layout.fragment_add_folder_dialog, null);
            addFolderDialog.setView(convertView);

            View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mInEditMode == true ? getString(R.string.edit_folder) : getString(R.string.add_folder));
            addFolderDialog.setCustomTitle(titleView);

            mFolderEditText = (EditText)convertView.findViewById(R.id.edit_text_add_category);


            addFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addFolderDialog.setPositiveButton(mInEditMode == true ? "Update" : "Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

            if (mFolder != null && !TextUtils.isEmpty(mFolder.getFolderName())){
                populateFields(mFolder);
                addFolderDialog.setTitle(mFolder.getFolderName());
            }


        }

        return addFolderDialog.create();
    }

    private void populateFields(Folder category) {
        mFolderEditText.setText(category.getFolderName());
    }

    private boolean requiredFieldCompleted(){
        if (mFolderEditText.getText().toString().isEmpty())
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
        if (mInEditMode){
            mFolder.setFolderName(categoryName);
            mFolder.setDateModified(System.currentTimeMillis());
            folderCloudReference.child(mFolder.getId()).setValue(mFolder);
            EventBus.getDefault().post(new FolderAddedEvent(mFolder));
        } else {
            mFolder = new Folder();
            mFolder.setFolderName(categoryName);
            mFolder.setId(folderCloudReference.push().getKey());
            folderCloudReference.child(mFolder.getId()).setValue(mFolder);
            EventBus.getDefault().post(new FolderAddedEvent(mFolder));
        }

    }


}

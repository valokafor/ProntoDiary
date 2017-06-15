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
    private DatabaseReference categoryCloudReference;

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

        categoryCloudReference =  mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.CATEGORY_CLOUD_END_POINT);

    }




    public static AddFolderDialogFragment newInstance(String jsonCategory){
        AddFolderDialogFragment dialogFragment = new AddFolderDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SERIALIZED_CATEGORY, jsonCategory);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    /**
     * The method gets the Category that was passed in, in the form of serialized String
     */
    public void getCurrentCategory(){
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.SERIALIZED_CATEGORY)){
            String jsonCategory = args.getString(Constants.SERIALIZED_CATEGORY);
            if (!TextUtils.isEmpty(jsonCategory)){
                Gson gson = new Gson();
                mFolder = gson.fromJson(jsonCategory, Folder.class);
            }
            if (mFolder != null){
                mInEditMode = true;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder addCategoryDialog = new AlertDialog.Builder(getActivity());

        getCurrentCategory();
        if (savedInstanceState == null){


            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = inflater.inflate(R.layout.fragment_add_folder_dialog, null);
            addCategoryDialog.setView(convertView);

            View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mInEditMode == true ? getString(R.string.edit_folder) : getString(R.string.add_folder));
            addCategoryDialog.setCustomTitle(titleView);

            mFolderEditText = (EditText)convertView.findViewById(R.id.edit_text_add_category);


            addCategoryDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addCategoryDialog.setPositiveButton(mInEditMode == true ? "Update" : "Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

            if (mFolder != null && !TextUtils.isEmpty(mFolder.getFolderName())){
                populateFields(mFolder);
                addCategoryDialog.setTitle(mFolder.getFolderName());
            }


        }

        return addCategoryDialog.create();
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
                        saveCategory();
                        readyToCloseDialog = true;
                    }
                    if (readyToCloseDialog)
                        dismiss();
                }
            });
        }
    }



    private void saveCategory() {
        final String categoryName = mFolderEditText.getText().toString().trim();
        if (mInEditMode){
            mFolder.setFolderName(categoryName);
            mFolder.setDateModified(System.currentTimeMillis());
            categoryCloudReference.child(mFolder.getId()).setValue(mFolder);
            EventBus.getDefault().post(new FolderAddedEvent(mFolder));
        } else {
            mFolder = new Folder();
            mFolder.setFolderName(categoryName);
            mFolder.setId(categoryCloudReference.push().getKey());
            categoryCloudReference.child(mFolder.getId()).setValue(mFolder);
            EventBus.getDefault().post(new FolderAddedEvent(mFolder));
        }

    }


}

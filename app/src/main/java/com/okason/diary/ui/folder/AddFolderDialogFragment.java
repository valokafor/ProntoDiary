package com.okason.diary.ui.folder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.models.Folder;
import com.okason.diary.ui.addnote.DataAccessManager;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFolderDialogFragment extends DialogFragment {
    private EditText mFolderEditText;
    private Folder mFolder = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DataAccessManager dataAccessManager;





    public AddFolderDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        dataAccessManager = new DataAccessManager(firebaseUser.getUid());
    }




    public static AddFolderDialogFragment newInstance(String content){
        AddFolderDialogFragment dialogFragment = new AddFolderDialogFragment();
        Bundle args = new Bundle();

        if (!TextUtils.isEmpty(content)){
            args.putString(Constants.SERIALIZED_FOLDER, content);
            dialogFragment.setArguments(args);
        }
        return dialogFragment;
    }

    /**
     * The method gets the Folder that was passed in
     */
    public void getCurrentFolder(){
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.SERIALIZED_FOLDER)){
            String serializedFolder = args.getString(Constants.SERIALIZED_FOLDER, "");
            if (!TextUtils.isEmpty(serializedFolder)){
                Gson gson = new Gson();
                mFolder = gson.fromJson(serializedFolder, new TypeToken<Folder>(){}.getType());
            }
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

    private void populateFields(Folder category) {
        mFolderEditText.setText(category.getFolderName());
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
        final String folderName = mFolderEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(folderName)) {
            if (mFolder == null){
                mFolder = new Folder();
                mFolder.setFolderName(folderName);
                mFolder.setDateCreated(System.currentTimeMillis());
                mFolder.setDateModified(System.currentTimeMillis());
                dataAccessManager.getFolderPath().add(mFolder).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            mFolder.setId(task.getResult().getId());
                            dataAccessManager.getFolderPath().document(mFolder.getId()).set(mFolder);
                            EventBus.getDefault().post(new FolderAddedEvent(mFolder));
                            dataAccessManager.getAllFolder();
                        }
                    }
                });
            }else {
                mFolder.setFolderName(folderName);
                mFolder.setDateModified(System.currentTimeMillis());
                dataAccessManager.getFolderPath().document(mFolder.getId()).set(mFolder);
                EventBus.getDefault().post(new FolderAddedEvent(mFolder));
                dataAccessManager.getAllFolder();
            }


        }

    }


}

package com.okason.diary.ui.tag;


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
import com.okason.diary.core.events.TagAddedEvent;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTagDialogFragment extends DialogFragment {

    private EditText tagEditText;
    private boolean mInEditMode = false;

    private String categoryId = "";

    private Tag mTag = null;
    private DatabaseReference mDatabase;
    private DatabaseReference tagCloudReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    public AddTagDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tagCloudReference =  mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.TAG_CLOUD_END_POINT);

    }

    public static AddTagDialogFragment newInstance(String serializedTag){
        AddTagDialogFragment dialogFragment = new AddTagDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SERIALIZED_TAG, serializedTag);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }



    /**
     * The method gets the Tag that was passed in, in the form of serialized String
     */
    public void getCurrentTag(){
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.SERIALIZED_TAG)){
            String jsonTag = args.getString(Constants.SERIALIZED_TAG);
            if (!TextUtils.isEmpty(jsonTag)){
                Gson gson = new Gson();
                mTag = gson.fromJson(jsonTag, Tag.class);
            }
            if (mTag != null){
                mInEditMode = true;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder addTagDialog = new AlertDialog.Builder(getActivity());

        getCurrentTag();
        if (savedInstanceState == null){


            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = inflater.inflate(R.layout.fragment_add_tag_dialog, null);
            addTagDialog.setView(convertView);

            View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mInEditMode == true ? getString(R.string.title_edit_tag) : getString(R.string.title_add_tag));
            addTagDialog.setCustomTitle(titleView);

            tagEditText = (EditText)convertView.findViewById(R.id.edit_text_add_category);


            addTagDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addTagDialog.setPositiveButton(mInEditMode == true ? "Update" : "Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

            if (mTag != null && !TextUtils.isEmpty(mTag.getTagName())){
                populateFields(mTag);
                addTagDialog.setTitle(mTag.getTagName());
            }


        }

        return addTagDialog.create();
    }

    private void populateFields(Tag category) {
        tagEditText.setText(category.getTagName());
    }

    private boolean requiredFieldCompleted(){
        if (tagEditText.getText().toString().isEmpty())
        {
            tagEditText.setError(getString(R.string.required));
            tagEditText.requestFocus();
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
                        saveTag();
                        readyToCloseDialog = true;
                    }
                    if (readyToCloseDialog)
                        dismiss();
                }
            });
        }
    }



    private void saveTag() {
        final String categoryName = tagEditText.getText().toString().trim();
        if (mInEditMode){
            mTag.setTagName(categoryName);
        } else {
            mTag = new Tag();
            mTag.setTagName(categoryName);
            mTag.setId(tagCloudReference.push().getKey());
        }

        mTag.setDateModified(System.currentTimeMillis());
        tagCloudReference.child(mTag.getId()).setValue(mTag);
        EventBus.getDefault().post(new TagAddedEvent(mTag));

    }


}

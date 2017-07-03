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

import com.okason.diary.R;
import com.okason.diary.data.TagRealmRepository;
import com.okason.diary.models.Tag;
import com.okason.diary.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTagDialogFragment extends DialogFragment {

    private EditText tagEditText;
    private Tag mTag = null;



    public AddTagDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static AddTagDialogFragment newInstance(String tagId){
        AddTagDialogFragment dialogFragment = new AddTagDialogFragment();
        if (!TextUtils.isEmpty(tagId)) {
            Bundle args = new Bundle();
            args.putString(Constants.TAG_ID, tagId);
            dialogFragment.setArguments(args);
        }
        return dialogFragment;
    }

    /**
     * The method gets the Folder that was passed in, in the form of serialized String
     */
    public void getCurrentTag(){
        if (getArguments() != null && getArguments().containsKey(Constants.TAG_ID)){
            String tagId = getArguments().getString(Constants.TAG_ID);
            if (!TextUtils.isEmpty(tagId)){
                mTag = new TagRealmRepository().getTagById(tagId);

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
            titleText.setText(mTag != null ? getString(R.string.title_edit_tag) : getString(R.string.title_add_tag));
            addTagDialog.setCustomTitle(titleView);

            tagEditText = (EditText)convertView.findViewById(R.id.edit_text_add_category);


            addTagDialog.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addTagDialog.setPositiveButton(mTag != null ? getString(R.string.label_update) : getString(R.string.label_add), new DialogInterface.OnClickListener() {
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
        final String tagName = tagEditText.getText().toString().trim();
        if (mTag == null){
            mTag = new TagRealmRepository().createNewTag();
        }
        new TagRealmRepository().updatedTagTitle(mTag.getId(), tagName);
    }



}

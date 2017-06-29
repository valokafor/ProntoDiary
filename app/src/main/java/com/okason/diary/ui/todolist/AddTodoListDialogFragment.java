package com.okason.diary.ui.todolist;


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
import com.okason.diary.data.TodoListRealmRepository;
import com.okason.diary.models.TodoList;
import com.okason.diary.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTodoListDialogFragment extends DialogFragment {
    private EditText mTodoListEditText;
    private TodoList mTodoList = null;




    public AddTodoListDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }




    public static AddTodoListDialogFragment newInstance(String todoListId){
        AddTodoListDialogFragment dialogFragment = new AddTodoListDialogFragment();
        if (!TextUtils.isEmpty(todoListId)) {
            Bundle args = new Bundle();
            args.putString(Constants.TODO_LIST_ID, todoListId);
            dialogFragment.setArguments(args);
        }
        return dialogFragment;
    }

    /**
     * The method gets the Folder that was passed in, in the form of serialized String
     */
    public void getPassedInTodoListItem(){
        if (getArguments() != null && getArguments().containsKey(Constants.TODO_LIST_ID)){
            String todoListId = getArguments().getString(Constants.TODO_LIST_ID);
            if (!TextUtils.isEmpty(todoListId)){
                mTodoList = new TodoListRealmRepository().getTodoListById(todoListId);

            }
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder addFolderDialog = new AlertDialog.Builder(getActivity(), R.style.dialog);

        getPassedInTodoListItem();
        if (savedInstanceState == null){


            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = inflater.inflate(R.layout.fragment_add_todo_list_dialog, null);
            addFolderDialog.setView(convertView);

            View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
            titleText.setText(mTodoList != null ? getString(R.string.title_edit_todo_list) : getString(R.string.title_add_todo_list));
            addFolderDialog.setCustomTitle(titleView);

            mTodoListEditText = (EditText)convertView.findViewById(R.id.edit_text_add_todo_list);


            addFolderDialog.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            addFolderDialog.setPositiveButton(mTodoList != null ? getString(R.string.label_update) : getString(R.string.label_add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });



            if (mTodoList != null && !TextUtils.isEmpty(mTodoList.getTitle())){
                populateFields(mTodoList);
                addFolderDialog.setTitle(mTodoList.getTitle());
            }


        }

        return addFolderDialog.create();
    }

    private void populateFields(TodoList todoList) {
        mTodoListEditText.setText(todoList.getTitle());
    }

    private boolean requiredFieldCompleted(){
        if (mTodoListEditText.getText().toString().isEmpty())
        {
            mTodoListEditText.setError(getString(R.string.required));
            mTodoListEditText.requestFocus();
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
        final String todoListName = mTodoListEditText.getText().toString().trim();
        if (mTodoList == null){
            mTodoList = new TodoListRealmRepository().createNewTodoListItem(todoListName);
        }
        new TodoListRealmRepository().updateTodoListItemName(mTodoList.getId(), todoListName);

    }


}

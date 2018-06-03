package com.okason.diary.ui.todolist;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.SubTaskItemListener;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Reminder;
import com.okason.diary.reminder.TextFormatUtil;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmObjectChangeListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSubTaskFragment extends Fragment implements SubTaskItemListener {
    private final static String TAG = "AddSubTaskFragment";

    private View rootView;
    @BindView(R.id.text_view_edit_task_label)
    TextView editTaskTextView;


    @BindView(R.id.text_view_due_date) TextView dueDateTextView;
    @BindView(R.id.text_view_repeat) TextView repeatTextView;
    @BindView(R.id.text_view_priority) TextView priorityTextView;
    @BindView(R.id.text_view_folder) TextView folderTextView;
    @BindView(R.id.edit_text_add_sub_task)
    EditText addSubTaskEditText;

    @BindView(R.id.sub_task_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text) TextView mEmptyText;

    private SubTaskListAdapter subTaskListAdapter;
    private boolean shouldUpdateAdapter = true;
    private ProntoTask parentProntoTask;
    private Realm realm;
    private TaskDao taskDao;




    public AddSubTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static AddSubTaskFragment newInstance(String taskId){
        AddSubTaskFragment fragment = new AddSubTaskFragment();
        if (!TextUtils.isEmpty(taskId)){
            Bundle args = new Bundle();
            args.putString(Constants.TASK_ID, taskId);
            fragment.setArguments(args);
        }

        return fragment;
    }


    /**
     * The method gets the parent ProntoTask that was passed in
     */
    public void getParentTask(){
        if (getArguments() != null && getArguments().containsKey(Constants.TASK_ID)){
            String taskId = getArguments().getString(Constants.TASK_ID);
            if (!TextUtils.isEmpty(taskId)){
                parentProntoTask = taskDao.getTaskById(taskId);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add_sub_task, container, false);
        realm = Realm.getDefaultInstance();
        taskDao = new TaskDao(realm);

        ButterKnife.bind(this, rootView);
        getParentTask();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parentProntoTask != null){
            populateTaskDetails(parentProntoTask);
            showSubTasks(parentProntoTask);
            parentProntoTask.addChangeListener(taskChangeListener);
        }

    }

    @Override
    public void onPause() {
        parentProntoTask.removeAllChangeListeners();
        super.onPause();
    }

    private void showSubTasks(ProntoTask prontoTask) {
        if (prontoTask != null && prontoTask.getSubTask().size() > 0){
            showEmptyText(false);
            subTaskListAdapter = new SubTaskListAdapter(prontoTask.getSubTask(), this);
            mRecyclerView.setAdapter(subTaskListAdapter);
        }else {
            showEmptyText(true);
        }
    }

    @OnClick(R.id.button_add_task)
    public void onAddSubTaskButtonClicked(View view){
        if (TextUtils.isEmpty(addSubTaskEditText.getText())){
            makeToast(getString(R.string.missing_title));
            addSubTaskEditText.setError(getString(R.string.required));
            return;
        }
        String subTaskText = addSubTaskEditText.getText().toString();
        taskDao.createNewSubTask(subTaskText, parentProntoTask.getId());
        addSubTaskEditText.setText("");
    }

    private void populateTaskDetails(ProntoTask parentProntoTask) {
        Reminder reminder = parentProntoTask.getReminder();
        String dueDate = null;
        try {
            dueDate = TimeUtils.getReadableModifiedDateWithTime(parentProntoTask.getReminder().getDateAndTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(dueDate)){
            dueDateTextView.setText(dueDate);
        } else {
            dueDateTextView.setVisibility(View.GONE);
        }

        int repeatType = reminder.getRepeatType();
        int interval = reminder.getInterval();
        String repeatText = "";
        if ( repeatType != Constants.DOES_NOT_REPEAT) {
            if (interval> 1) {
                repeatText = TextFormatUtil.formatAdvancedRepeatText(getContext(), repeatType, interval);
                repeatTextView.setText(repeatText);
            } else {
                repeatText = getResources().getStringArray(R.array.repeat_array)[repeatType];
                repeatTextView.setText(repeatText);
            }
        }

//        if (reminder.getRepeatType() == Constants.SPECIFIC_DAYS) {
//            repeatText = TextFormatUtil.formatDaysOfWeekText(getContext(), reminder.getDaysOfWeek());
//            repeatTextView.setText(repeatText);
//        }

        if (reminder.getRepeatType() == Constants.SPECIFIC_DAYS) {
            List<Boolean> daysOfWeek = reminder.getDaysOfWeekList();
            repeatTextView.setText(TextFormatUtil.formatDaysOfWeekText(getContext(), daysOfWeek));
        }

        try {
            int priority = parentProntoTask.getPriority();
            if (priority > 0){
                switch (priority){
                    case Constants.PRIORITY_LOW:
                        priorityTextView.setText(getString(R.string.label_low_priotity));
                        break;
                    case Constants.PRIORITY_MEDIUM:
                        priorityTextView.setText(getString(R.string.label_medium_priority));
                        break;
                    case Constants.PRIORITY_HIGH:
                        priorityTextView.setText(getString(R.string.label_high_priority));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            priorityTextView.setVisibility(View.GONE);
        }

        String folderName = null;
        try {
            folderName = parentProntoTask.getFolder().getFolderName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(folderName)){
            folderTextView.setText(folderName);
        } else {
            folderTextView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.text_view_edit_task_label)
    public void onClickEditTaskTextView(View view){
        if (parentProntoTask != null){
            Intent editTaskIntent = new Intent(getActivity(), AddTaskActivity.class);
            editTaskIntent.putExtra(Constants.TASK_ID, parentProntoTask.getId());
            startActivity(editTaskIntent);
        }else {
            makeToast(getString(R.string.no_parent_task_found));
        }
    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public void showEmptyText(boolean showText) {
        if (showText){
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
            //  mAdView.setVisibility(View.GONE);

        }else {
            //  mAdView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);

        }

    }

    @Override
    public void onSubTaskChecked(String subTaskId) {
        shouldUpdateAdapter = false;
        taskDao.updateSubTaskStatus(parentProntoTask.getId(), subTaskId, true);
    }


    @Override
    public void onSubTaskUnChecked(String subTaskId) {
        shouldUpdateAdapter = false;
        taskDao.updateSubTaskStatus(parentProntoTask.getId(), subTaskId, false);

    }


    @Override
    public void onSubTaskDeleted(String subTaskId) {
       taskDao.deleteSubTask(subTaskId);
    }

    @Override
    public void onEditSubTask(SubTask subTask) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private final RealmObjectChangeListener<ProntoTask> taskChangeListener = new RealmObjectChangeListener<ProntoTask>() {
        @Override
        public void onChange(ProntoTask prontoTask, @javax.annotation.Nullable ObjectChangeSet changeSet) {

            if (changeSet == null){
                return;
            }
            if (changeSet.isDeleted()){
                return;
            }

            for (String fieldName: changeSet.getChangedFields()){
                if (fieldName.equals("subTask")){
                    Log.i(TAG, "Field " + fieldName + " was changed.");
                    showSubTasks(prontoTask);
                }
            }

        }
    };
}

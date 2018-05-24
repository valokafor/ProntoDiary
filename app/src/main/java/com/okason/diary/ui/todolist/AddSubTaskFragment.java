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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.TaskChangedEvent;
import com.okason.diary.core.listeners.SubTaskItemListener;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.realmentities.SubTask;
import com.okason.diary.models.realmentities.Task;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSubTaskFragment extends Fragment implements SubTaskItemListener {

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
    private Task parentTask;
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
     * The method gets the parent Task that was passed in
     */
    public void getParentTask(){
        if (getArguments() != null && getArguments().containsKey(Constants.TASK_ID)){
            String taskId = getArguments().getString(Constants.TASK_ID);
            if (!TextUtils.isEmpty(taskId)){
                parentTask = taskDao.getTaskById(taskId);
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
        if (parentTask != null){
            populateTaskDetails(parentTask);
            showSubTasks(parentTask);
        }

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
    public void onTaskChange(TaskChangedEvent event){
        parentTask = event.getChangedTask();
        populateTaskDetails(parentTask);
        showSubTasks(parentTask);
    }



    private void showSubTasks(Task task) {
        if (task != null && task.getSubTask().size() > 0){
            showEmptyText(false);
            subTaskListAdapter = new SubTaskListAdapter(task.getSubTask(), this);
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
        taskDao.createNewSubTask(subTaskText, parentTask.getId());
        addSubTaskEditText.setText("");
    }

    private void populateTaskDetails(Task parentTask) {
        String dueDate = null;
        try {
            dueDate = TimeUtils.getReadableModifiedDateWithTime(parentTask.getDueDateAndTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(dueDate)){
            dueDateTextView.setText(dueDate);
        } else {
            dueDateTextView.setVisibility(View.GONE);
        }

        String repeat = "";
        try {
            repeat = parentTask.getRepeatFrequency();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (repeat != null){
            if (repeat.equals(Constants.REMINDER_NO_REMINDER)) {
                repeatTextView.setText(getString(R.string.one_time_event));
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Repeats every  ");
                stringBuilder.append(repeat);
                stringBuilder.append(" until ");
                stringBuilder.append(TimeUtils.getReadableDateWithoutTime(parentTask.getRepeatEndDate()));
                repeatTextView.setText(stringBuilder.toString());
            }
        }else {
            //Create Notification text based on Notification preferences
            repeatTextView.setText("Notify 30 minutes before due time");
        }

        try {
            int priority = parentTask.getPriority();
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
            folderName = parentTask.getFolder().getFolderName();
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
        if (parentTask != null){
            Intent editTaskIntent = new Intent(getActivity(), AddTaskActivity.class);
            String serializedTask = new Gson().toJson(parentTask);
            editTaskIntent.putExtra(Constants.SERIALIZED_TASK, serializedTask);
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
        taskDao.updateSubTaskStatus(parentTask.getId(), subTaskId, true);
    }


    @Override
    public void onSubTaskUnChecked(String subTaskId) {
        shouldUpdateAdapter = false;
        taskDao.updateSubTaskStatus(parentTask.getId(), subTaskId, false);

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
}

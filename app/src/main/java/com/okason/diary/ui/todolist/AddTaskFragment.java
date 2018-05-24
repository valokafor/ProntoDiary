package com.okason.diary.ui.todolist;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.ValueEventListener;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.data.FolderDao;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.realmentities.FolderEntity;
import com.okason.diary.models.realmentities.TaskEntity;
import com.okason.diary.ui.folder.AddFolderDialogFragment;
import com.okason.diary.ui.folder.FolderListAdapter;
import com.okason.diary.ui.folder.SelectFolderDialogFragment;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.DateHelper;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends Fragment{
    private View mRootView;

    private FolderListAdapter mAdapter;

    @BindView(R.id.button_one_time_event)
    Button oneTimeEventButton;

    @BindView(R.id.button_reminder_hourly)
    Button hourlyReminderButton;

    @BindView(R.id.button_reminder_daily)
    Button dailyReminderButton;

    @BindView(R.id.button_reminder_weekly)
    Button weeklyReminderButton;

    @BindView(R.id.button_reminder_week_days)
    Button weekDaysReminderButton;

    @BindView(R.id.button_reminder_monthly)
    Button monthlyReminderButton;

    @BindView(R.id.button_reminder_yearly)
    Button yearlyReminderButton;

    @BindView(R.id.text_view_due_date) TextView dateTextView;
    @BindView(R.id.text_view_due_time) TextView timeTextView;
    @BindView(R.id.linear_layout_reminder_end_date)
    LinearLayout reminderEndDateLayout;

    @BindView(R.id.edit_text_repeat_end_date) TextView repeadEndDateEditText;
    @BindView(R.id.radio_group_priority)
    RadioGroup priorityRadioGroup;

    @BindView(R.id.button_low_priority)
    RadioButton lowPriorityButton;

    @BindView(R.id.button_medium_priority)
    RadioButton mediumPriorityButton;

    @BindView(R.id.button_high_priority)
    RadioButton highPriorityButton;











    @BindView(R.id.image_button_add_reminder_options)
    ImageButton addOtherReminderButton;
    private SelectFolderDialogFragment selectFolderDialogFragment;
    private AddFolderDialogFragment addFolderDialogFragment;

  ;
    private TaskEntity currentTask = null;
    private Realm realm;
    private TaskDao taskDao;

    private String repeatFrequency = "";
    private int priority = Constants.PRIORITY_LOW;
   // private Folder selectedFolder;
    private Calendar mReminderTime;
    private Calendar repeatEndDate;

    //Flag that indicates if the Add Sub Task screen should be opened
    private boolean shouldAddSubTasks = false;
    private ValueEventListener folderEventListener;




    @BindView(R.id.edit_text_category)
    EditText mFolder;

    @BindView(R.id.edit_text_task_name) EditText taskNameEditText;



    public AddTaskFragment() {
        // Required empty public constructor
    }

    private void getPassedInTask() {
        if (getArguments() != null && getArguments().containsKey(Constants.TASK_ID)){
            String taskId = getArguments().getString(Constants.TASK_ID);
            if (!TextUtils.isEmpty(taskId)){
                currentTask = taskDao.getTaskById(taskId);
            }
        }

    }

    public static AddTaskFragment newInstance(String taskId){
        AddTaskFragment fragment = new AddTaskFragment();
        if (!TextUtils.isEmpty(taskId)){
            Bundle args = new Bundle();
            args.putString(Constants.TASK_ID, taskId);
            fragment.setArguments(args);
        }

        return fragment;
    }







    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getPassedInTask();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, mRootView);



        if (currentTask == null) {

            oneTimeEventButton.performClick();

            //Show Today date on Task Due Date
            mReminderTime = Calendar.getInstance();
            mReminderTime.setTimeInMillis(System.currentTimeMillis());
            String formattedDueDate = TimeUtils.getReadableDateWithoutTime(mReminderTime.getTimeInMillis());
            dateTextView.setText(formattedDueDate);

            String formattedDueTime = DateHelper.getTimeShort(getActivity(), mReminderTime.getTimeInMillis());
            timeTextView.setText(formattedDueTime);

        }
        priorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.button_low_priority:
                        priority = Constants.PRIORITY_LOW;
                        break;
                    case R.id.button_medium_priority:
                        priority = Constants.PRIORITY_MEDIUM;
                        break;
                    case R.id.button_high_priority:
                        priority = Constants.PRIORITY_HIGH;
                        break;
                }
            }
        });
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (currentTask != null){
            showTaskDetail(currentTask);
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_save:
                onSaveTaskButtonClicked();
                return true;
        }
        return false;
    }

    private void onSaveTaskButtonClicked() {



        if (TextUtils.isEmpty(taskNameEditText.getText())) {
            taskNameEditText.setError(getString(R.string.required));
            return;
        }

        if (currentTask == null) {
            currentTask = taskDao.createNewTask(taskNameEditText.getText().toString());
        }

        if (TextUtils.isEmpty(repeatFrequency)) {
            repeatFrequency = Constants.REMINDER_NO_REMINDER;
        }

        if (mReminderTime == null) {
            mReminderTime = Calendar.getInstance();
            mReminderTime.setTimeInMillis(System.currentTimeMillis());
        }

        if (repeatEndDate == null) {
            repeatEndDate = Calendar.getInstance();
            repeatEndDate.setTimeInMillis(System.currentTimeMillis());
        }

        if (currentTask.getFolder() == null){
            FolderEntity selectedFolder = new FolderDao(realm).getOrCreateFolder(getString(R.string.general));
            taskDao.setFolder(currentTask.getId(), selectedFolder.getId());
        }



        taskDao.updateTask(currentTask.getId(), priority,
                taskNameEditText.getText().toString(),
                mReminderTime.getTimeInMillis(),
                repeatFrequency,
                repeatEndDate.getTimeInMillis());


    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewFolder(FolderAddedEvent event){
        addFolderDialogFragment.dismiss();
        if (!TextUtils.isEmpty(event.getAddedFolderId())){
            FolderEntity selectedFolder = new FolderDao(realm).getFolderById(event.getAddedFolderId());
            taskDao.setFolder(currentTask.getId(), selectedFolder.getId());
            mFolder.setText(selectedFolder.getFolderName());
        }

    }

    private void onRepeatEndDateSelected(Calendar selectedEndDate){
        if (selectedEndDate.before(mReminderTime)){
            makeToast(getString(R.string.repeat_date_before_due_date));
            return;
        }

        repeatEndDate = selectedEndDate;
        String repeatUntilDate = TimeUtils.getReadableDateWithoutTime(selectedEndDate.getTimeInMillis());
        repeadEndDateEditText.setText(repeatUntilDate);
    }

    @OnClick(R.id.edit_text_category)
    public void showSelectFolder(){
        showChooseFolderDialog();
    }

    @OnClick(R.id.edit_text_repeat_end_date)
    public void showRepeatEndDate(){
        showReminderEndDate();
    }

    @OnClick(R.id.image_button_calendar)
    public void onClickAddDueDateButton(View view){
        showReminderDate();
    }

    @OnClick(R.id.image_button_time)
    public void onClickAddDueTimeButton(View view){
        showReminderTime();
    }

    @OnClick(R.id.image_button_add_sub_tasks)
    public void onAddSubTaskButtonClicked(View view){
        handleAddSubTaskButtonClicked();
    }

    //The click of the TextView and the button of AddSubTask
    //Will trigger the same functi
    @OnClick(R.id.text_view_add_sub_tasks)
    public void onAddSubTaskTextViewClicked(View view){
        handleAddSubTaskButtonClicked();
    }

    private void onReminderDateSelected(){
        String formattedDueDate = TimeUtils.getReadableDateWithoutTime(mReminderTime.getTimeInMillis());
        dateTextView.setText(formattedDueDate);
    }

    private void onReminderTimeSelected(){
        String formattedDueTime = DateHelper.getTimeShort(getActivity(), mReminderTime.getTimeInMillis());
        timeTextView.setText(formattedDueTime);
    }

    private void handleAddSubTaskButtonClicked(){
        shouldAddSubTasks = true;
        onSaveTaskButtonClicked();
    }




    @OnClick(R.id.button_one_time_event)
    public void onClickOneTimeEventButton(View view){
        resetReminderButtons();
        repeatFrequency = Constants.REMINDER_NO_REMINDER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oneTimeEventButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            oneTimeEventButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_reminder_hourly)
    public void onClickHourlyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Constants.REMINDER_HOURLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hourlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            hourlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }


    @OnClick(R.id.button_reminder_daily)
    public void onClickDailyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Constants.REMINDER_DAILY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dailyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            dailyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.button_reminder_weekly)
    public void onClickWeeklyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Constants.REMINDER_WEEKLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weeklyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            weeklyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.button_reminder_week_days)
    public void onClickWeekDaysReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Constants.REMINDER_WEEK_DAYS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weekDaysReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            weekDaysReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_reminder_monthly)
    public void onClickMonthlyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Constants.REMINDER_MONTHLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            monthlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            monthlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.button_reminder_yearly)
    public void onClickYearlyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Constants.REMINDER_YEARLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            yearlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            yearlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.image_button_add_reminder_options)
    public void onClickAddReminderOptions(View view){
        resetReminderButtons();
        makeToast("Not implemented yet");

    }


    private void resetReminderButtons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oneTimeEventButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
            oneTimeEventButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hourlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
            hourlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dailyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
            dailyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weeklyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
            weeklyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weekDaysReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
            weekDaysReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            monthlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
            monthlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            yearlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.white));
            yearlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text));
        }
    }



    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void showChooseFolderDialog() {
        selectFolderDialogFragment = selectFolderDialogFragment.newInstance();



        selectFolderDialogFragment.setCategorySelectedListener(new OnFolderSelectedListener() {
            @Override
            public void onCategorySelected(FolderEntity selectedFolder) {
                selectFolderDialogFragment.dismiss();
                mFolder.setText(selectedFolder.getFolderName());
                selectedFolder = selectedFolder;
            }

            @Override
            public void onEditCategoryButtonClicked(FolderEntity selectedCategory) {

            }

            @Override
            public void onDeleteCategoryButtonClicked(FolderEntity selectedCategory) {

            }

            @Override
            public void onAddCategoryButtonClicked() {
                showAddNewFolderDialog();
            }
        });
        selectFolderDialogFragment.show(getActivity().getFragmentManager(), "Dialog");
    }

    public void showAddNewFolderDialog() {
        if (selectFolderDialogFragment != null) {
            selectFolderDialogFragment.dismiss();
        }
        addFolderDialogFragment = AddFolderDialogFragment.newInstance("");
        addFolderDialogFragment.show(getActivity().getFragmentManager(), "Dialog");

    }


    /**
     * This method populates the AddTask screen
     * When it is in Edit mode
     * @param task - the passed in Task
     */
    public void showTaskDetail(TaskEntity task) {
        try {
            taskNameEditText.setText(task.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String formattedDueDate = TimeUtils.getReadableDateWithoutTime(task.getDueDateAndTime());
            dateTextView.setText(formattedDueDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String formattedDueTime = DateHelper.getTimeShort(getActivity(), task.getDueDateAndTime());
            timeTextView.setText(formattedDueTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int priority = task.getPriority();
            if (priority > 0){
                switch (priority){
                    case Constants.PRIORITY_LOW:
                        lowPriorityButton.setChecked(true);
                        break;
                    case Constants.PRIORITY_MEDIUM:
                        mediumPriorityButton.setChecked(true);
                        break;
                    case Constants.PRIORITY_HIGH:
                        highPriorityButton.setChecked(true);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String frequency = "";
        try {
            frequency = task.getRepeatFrequency();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(frequency)){

            switch (frequency){
                case Constants.REMINDER_HOURLY:
                    hourlyReminderButton.performClick();
                    break;
                case Constants.REMINDER_DAILY:
                    dailyReminderButton.performClick();
                    break;
                case Constants.REMINDER_WEEKLY:
                    weeklyReminderButton.performClick();
                    break;
                case Constants.REMINDER_WEEK_DAYS:
                    weekDaysReminderButton.performClick();
                    break;
                case Constants.REMINDER_YEARLY:
                    yearlyReminderButton.performClick();
                    break;
                case Constants.REMINDER_MONTHLY:
                    monthlyReminderButton.performClick();
                    break;


            }
        }

        try {
            mFolder.setText(task.getFolder().getFolderName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Show the repeat end date of the Frequency is not a single event
        if (!task.getRepeatFrequency().equals(Constants.REMINDER_NO_REMINDER)){
            reminderEndDateLayout.setVisibility(View.VISIBLE);
            repeatEndDate = Calendar.getInstance();
            if (task.getRepeatEndDate() > 0){
                repeatEndDate.setTimeInMillis(task.getRepeatEndDate());
            }
            onRepeatEndDateSelected(repeatEndDate);
        }


    }



    public void showReminderDate() {
        DialogFragment reminderDatePicker = new ReminderDatePickerDialogFragment();
        reminderDatePicker.setTargetFragment(AddTaskFragment.this, 0);
        reminderDatePicker.show(getFragmentManager(), "reminderDatePicker");

    }

    public void showReminderEndDate() {
        DialogFragment reminderDatePicker = new ReminderEndDatePickerDialogFragment();
        reminderDatePicker.setTargetFragment(AddTaskFragment.this, 0);
        reminderDatePicker.show(getFragmentManager(), "reminderDatePicker");

    }


    public void showReminderTime() {
        DialogFragment reminderTimePicker = new ReminderTimePickerDialogFragment();
        reminderTimePicker.setTargetFragment(AddTaskFragment.this, 0);
        reminderTimePicker.show(getFragmentManager(), "reminderTimePicker");

    }

    public static class ReminderEndDatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.setTitle("Select Due Date");
            return datePickerDialog;
        }
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            AddTaskFragment targetFragment = (AddTaskFragment) getTargetFragment();
            if (year < 0){
                targetFragment = null;
            } else {
                Calendar repeatEndDate = Calendar.getInstance();
                repeatEndDate.set(year, monthOfYear, dayOfMonth);
                targetFragment.onRepeatEndDateSelected(repeatEndDate);

            }

        }

    }



    public static class ReminderDatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            AddTaskFragment targetFragment = (AddTaskFragment) getTargetFragment();
            if (year < 0){
                targetFragment = null;
            } else {
                targetFragment.mReminderTime = Calendar.getInstance();
                targetFragment.mReminderTime.set(year, monthOfYear, dayOfMonth);
                targetFragment.onReminderDateSelected();

            }

        }

    }


    public void goBackToParent() {
        Intent parentIntent = new Intent(getActivity(), TodoListActivity.class);
        startActivity(parentIntent);
    }

    public static class ReminderTimePickerDialogFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AddTaskFragment targetFragment = (AddTaskFragment) getTargetFragment();
            final Calendar c;
            if (targetFragment.mReminderTime == null) {
                c = Calendar.getInstance();
            } else {
                c = targetFragment.mReminderTime;
            }

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            AddTaskFragment targetFragment = (AddTaskFragment) getTargetFragment();
            if (targetFragment.mReminderTime == null){
                targetFragment.mReminderTime = Calendar.getInstance();
            }
            targetFragment.mReminderTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            targetFragment.mReminderTime.set(Calendar.MINUTE, minute);
            targetFragment.onReminderTimeSelected();

        }

    }


}

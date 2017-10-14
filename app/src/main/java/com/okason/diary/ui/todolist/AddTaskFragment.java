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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Task;
import com.okason.diary.ui.folder.AddFolderDialogFragment;
import com.okason.diary.ui.folder.FolderListAdapter;
import com.okason.diary.ui.folder.SelectFolderDialogFragment;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.date.DateHelper;
import com.okason.diary.utils.date.TimeUtils;
import com.okason.diary.utils.reminder.Reminder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private List<Folder> folderList;
    private DatabaseReference database;
    private DatabaseReference folderCloudReference;
    private DatabaseReference taskCloudReference;





    @BindView(R.id.image_button_add_reminder_options)
    ImageButton addOtherReminderButton;
    private SelectFolderDialogFragment selectFolderDialogFragment;
    private AddFolderDialogFragment addFolderDialogFragment;

  ;
    private Task currentTast = null;

    private Reminder repeatFrequency;
    private int priority = Constants.PRIORITY_LOW;
    private Folder selectedFolder;
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
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.SERIALIZED_TASK)){
            String serializedTask = args.getString(Constants.SERIALIZED_TASK, "");
            if (!TextUtils.isEmpty(serializedTask)){
                Gson gson = new Gson();
                currentTast = gson.fromJson(serializedTask, new TypeToken<Task>(){}.getType());
            }
        }

    }

    public static AddTaskFragment newInstance(String content){
        AddTaskFragment fragment = new AddTaskFragment();
        if (!TextUtils.isEmpty(content)){
            Bundle args = new Bundle();
            args.putString(Constants.SERIALIZED_TASK, content);
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
        folderList = new ArrayList<>();


        if (currentTast == null) {

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            database = FirebaseDatabase.getInstance().getReference();
            taskCloudReference = database.child(Constants.USERS_CLOUD_END_POINT + firebaseUser.getUid() + Constants.TASK_CLOUD_END_POINT);
            folderCloudReference =  database.child(Constants.USERS_CLOUD_END_POINT + firebaseUser.getUid() + Constants.FOLDER_CLOUD_END_POINT);
        }

        folderEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    folderList.clear();
                    for (DataSnapshot folderSnapshot: dataSnapshot.getChildren()){
                        Folder folder = folderSnapshot.getValue(Folder.class);
                        folderList.add(folder);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        folderCloudReference.addValueEventListener(folderEventListener);


        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        folderCloudReference.removeEventListener(folderEventListener);

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
                break;
        }
        return true;
    }

    private void onSaveTaskButtonClicked(){
        if (currentTast == null){
            currentTast = new Task();
            String key = taskCloudReference.push().getKey();
            currentTast.setId(key);
        }


        if (TextUtils.isEmpty(taskNameEditText.getText())){
            taskNameEditText.setError(getString(R.string.required));
            return;
        }

        if (repeatFrequency == null){
            repeatFrequency = Reminder.NO;
        }

        if (selectedFolder == null) {
            selectedFolder = getDefaultFolder(Constants.DEFAULT_CATEGORY);
        }
        currentTast.setFolder(selectedFolder);


        if (mReminderTime == null){
            mReminderTime = Calendar.getInstance();
            mReminderTime.setTimeInMillis(System.currentTimeMillis());
        }

        if (repeatEndDate == null){
            repeatEndDate = Calendar.getInstance();
            repeatEndDate.setTimeInMillis(System.currentTimeMillis());
        }

        currentTast.setPriority(priority);
        currentTast.setTitle(taskNameEditText.getText().toString());
        currentTast.setDueDateAndTime(mReminderTime.getTimeInMillis());
        currentTast.setRepeatFrequency(repeatFrequency);
        currentTast.setRepeatEndDate(repeatEndDate.getTimeInMillis());
        currentTast.setFolder(selectedFolder);
        taskCloudReference.child(currentTast.getId()).setValue(currentTast);

        if (shouldAddSubTasks){
            Gson gson = new Gson();
            String serializedTask = gson.toJson(currentTast);
            Intent addSubTaskIntent = new Intent(getActivity(), AddSubTaskActivity.class);
            addSubTaskIntent.putExtra(Constants.SERIALIZED_TASK, serializedTask);
            addSubTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(addSubTaskIntent);
        }else {
            goBackToParent();
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewFolder(FolderAddedEvent event){
        addFolderDialogFragment.dismiss();
        if (event.getFolder() != null){
            mFolder.setText(event.getFolder().getFolderName());
            selectedFolder = event.getFolder();
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
        showChooseFolderDialog(folderList);
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
        repeatFrequency = Reminder.NO;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oneTimeEventButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            oneTimeEventButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_reminder_hourly)
    public void onClickHourlyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Reminder.HOURLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hourlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            hourlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }


    @OnClick(R.id.button_reminder_daily)
    public void onClickDailyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Reminder.DAILY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dailyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            dailyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.button_reminder_weekly)
    public void onClickWeeklyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Reminder.WEEKLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weeklyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            weeklyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.button_reminder_week_days)
    public void onClickWeekDaysReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Reminder.WEEKDAYS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weekDaysReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            weekDaysReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_reminder_monthly)
    public void onClickMonthlyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Reminder.MONTHLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            monthlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            monthlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        reminderEndDateLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.button_reminder_yearly)
    public void onClickYearlyReminderButton(View view){
        resetReminderButtons();
        repeatFrequency = Reminder.YEARLY;
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

    private void showChooseFolderDialog(List<Folder> folders) {
        selectFolderDialogFragment = selectFolderDialogFragment.newInstance();


        selectFolderDialogFragment.setCategorySelectedListener(new OnFolderSelectedListener() {
            @Override
            public void onCategorySelected(Folder selectedFolder) {
                selectFolderDialogFragment.dismiss();
                mFolder.setText(selectedFolder.getFolderName());
                selectedFolder = selectedFolder;
            }

            @Override
            public void onEditCategoryButtonClicked(Folder selectedCategory) {

            }

            @Override
            public void onDeleteCategoryButtonClicked(Folder selectedCategory) {

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
    public void showTaskDetail(Task task) {
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

        Reminder frequency = null;
        try {
            frequency = task.getRepeatFrequency();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (frequency != null){

            switch (frequency){
                case HOURLY:
                    hourlyReminderButton.performClick();
                    break;
                case DAILY:
                    dailyReminderButton.performClick();
                    break;
                case WEEKLY:
                    weeklyReminderButton.performClick();
                    break;
                case MINUTE:
                    weekDaysReminderButton.performClick();
                    break;
                case YEARLY:
                    yearlyReminderButton.performClick();
                    break;
                case MONTHLY:
                    monthlyReminderButton.performClick();
                    break;


            }
        }

        try {
            mFolder.setText(task.getFolder().getFolderName());
            getFolderById(task.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Show the repeat end date of the Frequency is not a single event
        if (task.getRepeatFrequency() != Reminder.NO){
            reminderEndDateLayout.setVisibility(View.VISIBLE);
            repeatEndDate = Calendar.getInstance();
            if (task.getRepeatEndDate() > 0){
                repeatEndDate.setTimeInMillis(task.getRepeatEndDate());
            }
            onRepeatEndDateSelected(repeatEndDate);
        }


    }

    private void getFolderById(String id) {
        if (folderList != null && folderList.size() > 0){
            //Get Folder from the List of retreived folders
            for (Folder folder: folderList){
                if (folder.getId().equals(id)){
                    selectedFolder = folder;
                    break;
                }
            }
        }else {
            //Get Folder from cloud
            folderCloudReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        selectedFolder = dataSnapshot.getValue(Folder.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
        parentIntent.putExtra(Constants.FRAGMENT_TAG, Constants.TODO_LIST_FRAGMENT_TAG);
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

    private Folder getDefaultFolder(String defaultCategory) {
        Folder folder = new Folder();
        folder.setId(getFolderId(defaultCategory));
        folder.setFolderName(defaultCategory);
        return folder;
    }

    private String getFolderId(String folderName) {
        for (Folder folder: folderList){
            if (!TextUtils.isEmpty(folder.getId()) && folder.getFolderName().equals(folderName)){
                return folder.getId();
            }
        }
        return addFolderToFirebase(folderName);
    }


    private String addFolderToFirebase(String folderName) {
        Folder folder = new Folder();
        folder.setFolderName(folderName);
        String key = folderCloudReference.push().getKey();
        folder.setId(key);
        folderCloudReference.child(key).setValue(folder);
        return key;
    }

}

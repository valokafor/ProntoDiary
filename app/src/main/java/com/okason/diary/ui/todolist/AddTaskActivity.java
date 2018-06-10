package com.okason.diary.ui.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.data.FolderDao;
import com.okason.diary.data.ReminderDao;
import com.okason.diary.data.TagDao;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.Folder;
import com.okason.diary.models.ProntoTag;
import com.okason.diary.models.ProntoTask;
import com.okason.diary.models.Reminder;
import com.okason.diary.reminder.AdvancedRepeatSelector;
import com.okason.diary.reminder.AlarmReceiver;
import com.okason.diary.reminder.AlarmUtil;
import com.okason.diary.reminder.DateAndTimeUtil;
import com.okason.diary.reminder.DaysOfWeekSelector;
import com.okason.diary.reminder.TextFormatUtil;
import com.okason.diary.ui.folder.AddFolderDialogFragment;
import com.okason.diary.ui.folder.SelectFolderDialogFragment;
import com.okason.diary.ui.tag.AddTagDialogFragment;
import com.okason.diary.ui.tag.SelectTagDialogFragment;
import com.okason.diary.utils.Constants;
import com.okason.diary.utils.SettingsHelper;
import com.okason.diary.utils.date.DateHelper;
import com.okason.diary.utils.date.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class AddTaskActivity extends AppCompatActivity implements
        AdvancedRepeatSelector.AdvancedRepeatSelectionListener,
        DaysOfWeekSelector.DaysOfWeekSelectionListener{

    @BindView(R.id.create_coordinator) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.radio_group_priority) RadioGroup priorityRadioGroup;
    @BindView(R.id.button_low_priority) RadioButton lowPriorityButton;
    @BindView(R.id.button_medium_priority) RadioButton mediumPriorityButton;
    @BindView(R.id.button_high_priority) RadioButton highPriorityButton;
    @BindView(R.id.edit_text_task_name) EditText taskNameEditText;
    @BindView(R.id.edit_text_task_description) EditText taskDescriptionEditText;
    @BindView(R.id.text_view_due_date) TextView dateTextView;
    @BindView(R.id.text_view_due_time) TextView timeTextView;
    @BindView(R.id.text_view_selected_folder) TextView folderTextView;
    @BindView(R.id.text_view_selected_tags) TextView tagsTextView;
    @BindView(R.id.text_view_repeat) TextView repeatTextView;
    @BindView(R.id.forever_row) LinearLayout foreverRow;
    @BindView(R.id.bottom_row) LinearLayout bottomRow;
    @BindView(R.id.bottom_view) View bottomView;
    @BindView(R.id.switch_toggle) SwitchCompat foreverSwitch;
    @BindView(R.id.error_time) ImageView imageWarningTime;
    @BindView(R.id.error_date) ImageView imageWarningDate;
    @BindView(R.id.error_show) ImageView imageWarningShow;
    @BindView(R.id.show_times_number) EditText timesEditText;
    @BindView(R.id.show) TextView showText;
    @BindView(R.id.times) TextView timesText;
    @BindView(R.id.adView) AdView mAdView;



    // private String repeatFrequency = "";
    private int priority = Constants.PRIORITY_LOW;

    //Flag that indicates if the Add Sub ProntoTask screen should be opened
    private boolean shouldAddSubTasks = false;

    //Flag to indicate if a Reminder should be added
    private boolean shouldSetReminder = false;


    private Realm realm;
    private TaskDao taskDao;
    private FolderDao folderDao;
    private TagDao tagDao;
    private ReminderDao reminderDao;
    private Activity activity;

    private ProntoTask currentProntoTask;
    private Reminder currentReminder;
    private Folder selectedFolder;
    private List<ProntoTag> selectedProntoTags;

    private Calendar calendar;
    private List<Boolean> daysOfWeek = new ArrayList<>();
    private int timesShown = 0;
    private int timesToShow = 1;
    private int repeatType;
    private int id;
    private int interval = 1;

    private SelectFolderDialogFragment selectFolderDialogFragment;
    private SelectTagDialogFragment selectTagDialogFragment;
    private AddFolderDialogFragment addFolderDialogFragment;
    private AddTagDialogFragment addTagDialogFragment;
    private  String[] repeatArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ButterKnife.bind(this);
        activity = this;

        realm = Realm.getDefaultInstance();
        taskDao = new TaskDao(realm);
        folderDao = new FolderDao(realm);
        tagDao = new TagDao(realm);
        reminderDao = new ReminderDao(realm);
        selectedProntoTags = new ArrayList<>();
        repeatArray = getResources().getStringArray(R.array.repeat_array);

        calendar = Calendar.getInstance();
        repeatType = Constants.DOES_NOT_REPEAT;

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_cancel_white);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().hasExtra(Constants.TASK_ID)) {
            String taskId = getIntent().getStringExtra(Constants.TASK_ID);
            currentProntoTask = taskDao.getTaskById(taskId);
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

        if (!SettingsHelper.getHelper(this).isPremiumUser()){
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentProntoTask != null){
            currentReminder = currentProntoTask.getReminder();
            showTaskDetail(currentProntoTask);
            showReminderDetail(currentReminder);
            getSupportActionBar().setTitle(getString(R.string.edit_task));
        }

        if (!SettingsHelper.getHelper(this).isPremiumUser()) {
            if (mAdView != null){
                mAdView.resume();
            }
        }
    }

    /**
     * This method populates the AddTask screen
     * When it is in Edit mode
     * @param prontoTask - the passed in ProntoTask
     */
    public void showTaskDetail(ProntoTask prontoTask) {
        try {
            taskNameEditText.setText(prontoTask.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            taskDescriptionEditText.setText(prontoTask.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int priority = prontoTask.getPriority();
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

        try {
            String formattedDueDate = TimeUtils.getReadableDateWithoutTime(prontoTask.getReminder().getDateAndTime());
            dateTextView.setText(formattedDueDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String formattedDueTime = DateHelper.getTimeShort(activity, prontoTask.getReminder().getDateAndTime());
            timeTextView.setText(formattedDueTime);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            onFolderSelected(currentProntoTask.getFolder());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Show Tags assigned to this prontoTask
        if (currentProntoTask.getTags() != null && currentProntoTask.getTags().size() > 0){
            String tagText = "";
            for (ProntoTag prontoTag : currentProntoTask.getTags()){
                tagText = tagText + "#" + prontoTag.getTagName() + ", ";
            }
            tagsTextView.setText(tagText);
            tagsTextView.setTextColor(ContextCompat.getColor(activity, R.color.primary_dark));
            tagsTextView.setTypeface(tagsTextView.getTypeface(), Typeface.BOLD);
        }
    }

    public void showReminderDetail(Reminder reminder) {
        // Prevent keyboard from opening automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        timesShown = reminder.getNumberShown();
        repeatType = reminder.getRepeatType();
        interval = reminder.getInterval();
        calendar = DateAndTimeUtil.parseDateAndTime(reminder.getDateAndTime());
        showText.setText(getString(R.string.times_shown_edit, reminder.getNumberShown()));

        dateTextView.setText(DateAndTimeUtil.toStringReadableDate(calendar));
        timeTextView.setText(DateAndTimeUtil.toStringReadableTime(calendar, this));
        timesEditText.setText(String.valueOf(reminder.getNumberToShow()));

        timesText.setVisibility(View.VISIBLE);

        if (reminder.getRepeatType() != Constants.DOES_NOT_REPEAT) {
            if (reminder.getInterval() > 1) {
                repeatTextView.setText(TextFormatUtil.formatAdvancedRepeatText(this, repeatType, interval));
            } else {
                repeatTextView.setText(getResources().getStringArray(R.array.repeat_array)[reminder.getRepeatType()]);
            }
            showFrequency(true);
        }

        if (reminder.getRepeatType() == Constants.SPECIFIC_DAYS) {
            daysOfWeek = reminder.getDaysOfWeekList();
            repeatTextView.setText(TextFormatUtil.formatDaysOfWeekText(this, daysOfWeek));
        }

        if (reminder.isIndefinite()) {
            foreverSwitch.setChecked(true);
            bottomRow.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                validateInput();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null){
            mAdView.pause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewFolder(FolderAddedEvent event){
//        addFolderDialogFragment.dismiss();
        if (!TextUtils.isEmpty(event.getAddedFolderId())){
            String folderId = event.getAddedFolderId();
            selectedFolder = folderDao.getFolderById(folderId);
            if (selectedFolder != null){
                String folderName = selectedFolder.getFolderName();
                folderTextView.setText(folderName);
            }
        }

    }


    @OnClick(R.id.time_row)
    public void timePicker() {
        TimePickerDialog TimePicker = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                shouldSetReminder = true;
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                timeTextView.setText(DateAndTimeUtil.toStringReadableTime(calendar, getApplicationContext()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        TimePicker.show();
    }

    @OnClick(R.id.date_row)
    public void datePicker(View view) {
        DatePickerDialog DatePicker = new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker DatePicker, int year, int month, int dayOfMonth) {
                shouldSetReminder = true;
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateTextView.setText(DateAndTimeUtil.toStringReadableDate(calendar));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker.show();
    }

    @OnClick(R.id.repeat_row)
    public void repeatSelector() {
        new MaterialDialog.Builder(this)
                .title("Select Repeat Cycle")
                .items(R.array.repeat_array)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        shouldSetReminder = true;
                        if (which == Constants.SPECIFIC_DAYS) {
                            DialogFragment daysOfWeekDialog = new DaysOfWeekSelector();
                            daysOfWeekDialog.show(getSupportFragmentManager(), "DaysOfWeekSelector");
                        }  else if (which == Constants.ADVANCED) {
                            DialogFragment advancedDialog = new AdvancedRepeatSelector();
                            advancedDialog.show(getSupportFragmentManager(), "AdvancedSelector");
                        } else {
                            onRepeatSelection(which, repeatArray[which]);
                        }
                    }
                })
                .show();
    }




    public void showFrequency(boolean show) {
        if (show) {
            foreverRow.setVisibility(View.VISIBLE);
            bottomRow.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.VISIBLE);
        } else {
            foreverSwitch.setChecked(false);
            foreverRow.setVisibility(View.GONE);
            bottomRow.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.folder_select)
    public void folderSelector() {
        showChooseFolderDialog(folderDao.getAllFolders());
    }


    //Handles when a select Folder button is clicked
    private void showChooseFolderDialog(List<Folder> folders) {
        selectFolderDialogFragment = selectFolderDialogFragment.newInstance();
        selectFolderDialogFragment.setCategories(folders);

        selectFolderDialogFragment.setCategorySelectedListener(new OnFolderSelectedListener() {
            @Override
            public void onCategorySelected(Folder folder) {
                onFolderSelected(folder);
                selectFolderDialogFragment.dismiss();
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
        selectFolderDialogFragment.show(getFragmentManager(), "Dialog");
    }

    private void onFolderSelected(Folder folder) {
        selectedFolder = folder;
        folderTextView.setText(selectedFolder.getFolderName());

    }

    public void showAddNewFolderDialog() {
        if (selectFolderDialogFragment != null) {
            selectFolderDialogFragment.dismiss();
        }
        addFolderDialogFragment = AddFolderDialogFragment.newInstance("");
        addFolderDialogFragment.show(getFragmentManager(), "Dialog");

    }


    @Override
    public void onDaysOfWeekSelected(boolean[] days) {
        shouldSetReminder = true;
        List<Boolean> selectedDays = new ArrayList<>();
        for (int i = 0; i < days.length; i++) {
            selectedDays.add(days[i]);
        }
        repeatTextView.setText(TextFormatUtil.formatDaysOfWeekText(this, selectedDays));
        daysOfWeek = selectedDays;
        repeatType = Constants.SPECIFIC_DAYS;
        showFrequency(true);
    }


    public void onRepeatSelection(int which, String repeatText) {
        shouldSetReminder = true;
        interval = 1;
        repeatType = which;
        this.repeatTextView.setText(repeatText);
        if (which == Constants.DOES_NOT_REPEAT) {
            showFrequency(false);
        } else {
            showFrequency(true);
        }

    }

    @OnClick(R.id.forever_row)
    public void toggleSwitch() {
        foreverSwitch.toggle();
        if (foreverSwitch.isChecked()) {
            shouldSetReminder = true;
            bottomRow.setVisibility(View.GONE);
        } else {
            bottomRow.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.switch_toggle)
    public void switchClicked() {
        if (foreverSwitch.isChecked()) {
            shouldSetReminder = true;
            bottomRow.setVisibility(View.GONE);
        } else {
            bottomRow.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.subtask_layout)
    public void addSubTaskClicked() {
        shouldAddSubTasks = true;
        validateInput();
    }

    @Override
    public void onAdvancedRepeatSelection(int type, int interval, String repeatText) {

    }

    private void makeToast(String message){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary_dark));
        TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public void validateInput() {
        imageWarningShow.setVisibility(View.GONE);
        imageWarningTime.setVisibility(View.GONE);
        imageWarningDate.setVisibility(View.GONE);
        Calendar nowCalendar = Calendar.getInstance();

        if (timeTextView.getText().equals(getString(R.string.time_now))) {
            calendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));
        }

        if (dateTextView.getText().equals(getString(R.string.date_today))) {
            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));
        }

        // Check if the number of times to show notification is empty
        if (timesEditText.getText().toString().isEmpty()) {
            timesEditText.setText("1");
        }

        timesToShow = Integer.parseInt(timesEditText.getText().toString());
        if (repeatType == Constants.DOES_NOT_REPEAT) {
            timesToShow = timesShown + 1;
        }

        // Check if selected date is before today's date
        if (DateAndTimeUtil.toLongDateAndTime(calendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            makeToast(getString(R.string.toast_past_date));
            imageWarningTime.setVisibility(View.VISIBLE);
            imageWarningDate.setVisibility(View.VISIBLE);

            // Check if title is empty
        } else if (taskNameEditText.getText().toString().trim().isEmpty()) {
            taskNameEditText.setError(getString(R.string.required));


            // Check if times to show notification is too low
        } else if (timesToShow <= timesShown && !foreverSwitch.isChecked()) {
            makeToast(getString(R.string.toast_higher_number));
            imageWarningShow.setVisibility(View.VISIBLE);
        } else {
            saveNotification();
        }
    }

    private void saveNotification() {

        if (currentProntoTask == null){
            currentProntoTask = taskDao.createNewTask();
        }

        String taskName = taskNameEditText.getText().toString();
        String description = taskDescriptionEditText.getText().toString();

        if (selectedFolder == null){
            selectedFolder = folderDao.getOrCreateFolder(getString(R.string.general));
        }

        taskDao.updateTask(currentProntoTask.getId(), taskName, description, priority, selectedFolder.getId());

        if (shouldSetReminder) {
            if (currentReminder == null){
                currentReminder = reminderDao.createNewReminder();
            }

            String daysOfWeekString = new Gson().toJson(daysOfWeek);

            reminderDao.updateReminder(calendar, repeatType, foreverSwitch.isChecked(), timesToShow, interval, daysOfWeekString, currentReminder.getId());

            if (currentReminder != null){
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                calendar.set(Calendar.SECOND, 0);
                AlarmUtil.setAlarm(this, alarmIntent, currentReminder.getId(), calendar);
            }

            taskDao.addReminder(currentProntoTask.getId(), currentReminder.getId());
        }

        if (shouldAddSubTasks) {
            Intent addSubTaskIntent = new Intent(activity, AddSubTaskActivity.class);
            addSubTaskIntent.putExtra(Constants.TASK_ID, currentProntoTask.getId());
            addSubTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(addSubTaskIntent);
        } else {
            onBackPressed();
        }
    }
}

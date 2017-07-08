package com.okason.diary.ui.todolist;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.data.FolderRealmRepository;
import com.okason.diary.models.Folder;
import com.okason.diary.models.Task;
import com.okason.diary.ui.folder.AddFolderDialogFragment;
import com.okason.diary.ui.folder.FolderListAdapter;
import com.okason.diary.ui.folder.SelectFolderDialogFragment;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends Fragment implements TaskContract.View{
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

    @BindView(R.id.button_reminder_monthly)
    Button monthlyReminderButton;

    @BindView(R.id.button_reminder_yearly)
    Button yearlyReminderButton;


    @BindView(R.id.image_button_add_reminder_options)
    ImageButton addOtherReminderButton;
    private SelectFolderDialogFragment selectFolderDialogFragment;
    private AddFolderDialogFragment addFolderDialogFragment;

    private RealmResults<Folder> mFolders;
    private Realm mRealm;

    private String selectedReminder;
    private int priority = Constants.PRIORITY_LOW;
    private Folder selectedFolder;
    private Calendar calender;

    private TaskContract.Actions presenter;


    @BindView(R.id.edit_text_category)
    EditText mCategory;

    @BindView(R.id.edit_text_task_name) EditText taskNameEditText;



    public AddTaskFragment() {
        // Required empty public constructor
    }


    private String getPassedInTaskId() {
        if (getArguments() != null && getArguments().containsKey(Constants.TASK_ID)){
            String taskId = getArguments().getString(Constants.TASK_ID);
            return taskId;
        }
        return "";

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, mRootView);
        oneTimeEventButton.performClick();
        presenter = new TaskPresenter(this);



        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            mRealm = Realm.getDefaultInstance();
            mFolders = mRealm.where(Folder.class).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mRealm.isClosed()) {
            mRealm.close();
        }

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
        if (TextUtils.isEmpty(taskNameEditText.getText())){
            taskNameEditText.setError(getString(R.string.required));
            return;
        }

        if (TextUtils.isEmpty(selectedReminder)){
            selectedReminder = Constants.REMINDER_NO_REMINDER;
        }

        if (selectedFolder == null){
            selectedFolder = new FolderRealmRepository().getOrCreateFolder(getString(R.string.general));
        }

        if (calender == null){
            calender = Calendar.getInstance();
            calender.setTimeInMillis(System.currentTimeMillis());
        }
        presenter.onSaveAndExit(priority, taskNameEditText.getText().toString(), calender.getTimeInMillis(), "", selectedFolder.getId(), false);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewFolder(FolderAddedEvent event){
        addFolderDialogFragment.dismiss();
        String folderId = event.getAddedFolderId();
        Folder selectedFolder = new FolderRealmRepository().getFolderById(folderId);

        if (selectedFolder != null){
            String folderName = selectedFolder.getFolderName();
            mCategory.setText(folderName);
        }

    }

    @OnClick(R.id.edit_text_category)
    public void showSelectFolder(){
        showChooseFolderDialog(mFolders);
    }




    @OnClick(R.id.button_one_time_event)
    public void onClickOneTimeEventButton(View view){
        resetReminderButtons();
        selectedReminder = Constants.REMINDER_NO_REMINDER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oneTimeEventButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            oneTimeEventButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    @OnClick(R.id.button_reminder_hourly)
    public void onClickHourlyReminderButton(View view){
        resetReminderButtons();
        selectedReminder = Constants.REMINDER_HOURLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hourlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            hourlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }


    @OnClick(R.id.button_reminder_daily)
    public void onClickDailyReminderButton(View view){
        resetReminderButtons();
        selectedReminder = Constants.REMINDER_DAILY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dailyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            dailyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    @OnClick(R.id.button_reminder_weekly)
    public void onClickWeeklyReminderButton(View view){
        resetReminderButtons();
        selectedReminder = Constants.REMINDER_WEEKLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            weeklyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            weeklyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    @OnClick(R.id.button_reminder_monthly)
    public void onClickMonthlyReminderButton(View view){
        resetReminderButtons();
        selectedReminder = Constants.REMINDER_MONTHLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            monthlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            monthlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    @OnClick(R.id.button_reminder_yearly)
    public void onClickYearlyReminderButton(View view){
        resetReminderButtons();
        selectedReminder = Constants.REMINDER_YEARLY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            yearlyReminderButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.primary));
            yearlyReminderButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
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
        selectFolderDialogFragment.setCategories(folders);

        selectFolderDialogFragment.setCategorySelectedListener(new OnFolderSelectedListener() {
            @Override
            public void onCategorySelected(Folder selectedCategory) {
                selectFolderDialogFragment.dismiss();
                mCategory.setText(selectedCategory.getFolderName());
                mCategory.setText(selectedCategory.getFolderName());
                selectedFolder = new FolderRealmRepository().getFolderById(selectedCategory.getId());
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


    @Override
    public void showTaskDetail(Task task) {

    }

    @Override
    public void showEditTaskItem(Task todoList) {

    }

    @Override
    public void showMessage(boolean message) {

    }
}

package com.okason.diary.ui.todolist;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.TaskListChangeEvent;
import com.okason.diary.core.listeners.TaskItemListener;
import com.okason.diary.models.SubTask;
import com.okason.diary.models.Task;
import com.okason.diary.ui.addnote.DataAccessManager;
import com.okason.diary.ui.auth.AuthUiActivity;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment implements TaskItemListener,
         SearchView.OnCloseListener, SearchView.OnQueryTextListener{


    private final static String TAG = "TaskListFragment";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DataAccessManager dataAccessManager;


    private List<Task> allTasks;
    private List<Task> filteredTasks;
    private TaskListAdapter mListAdapter;
    private View mRootView;
    private boolean shouldUpdateAdapter = true;
    private String sortColumn = "title";





    @BindView(R.id.todo_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;

    private FloatingActionButton floatingActionButton;
    private int priority = Constants.PRIORITY_ALL;



    public TaskListFragment() {
        // Required empty public constructor
    }

    public static TaskListFragment newInstance(int priority){
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.TASK_PRIORITY, priority);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null && getArguments().containsKey(Constants.TASK_PRIORITY)){
            priority = getArguments().getInt(Constants.TASK_PRIORITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_todo_list, container, false);
        ButterKnife.bind(this, mRootView);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allTasks = new ArrayList<>();
        filteredTasks = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
                    startActivity(new Intent(getActivity(), AddTaskActivity.class));
                } else {
                    startActivity(AuthUiActivity.createIntent(getActivity()));
                }

            }
        });
        sortColumn = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("sort_options","title");
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            dataAccessManager = new DataAccessManager(firebaseUser.getUid());


            if (priority == Constants.PRIORITY_ALL){
               dataAccessManager.getTaskPath()
                       .orderBy(sortColumn)
                       .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                       if (task.isSuccessful()){
                           for (DocumentSnapshot snapshot : task.getResult()) {
                               com.okason.diary.models.Task item = snapshot.toObject(com.okason.diary.models.Task.class);
                               if (item != null) {
                                   allTasks.add(item);
                               }
                           }
                           showTodoLists(allTasks);
                       }
                   }
               });

            } else {
               dataAccessManager
                       .getTaskPath()
                       .orderBy(sortColumn)
                       .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                       if (task.isSuccessful()){
                           for (DocumentSnapshot snapshot : task.getResult()) {
                               com.okason.diary.models.Task item = snapshot.toObject(com.okason.diary.models.Task.class);
                               if (item != null && item.getPriority() == priority) {
                                   allTasks.add(item);
                               }
                           }
                           showTodoLists(allTasks);
                       }
                   }
               });
            }
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
    public void onTaskListChange(TaskListChangeEvent event){
        allTasks = event.getTasklList();
        showTodoLists(allTasks);
    }




    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_todo_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id){
            case R.id.action_add:

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {

        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            showTodoLists(allTasks);
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            if (query.length() > 0) {
                filteredTasks = filterTasks(query);
                showTodoLists(filteredTasks);
                return true;
            }
        }  else {
            makeToast(getString(R.string.login_required));
        }

        return true;
    }

    private List<Task> filterTasks(String query) {
        List<Task> taskList = new ArrayList<>();
        for (Task task: allTasks){
            String title = task.getTitle().toLowerCase();
            query = query.toLowerCase();
            if (title.contains(query)){
                taskList.add(task);
            }else {
                for (SubTask subTask: task.getSubTask()){
                    String subTasktitle = subTask.getTitle().toLowerCase();
                    if (subTasktitle.contains(query)){
                        taskList.add(task);
                        break;
                    }
                }
            }
        }
        return taskList;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            if (newText.length() > 0) {
                filteredTasks = filterTasks(newText);
                showTodoLists(filteredTasks);
                return true;
            }
        }  else {
            makeToast(getString(R.string.login_required));
        }

        return true;
    }


    private void showTodoLists(List<Task> tasks) {

        if (tasks != null && tasks.size() > 0){
            showEmptyText(false);
            mListAdapter = new TaskListAdapter(tasks, getActivity(), this);
            mRecyclerView.setAdapter(mListAdapter);
        }else {
            showEmptyText(true);
        }
    }

    public void showEmptyText(boolean showText) {
        if (showText){
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
            //  mAdView.setVisibility(View.GONE);
            switch (priority){
                case 0:
                    mEmptyText.setText(getString(R.string.no_todo_list_found_high_priority));
                    break;
                case 1:
                    mEmptyText.setText(getString(R.string.no_todo_list_found_medium_priority));
                    break;
                case 2:
                    mEmptyText.setText(getString(R.string.no_todo_list_found_low_priority));
                    break;
                default:
                    mEmptyText.setText(getString(R.string.no_todo_list_found_default));
                    break;
            }

        }else {
            //  mAdView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);

        }

    }


    @Override
    public void onEditTaskButtonClicked(Task clickedTask) {
        Intent editTaskIntent = new Intent(getActivity(), AddTaskActivity.class);
        String serializedTask = new Gson().toJson(clickedTask);
        editTaskIntent.putExtra(Constants.SERIALIZED_TASK, serializedTask);
        startActivity(editTaskIntent);
    }

    @Override
    public void onDeleteTaskButtonClicked(Task clickedTask) {
        boolean shouldPromptForDelete = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("prompt_for_delete", true);
        if (shouldPromptForDelete) {
            promptForDelete(clickedTask);
        } else {
            deleteTask(clickedTask);
        }

    }

    private void deleteTask(Task clickedTask) {
        dataAccessManager.deleteTask(clickedTask);

    }

    @Override
    public void onAddSubTasksButtonClicked(Task clickedTask) {
        Intent addSubTaskIntent = new Intent(getActivity(), AddSubTaskActivity.class);
        Gson gson = new Gson();
        String serializedTask = gson.toJson(clickedTask);
        addSubTaskIntent.putExtra(Constants.SERIALIZED_TASK, serializedTask);
        startActivity(addSubTaskIntent);

    }

    @Override
    public void onTaskChecked(Task task) {
//        shouldUpdateAdapter = false;
//        for (SubTask subTask: task.getSubTask()){
//            subTask.setChecked(true);
//        }
        dataAccessManager.getTaskPath().document(task.getId()).update("checked", true);

    }



    @Override
    public void onTaskUnChecked(Task task) {
//        shouldUpdateAdapter = false;
//        for (SubTask subTask: task.getSubTask()){
//            subTask.setChecked(false);
//        }
        dataAccessManager.getTaskPath().document(task.getId()).update("checked", false);

    }


    private void promptForDelete(final Task clickedTask) {
        String title = getString(R.string.are_you_sure);
        String message =  getString(R.string.action_delete) + " " + clickedTask.getTitle();


        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataAccessManager.deleteTask(clickedTask);
            }
        });
        alertDialog.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }


    private void makeToast(String message) {
        try {
            Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
            TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

package com.okason.diary.ui.todolist;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.listeners.TaskItemListener;
import com.okason.diary.data.TaskDao;
import com.okason.diary.models.Task;
import com.okason.diary.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment implements TaskItemListener,
         SearchView.OnCloseListener, SearchView.OnQueryTextListener{


    private final static String TAG = "TaskListFragment";




    private RealmResults<Task> allTasks;
    private List<Task> filteredTasks;
    private TaskListAdapter mListAdapter;
    private View mRootView;
    private String sortColumn = "title";
    private Realm realm;
    private TaskDao taskDao;





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



        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTaskActivity.class));
            }
        });
        sortColumn = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("sort_options","title").toLowerCase();
        realm = Realm.getDefaultInstance();
        taskDao = new TaskDao(realm);
        return mRootView;
    }

    private void getTasks() {
        if (priority == Constants.PRIORITY_ALL){
            allTasks = taskDao.getAllTask().sort(sortColumn);
        } else {
            allTasks = taskDao.getAllTasksForPriority(priority).sort(sortColumn);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getTasks();
        showTodoLists(allTasks);
        allTasks.addChangeListener(changeListener);
    }




    @Override
    public void onPause() {
        allTasks.removeAllChangeListeners();
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
        getTasks();
        showTodoLists(allTasks);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() > 0) {
            filteredTasks = taskDao.filterTasks(query);
            showTodoLists(filteredTasks);
        }
        return true;
    }



    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            filteredTasks = taskDao.filterTasks(newText);
            showTodoLists(filteredTasks);
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
                case 1:
                    mEmptyText.setText(getString(R.string.no_todo_list_found_low_priority));
                    break;
                case 2:
                    mEmptyText.setText(getString(R.string.no_todo_list_found_medium_priority));
                    break;
                case 3:
                    mEmptyText.setText(getString(R.string.no_todo_list_found_high_priority));
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
        editTaskIntent.putExtra(Constants.TASK_ID, clickedTask.getId());
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
        taskDao.deleteTask(clickedTask.getId());

    }

    @Override
    public void onAddSubTasksButtonClicked(Task clickedTask) {
        Intent addSubTaskIntent = new Intent(getActivity(), AddSubTaskActivity.class);
        addSubTaskIntent.putExtra(Constants.TASK_ID, clickedTask.getId());
        startActivity(addSubTaskIntent);

    }

    @Override
    public void onTaskChecked(Task task) {
        taskDao.updateTaskStatus(task, true);

    }



    @Override
    public void onTaskUnChecked(Task task) {
        taskDao.updateTaskStatus(task, false);
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
                taskDao.deleteTask(clickedTask.getId());
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


    private final OrderedRealmCollectionChangeListener<RealmResults<Task>> changeListener =
            new OrderedRealmCollectionChangeListener<RealmResults<Task>>() {
                @Override
                public void onChange(RealmResults<Task> allTasks, OrderedCollectionChangeSet changeSet) {
                    // `null`  means the async query returns the first time.
                    if (changeSet == null) {
                        return;
                    }

                    // For deletions, the adapter has to be notified in reverse order.
                    OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                    for (int i = deletions.length - 1; i >= 0; i--) {
                        OrderedCollectionChangeSet.Range range = deletions[i];
                        mListAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
                        //Re-run the ViewPager setup so that the ViewPager title will be updated
                        //To reflect accurate count of the labels
                        try {
                            TodoListActivity parentActivity = (TodoListActivity) getActivity();
                            parentActivity.setupViewPager();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                    for (OrderedCollectionChangeSet.Range range : insertions) {
                        mListAdapter.notifyItemRangeInserted(range.startIndex, range.length);
                    }

                    OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                    for (OrderedCollectionChangeSet.Range range : modifications) {
                        mListAdapter.notifyItemRangeChanged(range.startIndex, range.length);
                    }
                }
            };



}

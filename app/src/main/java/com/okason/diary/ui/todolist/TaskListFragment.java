package com.okason.diary.ui.todolist;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.okason.diary.data.TaskRealmRepository;
import com.okason.diary.models.Task;
import com.okason.diary.ui.auth.RegisterActivity;
import com.okason.diary.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.SyncUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment implements TaskItemListener,
        TaskContract.View, SearchView.OnCloseListener, SearchView.OnQueryTextListener{

    private Realm mRealm;
    private RealmResults<Task> mTasks;
    private TaskListAdapter mListAdapter;
    private View mRootView;
    private boolean shouldUpdateAdapter = true;
    private TaskContract.Actions presenter;



    @BindView(R.id.todo_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;

    private FloatingActionButton floatingActionButton;


    public TaskListFragment() {
        // Required empty public constructor
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
        mRootView = inflater.inflate(R.layout.fragment_todo_list, container, false);
        ButterKnife.bind(this, mRootView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        presenter = new TaskPresenter(this);

        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SyncUser.currentUser() != null) {
                    startActivity(new Intent(getActivity(), AddTaskActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), RegisterActivity.class));
                }
            }
        });
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SyncUser.currentUser() != null) {
            mListAdapter = null;
            try {
                mRealm = Realm.getDefaultInstance();
                mTasks = mRealm.where(Task.class).findAll();
                mTasks.addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
                    @Override
                    public void onChange(RealmResults<Task> tasks) {
                        if (shouldUpdateAdapter) {
                            showTodoLists(tasks);
                        }else {
                            shouldUpdateAdapter = true;
                        }
                    }
                });
                showTodoLists(mTasks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showEmptyText(true);
        }

        // addSampleTodoList();

    }



    @Override
    public void onPause() {
        super.onPause();
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }

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
        mTasks = mRealm.where(Task.class).findAll();
        mTasks.addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> tasks) {
                if (shouldUpdateAdapter) {
                    showTodoLists(tasks);
                }else {
                    shouldUpdateAdapter = true;
                }
            }
        });
        showTodoLists(mTasks);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() > 0) {
            mTasks = mRealm.where(Task.class).contains("title", query, Case.INSENSITIVE).findAll();
            showTodoLists(mRealm.copyFromRealm(mTasks));
            return true;
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
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
            presenter.deleteTask(clickedTask);
        }

    }

    @Override
    public void onAddSubTasksButtonClicked(Task clickedTask) {
        Intent addSubTaskIntent = new Intent(getActivity(), AddSubTaskActivity.class);
        addSubTaskIntent.putExtra(Constants.TASK_ID, clickedTask.getId());
        startActivity(addSubTaskIntent);

    }

    @Override
    public void onTaskChecked(Task task) {
        shouldUpdateAdapter = false;
        presenter.onMarkTaskAsComplete(task);

    }

    @Override
    public void onTaskUnChecked(Task task) {
        shouldUpdateAdapter = false;
        presenter.onMarkTaskAsInComplete(task);
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
                new TaskRealmRepository().deleteTask(clickedTask.getId());
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

    @Override
    public void showTaskDetail(Task task) {
    //NA
    }

    @Override
    public void showEditTaskItem(Task todoList) {
        //NA
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void goBackToParent() {
        getActivity().onBackPressed();
    }


}

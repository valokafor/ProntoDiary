package com.okason.diary.ui.todolist;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.core.ProntoDiaryApplication;
import com.okason.diary.core.events.DisplayFragmentEvent;
import com.okason.diary.models.Task;
import com.okason.diary.ui.auth.RegisterActivity;
import com.okason.diary.ui.auth.SignInActivity;
import com.okason.diary.utils.SettingsHelper;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment {

    private Realm mRealm;
    private RealmResults<Task> mTasks;
    private TaskListAdapter mListAdapter;
    private View mRootView;


    @BindView(R.id.todo_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;


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
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ProntoDiaryApplication.isCloudSyncEnabled()) {
            mListAdapter = null;
            try {
                mRealm = Realm.getDefaultInstance();
                mTasks = mRealm.where(Task.class).findAll();
                mTasks.addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
                    @Override
                    public void onChange(RealmResults<Task> tasks) {
                        showTodoLists(tasks);
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
        inflater.inflate(R.menu.menu_todo_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id){
            case R.id.action_add:
                if (getActivity() != null) {
                    if (ProntoDiaryApplication.isCloudSyncEnabled()) {
                        showAddNewTaskFragment("");
                    } else {
                        boolean registeredUser = SettingsHelper.getHelper(getActivity()).isRegisteredUser();
                        if (registeredUser){
                            startActivity(new Intent(getActivity(), SignInActivity.class));
                        }else {
                            startActivity(new Intent(getActivity(), RegisterActivity.class));
                        }

                    }
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddNewTaskFragment(String taskId) {
        AddTaskFragment fragment = AddTaskFragment.newInstance(taskId);
        EventBus.getDefault().post(new DisplayFragmentEvent(fragment, getString(R.string.title_add_new_task)));
    }


    private void showTodoLists(RealmResults<Task> tasks) {

        if (tasks != null && tasks.size() > 0){
            showEmptyText(false);
            mListAdapter = new TaskListAdapter(tasks, getActivity());
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


}

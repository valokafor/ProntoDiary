package com.okason.diary.ui.todolist;


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
import com.okason.diary.core.listeners.OnTodoListSelectedListener;
import com.okason.diary.data.SampleData;
import com.okason.diary.data.TodoListRealmRepository;
import com.okason.diary.models.TodoList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodoListFragment extends Fragment {

    private Realm mRealm;
    private RealmResults<TodoList> mTodoLists;
    private TodoListAdapter mListAdapter;
    private View mRootView;
    private AddTodoListDialogFragment addTodoListDialog;

    @BindView(R.id.todo_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;


    public TodoListFragment() {
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
        mListAdapter = null;
        try {
            mRealm = Realm.getDefaultInstance();
            mTodoLists = mRealm.where(TodoList.class).findAll();
            mTodoLists.addChangeListener(new RealmChangeListener<RealmResults<TodoList>>() {
                @Override
                public void onChange(RealmResults<TodoList> todoLists) {
                    showTodoLists(todoLists);
                }
            });
            showTodoLists(mTodoLists);
        } catch (Exception e) {
            e.printStackTrace();
        }

       // addSampleTodoList();

    }

    private void addSampleTodoList() {

        for (TodoList todoList : SampleData.getSampleTodoListItems()){
            new TodoListRealmRepository().createNewTodoListItem(todoList.getTitle());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRealm.close();

    }


    private final OrderedRealmCollectionChangeListener<RealmResults<TodoList>> todoListChangeListener
            = new OrderedRealmCollectionChangeListener<RealmResults<TodoList>>() {
        @Override
        public void onChange(RealmResults<TodoList> todoLists, OrderedCollectionChangeSet changeSet) {

            if (changeSet == null){
                showTodoLists(mTodoLists);
            }

            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                mListAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                try {
                    mListAdapter.notifyItemRangeInserted(range.startIndex, range.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                mListAdapter.notifyItemRangeChanged(range.startIndex, range.length);
            }

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_folder_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id){
            case R.id.action_add:
                showAddNewTodoListDialog("");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddNewTodoListDialog(String todolistId) {
        addTodoListDialog = AddTodoListDialogFragment.newInstance(todolistId);
        addTodoListDialog.show(getActivity().getFragmentManager(), "Dialog");
    }


    private void showTodoLists(RealmResults<TodoList> todoLists) {

        if (todoLists != null && todoLists.size() > 0){
            showEmptyText(false);
            mListAdapter = new TodoListAdapter(todoLists, new OnTodoListSelectedListener() {
                @Override
                public void onTodoListClick(TodoList clickedTodo) {
                    showAddNewTodoListDialog(clickedTodo.getId());
                }
            });
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

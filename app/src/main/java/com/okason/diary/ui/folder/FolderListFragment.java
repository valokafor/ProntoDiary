package com.okason.diary.ui.folder;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.data.FolderDao;
import com.okason.diary.models.Folder;
import com.okason.diary.models.ProntoTag;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
public class FolderListFragment extends Fragment implements OnFolderSelectedListener,
        SearchView.OnCloseListener, SearchView.OnQueryTextListener{


    // private List<Journal> mNotes;
    private FolderListAdapter mAdapter;
    private View mRootView;

    @BindView(R.id.category_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;

    private AddFolderDialogFragment addCategoryDialog;

    private Realm realm;
    private RealmResults<Folder> mFolders;
    private FolderDao folderDao;
    private ProntoTag prontoTag;
    private FloatingActionButton floatingActionButton;





    public FolderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        realm = Realm.getDefaultInstance();
        folderDao = new FolderDao(realm);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_folder_list, container, false);
        ButterKnife.bind(this, mRootView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));




        return  mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewFolderDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mFolders = folderDao.getAllFolders();
        showFolders(mFolders);
        mFolders.addChangeListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mFolders.removeAllChangeListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_folder_list, menu);
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
                showAddNewFolderDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        mFolders = folderDao.getAllFolders();
        showFolders(mFolders);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() > 0) {
            mFolders = folderDao.filterFolder(query);
            showFolders(mFolders);
            return true;
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewCategory(FolderAddedEvent event){
        addCategoryDialog.dismiss();
    }




    public void showAddNewFolderDialog() {
        addCategoryDialog = AddFolderDialogFragment.newInstance("");
        if (getActivity() != null) {
            addCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");
        }
    }




    public void showFolders(List<Folder> folders) {
        if (folders.size() > 0){
            hideEmptyText();
            mAdapter = new FolderListAdapter(getContext(),folders, this);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            showEmptyText();
        }

    }


    @Override
    public void onCategorySelected(Folder selectedCategory) {

    }

    @Override
    public void onEditCategoryButtonClicked(Folder selectedCategory) {
        showEditCategoryForm(selectedCategory);

    }

    @Override
    public void onDeleteCategoryButtonClicked(Folder selectedFolder) {
        showConfirmDeleteCategoryPrompt(selectedFolder);

    }

    @Override
    public void onAddCategoryButtonClicked() {

    }


    public void showEditCategoryForm(Folder folder) {
        addCategoryDialog = AddFolderDialogFragment.newInstance(folder.getId());
        addCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");
    }

    public void showEmptyText() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    public void hideEmptyText() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.GONE);
    }


    public void showConfirmDeleteCategoryPrompt(final Folder folder) {
        String title = getString(R.string.are_you_sure);
        String message =  getString(R.string.action_delete) + " " + folder.getFolderName();


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
                //Delete Category
                folderDao.deleteFolder(folder.getId());
//                int noteCount = folder.getJournals().size();
//                if (noteCount > 0){
//                    Intent intent = new Intent(getContext(), DeleteCategoryIntentService.class);
//                    intent.putExtra(Constants.SELECTED_FOLDER_ID, folder.getId());
//                    getActivity().startService(intent);
//                }else {
//                    new FolderRealmRepository().deleteFolder(folder.getId());
//                }

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





    private OrderedRealmCollectionChangeListener<RealmResults<Folder>>  listener
            = new OrderedRealmCollectionChangeListener<RealmResults<Folder>>() {
        @Override
        public void onChange(RealmResults<Folder> folderEntities, OrderedCollectionChangeSet changeSet) {

            if (changeSet == null) {
                mAdapter.notifyDataSetChanged();
                return;
            }
            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                if (mAdapter != null) {
                    mAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
                }
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                if (mAdapter != null) {
                    mAdapter.notifyItemRangeInserted(range.startIndex, range.length);
                }
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                if (mAdapter != null) {
                    mAdapter.notifyItemRangeChanged(range.startIndex, range.length);
                }
            }
        }
    };




}

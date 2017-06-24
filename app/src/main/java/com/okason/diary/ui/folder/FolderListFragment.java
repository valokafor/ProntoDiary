package com.okason.diary.ui.folder;


import android.content.DialogInterface;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.data.FolderRealmRepository;
import com.okason.diary.models.Folder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderListFragment extends Fragment implements OnFolderSelectedListener{

   // private List<Note> mNotes;
    private FolderRecyclerViewAdapter mAdapter;
    private View mRootView;

    @BindView(R.id.category_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;

    private AddFolderDialogFragment addCategoryDialog;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Realm mRealm;
    private RealmChangeListener<Folder> folderChangeListener;
    private RealmResults<Folder> mFolders;





    public FolderListFragment() {
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
        mRootView = inflater.inflate(R.layout.fragment_folder_list, container, false);
        ButterKnife.bind(this, mRootView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mRealm = Realm.getDefaultInstance();
        mFolders = mRealm.where(Folder.class).findAll();


        mFolders.addChangeListener(new RealmChangeListener<RealmResults<Folder>>() {
            @Override
            public void onChange(RealmResults<Folder> folders) {
                for (Folder folder: folders){
                    String name = folder.getFolderName();
                    String id = folder.getId();
                //    Log.d(NoteListActivity.TAG, name);
                }
                showFolders(mFolders);
            }
        });
        return  mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showFolders(mFolders);
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
        try {
            mRealm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                showAddNewFolderDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewCategory(FolderAddedEvent event){
        addCategoryDialog.dismiss();
    }




    public void showAddNewFolderDialog() {
        addCategoryDialog = AddFolderDialogFragment.newInstance("");
        addCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");
    }




    public void showFolders(List<Folder> folders) {
        if (folders.size() > 0){
            hideEmptyText();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter = new FolderRecyclerViewAdapter(getContext(),folders, this);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            showEmptyText();
        }

    }

    @Override
    public void onCategorySelected(Folder selectedCategory) {

    }

    @Override
    public void onEditCategoryButtonClicked(Folder selectedFolder) {
        showEditCategoryForm(selectedFolder);
    }

    @Override
    public void onDeleteCategoryButtonClicked(Folder selectedFolder) {
        showConfirmDeleteCategoryPrompt(selectedFolder);

    }

    @Override
    public void onAddCategoryButtonClicked() {

    }


    public void showEditCategoryForm(Folder folder) {
        Gson gson = new Gson();
        String serializedCategory = gson.toJson(folder);

        addCategoryDialog = AddFolderDialogFragment.newInstance(serializedCategory);
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
                new FolderRealmRepository().deleteFolder(folder.getId());
//                int noteCount = folder.getNotes().size();
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
}

package com.okason.diary.ui.folder;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.events.FolderListChangeEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.models.Folder;
import com.okason.diary.models.SampleData;
import com.okason.diary.ui.addnote.DataAccessManager;
import com.okason.diary.ui.auth.AuthUiActivity;

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
public class FolderListFragment extends Fragment implements OnFolderSelectedListener,
        SearchView.OnCloseListener, SearchView.OnQueryTextListener{

   // private List<Journal> mNotes;
    private FolderListAdapter mAdapter;
    private DataAccessManager dataAccessManager;
    private View mRootView;

    @BindView(R.id.category_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;

    private AddFolderDialogFragment addCategoryDialog;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private List<Folder> unFilteredFolders;
    private List<Folder> filteredFolders;

    private FloatingActionButton floatingActionButton;
    private String sortColumn = "title";







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

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        unFilteredFolders = new ArrayList<>();
        filteredFolders = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
                    showAddNewFolderDialog();
                } else {
                   startActivity(AuthUiActivity.createIntent(getActivity()));
                }
            }
        });


        return  mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            dataAccessManager = new DataAccessManager(firebaseUser.getUid());
            sortColumn = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("sort_options","title");
            dataAccessManager.getAllFolder();
        } else {
            showFolders(generateSampleFolders());
        }
    }

    private List<Folder> generateSampleFolders() {

        final List<Folder> folders = new ArrayList<>();

        List<String> sampleFolderNames = SampleData.getSampleCategories();
        for (String name : sampleFolderNames) {

            final Folder folder = new Folder();
            folder.setTitle(name);

            folders.add(folder);
        }
        return folders;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFolderListChange(FolderListChangeEvent event){
        showFolders(event.getFolderlList());
    }




    @Override
    public void onPause() {
        super.onPause();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.menu_folder_list, menu);
//        MenuItem search = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
//        searchView.setOnQueryTextListener(this);
//        searchView.setOnCloseListener(this);
//        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        showFolders(unFilteredFolders);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

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
        addCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");
    }




    public void showFolders(List<Folder> folders) {
        if (folders.size() > 0){
            hideEmptyText();
            mAdapter = null;
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
    public void onEditCategoryButtonClicked(Folder selectedFolder) {
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            showEditCategoryForm(selectedFolder);
        } else {
            makeToast(getString(R.string.login_required));
        }
    }

    @Override
    public void onDeleteCategoryButtonClicked(Folder selectedFolder) {
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            showConfirmDeleteCategoryPrompt(selectedFolder);
        } else {
            makeToast(getString(R.string.login_required));
        }


    }

    @Override
    public void onAddCategoryButtonClicked() {

    }


    public void showEditCategoryForm(Folder folder) {
        Gson gson = new Gson();
        String serializedFolder = gson.toJson(folder);
        addCategoryDialog = AddFolderDialogFragment.newInstance(serializedFolder);
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
        String message =  getString(R.string.action_delete) + " " + folder.getTitle() + "?";


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
                //Delete Folder
                dataAccessManager.deleteFolder(folder.getId());
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
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }



}

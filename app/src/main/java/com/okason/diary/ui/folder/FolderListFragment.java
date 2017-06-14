package com.okason.diary.ui.folder;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.AddFolderEvent;
import com.okason.diary.core.listeners.OnFolderSelectedListener;
import com.okason.diary.core.services.DeleteCategoryIntentService;
import com.okason.diary.models.Folder;
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
public class FolderListFragment extends Fragment implements OnFolderSelectedListener{

   // private List<Note> mNotes;
    private List<Folder> mFolders;
    private FolderRecyclerViewAdapter mAdapter;
    private View mRootView;

    @BindView(R.id.category_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;

    private AddFolderDialogFragment addCategoryDialog;

    private DatabaseReference mDatabase;
    private DatabaseReference folderCloudReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FloatingActionButton mFab;

    private ValueEventListener folderEventListener;



    public FolderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_folder_list, container, false);
        ButterKnife.bind(this, mRootView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        folderCloudReference =  mDatabase.child(Constants.USERS_CLOUD_END_POINT + mFirebaseUser.getUid() + Constants.CATEGORY_CLOUD_END_POINT);


        mFolders = new ArrayList<>();

        folderEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadFolders(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


//        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showAddNewFolderDialog();
//            }
//        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new FolderRecyclerViewAdapter(getContext(),mFolders, this);
        mRecyclerView.setAdapter(mAdapter);

        return  mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (folderCloudReference != null && folderEventListener != null){
            folderCloudReference.addValueEventListener(folderEventListener);
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
        if (folderCloudReference != null && folderEventListener != null){
            folderCloudReference.removeEventListener(folderEventListener);
            folderEventListener = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewCategory(AddFolderEvent event){
        addCategoryDialog.dismiss();
    }




    public void showAddNewFolderDialog() {
        addCategoryDialog = AddFolderDialogFragment.newInstance("");
        addCategoryDialog.show(getActivity().getFragmentManager(), "Dialog");
    }


    private void loadFolders(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null){
            mFolders.clear();
            for (DataSnapshot categorySnapshot: dataSnapshot.getChildren()){
                Folder folder = null;
                try {
                    folder = categorySnapshot.getValue(Folder.class);
                    mFolders.add(folder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (mFolders.size() > 0){
            hideEmptyText();
            showFolders(mFolders);
        }else {
            showEmptyText();
        }

    }

    public void showFolders(List<Folder> folders) {
        mAdapter.replaceData(folders);
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
                int noteCount = folder.getListOfNoteIds().size();
                if (noteCount > 0){
                    Intent intent = new Intent(getContext(), DeleteCategoryIntentService.class);
                    intent.putExtra(Constants.SELECTED_FOLDER_ID, folder.getId());
                    getActivity().startService(intent);
                }else {
                    folderCloudReference.child(folder.getId()).removeValue();
                }

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

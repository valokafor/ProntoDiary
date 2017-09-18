package com.okason.diary.ui.tag;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.okason.diary.R;
import com.okason.diary.core.events.DisplayFragmentEvent;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.models.Tag;
import com.okason.diary.ui.auth.AuthUiActivity;
import com.okason.diary.ui.notes.NoteListFragment;
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
public class TagListFragment extends Fragment implements OnTagSelectedListener{



    private TagListAdapter mAdapter;
    private View mRootView;

    @BindView(R.id.tag_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;
    @BindView(R.id.add_tag_fab)
    FloatingActionButton addTagbutton;

    private AddTagDialogFragment addTagDialog;
    private List<Tag> unFilteredTag;
    private List<Tag> filteredTag;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference database;
    private DatabaseReference tagCloudReference;


    public TagListFragment() {
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
        mRootView = inflater.inflate(R.layout.fragment_tag_list, container, false);
        ButterKnife.bind(this, mRootView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        unFilteredTag = new ArrayList<>();
        filteredTag = new ArrayList<>();

        if (firebaseUser != null) {
            database = FirebaseDatabase.getInstance().getReference();
            tagCloudReference = database.child(Constants.USERS_CLOUD_END_POINT + firebaseUser.getUid() + Constants.TAG_CLOUD_END_POINT);
        }

        addTagbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    showAddNewTagDialog();
                } else {
                    startActivity(new Intent(getActivity(), AuthUiActivity.class));
                }

            }
        });
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firebaseUser != null) {
            populateTagList();
        } else {
            showEmptyText();
        }

    }

    private void populateTagList() {
        tagCloudReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    unFilteredTag.clear();
                    for (DataSnapshot folderSnapshot: dataSnapshot.getChildren()){
                        Tag tag = folderSnapshot.getValue(Tag.class);
                        unFilteredTag.add(tag);
                    }
                    showTags(unFilteredTag);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast("Error fetching data " + databaseError.getMessage());
            }
        });

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_tag_list, menu);
//        MenuItem search = menu.findItem(R.id.action_search);
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewTag(FolderAddedEvent event){
        addTagDialog.dismiss();
    }




    public void showAddNewTagDialog() {
        addTagDialog = AddTagDialogFragment.newInstance("");
        addTagDialog.show(getActivity().getFragmentManager(), "Dialog");
    }




    public void showTags(List<Tag> tags) {
        if (tags.size() > 0){
            hideEmptyText();
            mAdapter = new TagListAdapter(getActivity(), tags, this);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            showEmptyText();
        }

    }

    public void showEmptyText() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    public void hideEmptyText() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.GONE);
    }

    @Override
    public void onTagChecked(Tag selectedTag) {

    }

    @Override
    public void onTagUnChecked(Tag unSelectedTag) {

    }

    @Override
    public void onAddTagButtonClicked() {

    }

    @Override
    public void onTagClicked(Tag clickedTag) {

        NoteListFragment fragment = NoteListFragment.newInstance(false, clickedTag.getTagName());
        String title = getString(R.string.action_tag) + ": " + clickedTag.getTagName();
        EventBus.getDefault().post(new DisplayFragmentEvent(fragment, title));

    }

    @Override
    public void onEditTagButtonClicked(Tag clickedTag) {
        showEditTagForm(clickedTag);

    }

    private void showEditTagForm(Tag clickedTag) {
        Gson gson = new Gson();
        String serializedTag = gson.toJson(clickedTag);
        addTagDialog = AddTagDialogFragment.newInstance(serializedTag);
        addTagDialog.show(getActivity().getFragmentManager(), "Dialog");
    }

    @Override
    public void onDeleteTagButtonClicked(Tag clickedTag) {
        showConfirmDeleteTagPrompt(clickedTag);
    }

    private void showConfirmDeleteTagPrompt(final Tag clickedTag) {
        String title = getString(R.string.are_you_sure);
        String message =  getString(R.string.action_delete) + " " + clickedTag.getTagName();


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
                tagCloudReference.child(clickedTag.getId()).removeValue();
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
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }
}

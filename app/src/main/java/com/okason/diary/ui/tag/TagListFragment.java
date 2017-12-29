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
import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.events.TagListChangeEvent;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.models.SampleData;
import com.okason.diary.models.Tag;
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



    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DataAccessManager dataAccessManager;




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

        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            dataAccessManager = new DataAccessManager(firebaseUser.getUid());
        }

        addTagbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getDisplayName())) {
                    showAddNewTagDialog();
                } else {
                    startActivity(AuthUiActivity.createIntent(getActivity()));
                }
            }
        });
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getEmail())) {
            if (dataAccessManager != null){
                dataAccessManager.getAllTags();
            }
        } else {
            showTags(generateSampleTags());
        }

    }

    private List<Tag> generateSampleTags() {
        final List<Tag> tags = new ArrayList<>();

        List<String> sampleTagNames = SampleData.getSampleTags();
        for (String name : sampleTagNames) {

            final Tag folder = new Tag();
            folder.setTagName(name);

            tags.add(folder);
        }
        return tags;
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTagListChange(TagListChangeEvent event){
        showTags(event.getTaglList());
    }




    public void showAddNewTagDialog() {
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getEmail())) {
            addTagDialog = AddTagDialogFragment.newInstance("");
            addTagDialog.show(getActivity().getFragmentManager(), "Dialog");
        } else {
            startActivity(AuthUiActivity.createIntent(getActivity()));
        }
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
        Intent tagIntent = new Intent(getActivity(), NoteListActivity.class);
        tagIntent.putExtra(Constants.TAG_FILTER, clickedTag.getTagName());
        startActivity(tagIntent);
    }

    @Override
    public void onEditTagButtonClicked(Tag clickedTag) {
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getEmail())) {
            showEditTagForm(clickedTag);
        } else {
            makeToast(getString(R.string.login_required));
        }

    }

    private void showEditTagForm(Tag clickedTag) {
        Gson gson = new Gson();
        String serializedTag = gson.toJson(clickedTag);
        addTagDialog = AddTagDialogFragment.newInstance(serializedTag);
        addTagDialog.show(getActivity().getFragmentManager(), "Dialog");
    }

    @Override
    public void onDeleteTagButtonClicked(Tag clickedTag) {
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getEmail())) {
            showConfirmDeleteTagPrompt(clickedTag);
        } else {
            makeToast(getString(R.string.login_required));
        }
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
                dataAccessManager.deleteTag(clickedTag.getId());
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

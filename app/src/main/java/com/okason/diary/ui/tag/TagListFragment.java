package com.okason.diary.ui.tag;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.okason.diary.core.events.FolderAddedEvent;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.data.TagRealmRepository;
import com.okason.diary.models.Tag;
import com.okason.diary.ui.auth.RegisterActivity;
import com.okason.diary.ui.auth.SignInActivity;
import com.okason.diary.utils.SettingsHelper;

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
    private Realm mRealm;
    private RealmResults<Tag> mTags;


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

        addTagbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewTagDialog();
            }
        });
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ProntoDiaryApplication.isCloudSyncEnabled()) {
            mAdapter = null;
            try {
                mRealm = Realm.getDefaultInstance();
                mTags = mRealm.where(Tag.class).findAll();
                mTags.addChangeListener(new RealmChangeListener<RealmResults<Tag>>() {
                    @Override
                    public void onChange(RealmResults<Tag> tags) {
                        showTags(tags);
                    }
                });
                showTags(mTags);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showEmptyText();
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
        try {
            mRealm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_tag_list, menu);
//        MenuItem search = menu.findItem(R.id.action_search);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id){
            case R.id.action_add:
                if (getActivity() != null) {
                    if (ProntoDiaryApplication.isCloudSyncEnabled()) {
                        showAddNewTagDialog();
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

    }

    @Override
    public void onEditTagButtonClicked(Tag clickedTag) {
        showEditTagForm(clickedTag);

    }

    private void showEditTagForm(Tag clickedTag) {
        addTagDialog = AddTagDialogFragment.newInstance(clickedTag.getId());
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
                new TagRealmRepository().deleteTag(clickedTag.getId());

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

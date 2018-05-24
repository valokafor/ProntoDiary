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

import com.okason.diary.NoteListActivity;
import com.okason.diary.R;
import com.okason.diary.core.listeners.OnTagSelectedListener;
import com.okason.diary.data.TagDao;
import com.okason.diary.models.realmentities.Tag;
import com.okason.diary.utils.Constants;

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
    private String sortColumn = "title";
    private Realm realm;
    private TagDao tagDao;
    private RealmResults<Tag> tags;



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
        realm = Realm.getDefaultInstance();
        tagDao = new TagDao(realm);
        tags = tagDao.getAllTags();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
       showTags(tags);

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStart() {
        super.onStart();
        tags.addChangeListener(listener);
    }

    @Override
    public void onStop() {
        tags.removeAllChangeListeners();
        super.onStop();
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
        Intent tagIntent = new Intent(getActivity(), NoteListActivity.class);
        tagIntent.putExtra(Constants.TAG_FILTER, clickedTag.getTagName());
        startActivity(tagIntent);
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
                tagDao.deleteTag(clickedTag.getId());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private OrderedRealmCollectionChangeListener<RealmResults<Tag>> listener
            = new OrderedRealmCollectionChangeListener<RealmResults<Tag>>() {
        @Override
        public void onChange(RealmResults<Tag> folderEntities, OrderedCollectionChangeSet changeSet) {

            if (changeSet == null) {
                mAdapter.notifyDataSetChanged();
                return;
            }
            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                mAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                mAdapter.notifyItemRangeInserted(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                mAdapter.notifyItemRangeChanged(range.startIndex, range.length);
            }
        }
    };
}

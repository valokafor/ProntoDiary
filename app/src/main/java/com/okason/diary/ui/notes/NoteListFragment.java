package com.okason.diary.ui.notes;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.okason.diary.R;
import com.okason.diary.core.listeners.NoteItemListener;
import com.okason.diary.models.viewModel.NoteViewModel;
import com.okason.diary.ui.addnote.AddNoteActivity;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteListFragment extends Fragment implements
        NoteListContract.View, SearchView.OnQueryTextListener {

    private View mRootView;
    private NoteListContract.Actions mPresenter;
    private NoteListAdapter mListAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean isDualScreen = false;

    @BindView(R.id.note_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_text)
    TextView mEmptyText;
    @BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;



    public NoteListFragment() {
        // Required empty public constructor
    }

    public static NoteListFragment newInstance(boolean dualScreen){
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_DUAL_SCREEN, dualScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args  != null && args.containsKey(Constants.IS_DUAL_SCREEN)){
            isDualScreen = args.getBoolean(Constants.IS_DUAL_SCREEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        ButterKnife.bind(this, mRootView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mListAdapter = new NoteListAdapter(new ArrayList<NoteViewModel>(), getContext());
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPresenter = new NoteListPresenter(this);


        //Pull to refresh
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.primary),
                ContextCompat.getColor(getActivity(), R.color.accent),
                ContextCompat.getColor(getActivity(), R.color.primary_dark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadNotes(true);
            }
        });

        mListAdapter.setNoteItemListener(new NoteItemListener() {
            @Override
            public void onNoteClick(NoteViewModel clickedNote) {
                if (isDualScreen) {
                    showDualDetailUi(clickedNote);
                } else {
                    showSingleDetailUi(clickedNote.getId());
                }
            }

            @Override
            public void onDeleteButtonClicked(NoteViewModel clickedNote) {
                showDeleteConfirmation(clickedNote);
            }
        });

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadNotes(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (id){
            case R.id.action_add:
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), AddNoteActivity.class));
                }
                break;
            case R.id.action_sort:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void showNotes(List<NoteViewModel> notes) {
        mListAdapter.replaceData(notes);
    }

    @Override
    public void showEmptyText(boolean showText) {
        if (showText){
            swipeRefreshLayout.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.GONE);

        }else {
            mAdView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }

    }

    @Override
    public void showDeleteConfirmation(NoteViewModel note) {
        boolean shouldPromptForDelete = PreferenceManager
                .getDefaultSharedPreferences(getContext()).getBoolean("prompt_for_delete", true);
        if (shouldPromptForDelete) {
            promptForDelete(note);
        } else {
            mPresenter.deleteNote(note);
        }

    }

    @Override
    public void setProgressIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });

    }

    public void showSingleDetailUi(String noteId) {
       // startActivity(NoteDetailActivity.getStartIntent(getContext(), noteId));
    }


    public void showDualDetailUi(NoteViewModel note) {
//        NoteListActivity activity = (NoteListActivity)getActivity();
//        activity.showTwoPane(note);
    }

    public void promptForDelete(final NoteViewModel note){
        String content;
        if (!TextUtils.isEmpty(note.getContent())) {
            content = note.getContent();
        } else {
            content = "";
        }
        String message =  getString(R.string.label_delete)  + " " + content.substring(0, Math.min(content.length(), 50)) + "  ... ?";


        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = (View)inflater.inflate(R.layout.dialog_title, null);
        TextView titleText = (TextView)titleView.findViewById(R.id.text_view_dialog_title);
        titleText.setText(getString(R.string.warning_are_you_sure));
        alertDialog.setCustomTitle(titleView);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteNote(note);
            }
        });
        alertDialog.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }





}

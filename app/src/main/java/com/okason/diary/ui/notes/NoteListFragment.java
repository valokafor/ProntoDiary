package com.okason.diary.ui.notes;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.okason.diary.R;
import com.okason.diary.models.viewModel.NoteViewModel;
import com.okason.diary.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteListFragment extends Fragment implements NoteListContract.View{

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
        super.onCreateOptionsMenu(menu, inflater);
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
}

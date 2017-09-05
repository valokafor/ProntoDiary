package com.okason.diary.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.okason.diary.R;
import com.okason.diary.core.events.ShowFragmentEvent;
import com.okason.diary.ui.auth.RegisterActivity;
import com.okason.diary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SyncFragment extends Fragment {

    @BindView(R.id.cancel_button)
    Button cancelButton;

    @BindView(R.id.enable_button) Button enableSyncButton;
    private View mRootView;

    private String fragmentTag = "";
    private String fragmentTitle = "";

    public static SyncFragment newInstance(String tag, String title){
        SyncFragment fragment = new SyncFragment();
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(title)){
            Bundle args = new Bundle();
            args.putString(Constants.FRAGMENT_TAG, tag);
            args.putString(Constants.FRAGMENT_TITLE, title);
            fragment.setArguments(args);
        }
        return fragment;
    }


    public SyncFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null && getArguments().containsKey(Constants.FRAGMENT_TAG)
                && getArguments().containsKey(Constants.FRAGMENT_TITLE)){
            fragmentTag = getArguments().getString(Constants.FRAGMENT_TAG);
            fragmentTitle = getArguments().getString(Constants.FRAGMENT_TITLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, mRootView);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ShowFragmentEvent(fragmentTitle, fragmentTag));
            }
        });
        enableSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RegisterActivity.class));
            }
        });

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings){
            startActivity(new Intent(getActivity(), RegisterActivity.class));
        }
        return true;
    }
}

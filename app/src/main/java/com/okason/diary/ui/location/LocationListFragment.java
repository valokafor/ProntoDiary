package com.okason.diary.ui.location;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okason.diary.models.Location;
import com.okason.diary.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationListFragment extends Fragment {

    private View rootView;
    @BindView(R.id.location_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_text) TextView emptyText;

    private Realm realm;


    public LocationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_location_list, container, false);
        ButterKnife.bind(this, rootView);
        realm = Realm.getDefaultInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    public void showLocations(List<Location> locations){
        if (locations.size() > 0){
            hideEmptyText();
        } else {
            showEmptyText();
        }

    }


    public void showEmptyText() {
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    public void hideEmptyText() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
    }


    @Override
    public void onDestroy() {
        if (realm != null && !realm.isClosed()){
            realm.close();
        }
        super.onDestroy();
    }
}

package com.okason.diary.ui.tag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okason.diary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectTagFragment extends Fragment {

    private View mRooView;


    public SelectTagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRooView = inflater.inflate(R.layout.fragment_select_tag, container, false);
        return mRooView;
    }

}

package com.okason.diary.ui.folder;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okason.diary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabhost;
    private View mRootView;




    public AccountFragment() {
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

        mRootView = inflater.inflate(R.layout.fragment_account, container, false);

        mViewPager = (ViewPager)mRootView.findViewById(R.id.viewPager);
        mTabhost = (TabLayout)mRootView.findViewById(R.id.tabs);
        // Inflate the layout for this fragment

        setupViewPager();
        return mRootView;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_account, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.action_settings:
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                break;
//        }
//        return true;
//    }

    private void setupViewPager() {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabhost.setupWithViewPager(mViewPager);
    }

}

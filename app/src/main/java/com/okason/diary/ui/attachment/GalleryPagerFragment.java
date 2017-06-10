package com.okason.diary.ui.attachment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.okason.diary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryPagerFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    public static final String ARG_PATH = "path";
    private int mPageNumber;
    private String mImagePath;
    private View mRootView;

    @BindView(R.id.gallery_full_image)
    ImageView mImageView;

    public GalleryPagerFragment() {
    }

    public static GalleryPagerFragment newInstance(int pageNumber, String imagePath) {
        GalleryPagerFragment fragment = new GalleryPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPageNumber = this.getArguments().getInt(ARG_PAGE);
        this.mImagePath = getArguments().getString(ARG_PATH);
    }

    @SuppressLint({"NewApi"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_gallery_pager, container, false);
        ButterKnife.bind(this, mRootView);


        Glide.with(getActivity().getApplicationContext())
                .load(mImagePath)
                .fitCenter()
                .crossFade()
                .into(mImageView);
        return mRootView;
    }

    public int getPageNumber() {
        return this.mPageNumber;
    }
}

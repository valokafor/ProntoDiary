package com.okason.diary.ui.attachment;


import android.annotation.SuppressLint;
import android.net.Uri;
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
    private Uri mImagePath;
    private View mRootView;

    @BindView(R.id.gallery_full_image)
    ImageView mImageView;

    public GalleryPagerFragment() {
    }

    public static GalleryPagerFragment create(int pageNumber, Uri imagePath) {
        GalleryPagerFragment fragment = new GalleryPagerFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        args.putSerializable("path", imagePath.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPageNumber = this.getArguments().getInt("page");
        this.mImagePath = Uri.parse(this.getArguments().getString("path"));
    }

    @SuppressLint({"NewApi"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_gallery_pager, container, false);
        ButterKnife.bind(this, mRootView);

        Glide.with(this.getActivity())
                .load(this.mImagePath)
                .centerCrop().crossFade()
                .error(R.drawable.image_broken).into(mImageView);
        return mRootView;
    }

    public int getPageNumber() {
        return this.mPageNumber;
    }
}

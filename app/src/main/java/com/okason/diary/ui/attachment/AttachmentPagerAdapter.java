package com.okason.diary.ui.attachment;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;

import com.okason.diary.models.Attachment;

import java.util.List;

/**
 * Created by Valentine on 5/12/2017.
 */

public class AttachmentPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Attachment> attachments;



    public AttachmentPagerAdapter(FragmentManager fm, List<Attachment> attachments) {
        super(fm);
        this.attachments = attachments;
    }


    @Override
    public Fragment getItem(int position) {
        Attachment selectedAttachment = attachments.get(position);
        String path = TextUtils.isEmpty(selectedAttachment.getUriCloudPath())?
                selectedAttachment.getUriLocalPath(): selectedAttachment.getUriCloudPath();
        return GalleryPagerFragment.create(position, Uri.parse(path) );
    }

    @Override
    public int getCount() {
        return attachments.size();
    }


}

package com.okason.diary.ui.attachment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
        return GalleryPagerFragment.newInstance(position, selectedAttachment.getFilePath() );
    }

    @Override
    public int getCount() {
        return attachments.size();
    }


}

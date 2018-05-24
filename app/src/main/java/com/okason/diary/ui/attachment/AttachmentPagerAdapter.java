package com.okason.diary.ui.attachment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.okason.diary.models.realmentities.Attachment;

import java.io.File;
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

        String filePath;
        File file = new File(selectedAttachment.getLocalFilePath());
        if (file.exists()){
            filePath = selectedAttachment.getLocalFilePath();
        }else {
            filePath = selectedAttachment.getCloudFilePath();
        }
        return GalleryPagerFragment.newInstance(position, filePath);
    }

    @Override
    public int getCount() {
        return attachments.size();
    }


}

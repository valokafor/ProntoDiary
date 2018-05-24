package com.okason.diary.ui.attachment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.okason.diary.models.realmentities.AttachmentEntity;

import java.io.File;
import java.util.List;

/**
 * Created by Valentine on 5/12/2017.
 */

public class AttachmentPagerAdapter extends FragmentStatePagerAdapter {
    private final List<AttachmentEntity> attachments;



    public AttachmentPagerAdapter(FragmentManager fm, List<AttachmentEntity> attachments) {
        super(fm);
        this.attachments = attachments;
    }


    @Override
    public Fragment getItem(int position) {
        AttachmentEntity selectedAttachment = attachments.get(position);

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

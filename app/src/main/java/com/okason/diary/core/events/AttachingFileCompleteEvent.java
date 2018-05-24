package com.okason.diary.core.events;

import com.okason.diary.models.realmentities.AttachmentEntity;

/**
 * Created by valokafor on 6/4/17.
 */

public class AttachingFileCompleteEvent {
    private final boolean resultOk;
    private final AttachmentEntity attachment;

    public AttachingFileCompleteEvent(boolean resultOk, AttachmentEntity attachment) {
        this.resultOk = resultOk;
        this.attachment = attachment;
    }

    public boolean isResultOk() {
        return resultOk;
    }

    public AttachmentEntity getAttachment() {
        return attachment;
    }
}

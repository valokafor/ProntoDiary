package com.okason.diary.core.events;

import com.okason.diary.models.Attachment;

/**
 * Created by valokafor on 6/4/17.
 */

public class AttachingFileCompleteEvent {
    private final boolean resultOk;
    private final Attachment attachment;

    public AttachingFileCompleteEvent(boolean resultOk, Attachment attachment) {
        this.resultOk = resultOk;
        this.attachment = attachment;
    }

    public boolean isResultOk() {
        return resultOk;
    }

    public Attachment getAttachment() {
        return attachment;
    }
}

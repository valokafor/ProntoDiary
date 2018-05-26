package com.okason.diary.core.events;

/**
 * Created by valokafor on 6/4/17.
 */

public class AttachingFileCompleteEvent {
    private final String attachmentId;

    public AttachingFileCompleteEvent(String id) {
        this.attachmentId = id;
    }

    public String getAttachmentId() {
        return attachmentId;
    }
}

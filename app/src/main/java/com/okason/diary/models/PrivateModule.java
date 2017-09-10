package com.okason.diary.models;

import io.realm.annotations.RealmModule;

/**
 * Created by valokafor on 9/9/17.
 */
@RealmModule(classes = {Attachment.class, Folder.class, Note.class, SubTask.class, Tag.class, Task.class, History.class, PeopleJournal.class})
public class PrivateModule {

}

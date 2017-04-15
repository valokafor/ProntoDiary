package com.okason.prontodiary.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class Note extends RealmObject{
    @PrimaryKey
    private long id;
    @Index
    private String title;
    private String content;
    private String gratitude;
    private long dateCreated;
    private long dateModified;
    

    //Relationships
    private RealmList<Attachment> attachments;
    private RealmList<TodoItem> todoItems;
    private RealmList<Tag> tags;
    private Category category;
    private RealmList<Friend> friends;

}

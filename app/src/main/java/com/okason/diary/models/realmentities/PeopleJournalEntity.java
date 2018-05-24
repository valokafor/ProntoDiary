package com.okason.diary.models.realmentities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valentine on 4/10/2017.
 */

public class PeopleJournalEntity extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private String phoneNumher;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumher() {
        return phoneNumher;
    }

    public void setPhoneNumher(String phoneNumher) {
        this.phoneNumher = phoneNumher;
    }
}
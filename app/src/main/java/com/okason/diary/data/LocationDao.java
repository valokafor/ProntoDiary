package com.okason.diary.data;

import com.okason.diary.models.Location;

import io.realm.Realm;
import io.realm.RealmResults;

public class LocationDao {

    private Realm realm;
    private final static String TAG = "FolderDao";

    public LocationDao(Realm realm) {
        this.realm = realm;
    }

    public RealmResults<Location> getAllLocations() {
        RealmResults<Location> locations = realm.where(Location.class).findAll();
        return locations;
    }

    public Location getLocationById(String id) {
        Location location = realm.where(Location.class).equalTo("id", id).findFirst();
        return location;
    }
}

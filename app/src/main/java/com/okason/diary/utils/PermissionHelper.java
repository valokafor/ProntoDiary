package com.okason.diary.utils;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.sync.permissions.ClassPermissions;
import io.realm.sync.permissions.Permission;
import io.realm.sync.permissions.RealmPermissions;

public class PermissionHelper {

    /**
     * Configure the permissions on the Realm and each model class.
     * This will only succeed the first time that this code is executed. Subsequent attempts
     * will silently fail due to `canSetPermissions` having already been removed.
     *
     * @param postInitialization block to run after the Role has been added or found, usually a
     *                           navigation to the next screen.
     */
    public static void initializePermissions(Runnable postInitialization) {
        Realm realm = Realm.getDefaultInstance();
        RealmPermissions realmPermission = realm.where(RealmPermissions.class).findFirst();
        if (realmPermission == null || realmPermission.getPermissions().first().canModifySchema()) {
            // Permission schema is not yet locked
            // Temporary workaround: register an async query to wait until the permission system is synchronized before applying changes.
            RealmResults<RealmPermissions> realmPermissions = realm.where(RealmPermissions.class).findAllAsync();
            realmPermissions.addChangeListener((permissions, changeSet) -> {
                if (changeSet.isCompleteResult()) {
                        realmPermissions.removeAllChangeListeners();
                        // setup and lock the schema
                        realm.executeTransactionAsync(bgRealm -> {
                            // Remove update permissions from the __Role table to prevent a malicious user
                            // from adding themselves to another user's private role.
                            Permission rolePermission = bgRealm.where(ClassPermissions.class).equalTo("name", "__Role").findFirst().getPermissions().first();
                            rolePermission.setCanUpdate(false);
                            rolePermission.setCanCreate(false);// we use the user private role, no other roles are allowed to be created

                            // Lock the permission and schema
                            RealmPermissions permission = bgRealm.where(RealmPermissions.class).equalTo("id", 0).findFirst();
                            Permission everyonePermission = permission.getPermissions().first();
                            everyonePermission.setCanModifySchema(false);
                            everyonePermission.setCanSetPermissions(false);
                        }, () -> {
                            realm.close();
                            postInitialization.run();
                        });
                }
            });
        } else {
            realm.close();
            postInitialization.run();
        }
    }
}

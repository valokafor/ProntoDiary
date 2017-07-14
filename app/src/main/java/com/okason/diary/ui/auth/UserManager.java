/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.okason.diary.ui.auth;

import com.facebook.login.LoginManager;
import com.okason.diary.core.ProntoDiaryApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class UserManager {
//    public static RealmConfiguration getLocalConfig() {
//        RealmConfiguration configuration  = new RealmConfiguration.Builder()
//                .name(Constants.REALM_DATABASE)
//                .schemaVersion(5)
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        return configuration;
//    }

    public static RealmConfiguration getConfig() {
        SyncUser  user = SyncUser.currentUser();
        return getSyncConfig(user);
    }

    // Supported authentication mode
    public enum AUTH_MODE {
        PASSWORD,
        FACEBOOK,
        GOOGLE
    }
    private static AUTH_MODE mode = AUTH_MODE.PASSWORD; // default

    public static void setAuthMode(AUTH_MODE m) {
        mode = m;
    }

    public static void logoutActiveUser() {
        switch (mode) {
            case PASSWORD: {
                // Do nothing, handled by the `User.currentUser().logout();`
                break;
            }
            case FACEBOOK: {
                LoginManager.getInstance().logOut();
                break;
            }
            case GOOGLE: {
                // the connection is handled by `enableAutoManage` mode
                break;
            }
        }
        ProntoDiaryApplication.setCloudSyncEnabled(false);
        SyncUser.currentUser().logout();
    }

    // Configure Realm for the current active user
    public static void setActiveUser(SyncUser user) {
     //   SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, ProntoDiaryApplication.REALM_URL).build();
        Realm.removeDefaultConfiguration();
        Realm.setDefaultConfiguration(getSyncConfig(user));
    }

    public static RealmConfiguration getSyncConfig(SyncUser user) {
        String identityToken = user.getAccessToken().identity();
        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, ProntoDiaryApplication.REALM_URL)
                .name(identityToken + ".realm")
                .build();
        return defaultConfig;
    }
}

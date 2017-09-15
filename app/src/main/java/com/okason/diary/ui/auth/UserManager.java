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
import com.okason.diary.models.CommonModule;
import com.okason.diary.models.PrivateModule;
import com.okason.diary.models.ProntoDiaryUser;
import com.okason.diary.utils.Constants;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class UserManager {
    public static RealmConfiguration getLocalConfig() {
        RealmConfiguration configuration  = new RealmConfiguration.Builder()
                .name(Constants.REALM_DATABASE)
                .schemaVersion(5)
                .deleteRealmIfMigrationNeeded()
                .build();
        return configuration;
    }

    public static RealmConfiguration getConfig() {
        SyncUser  user = SyncUser.currentUser();
        return getSyncConfig(user);
    }

    public static ProntoDiaryUser getProntoDiaryUser(SyncUser user) {
        try {
            ProntoDiaryUser prontoDiaryUser = null;
            SyncConfiguration syncConfiguration = UserManager.getPublicConfig(user);
            Realm commonRealm = Realm.getInstance(syncConfiguration);
            prontoDiaryUser = commonRealm.where(ProntoDiaryUser.class).equalTo("realmUserId", SyncUser.currentUser().getIdentity()).findFirst();
            prontoDiaryUser = commonRealm.copyFromRealm(prontoDiaryUser);
            commonRealm.close();
            return prontoDiaryUser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ProntoDiaryUser getProntoDiaryUserById(String referrerUid) {
        ProntoDiaryUser prontoDiaryUser;
        try {
            SyncConfiguration syncConfiguration = UserManager.getPublicConfig(SyncUser.currentUser());
            Realm commonRealm = Realm.getInstance(syncConfiguration);
            prontoDiaryUser = commonRealm.where(ProntoDiaryUser.class).equalTo("realmUserId", referrerUid).findFirst();
            prontoDiaryUser = commonRealm.copyFromRealm(prontoDiaryUser);
            commonRealm.close();
            return prontoDiaryUser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        SyncUser.currentUser().logout();
    }

    // Configure Realm for the current active user
    public static void setActiveUser(SyncUser user) {
     //   SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, ProntoDiaryApplication.REALM_URL).build();
        Realm.removeDefaultConfiguration();
        Realm.setDefaultConfiguration(getSyncConfig(user));
    }

    public static SyncConfiguration getSyncConfig(SyncUser user) {
        String identityToken = user.getAccessToken().identity();
        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, ProntoDiaryApplication.REALM_URL)
                .name(identityToken + ".realm")
                .modules(new PrivateModule())
                .build();
        return defaultConfig;
    }

    public static SyncConfiguration getPublicConfig(SyncUser user) {
        SyncConfiguration commonConfig = new SyncConfiguration.Builder(user, ProntoDiaryApplication.COMMON_REALM_URL)
                .name("user_info.realm")
                .modules(new CommonModule())
                .build();
        return commonConfig;
    }
}

package com.okason.diary.models;

/**
 * Created by valokafor on 6/1/17.
 */

public class ProntoDiaryUser {
    private String id;
    private String firebaseUid;
    private String fcmToken;
    private String displayName;
    private String emailAddress;
    private boolean isEmailValid;
    private long totalFileSize;
    private boolean isPremium;
    private String upgradeMethod;
    private String realmPassword;
    private String loginProvider;
    private String photoUrl;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isEmailValid() {
        return isEmailValid;
    }

    public void setEmailValid(boolean emailValid) {
        isEmailValid = emailValid;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getUpgradeMethod() {
        return upgradeMethod;
    }

    public void setUpgradeMethod(String upgradeMethod) {
        this.upgradeMethod = upgradeMethod;
    }

    public String getLoginProvider() {
        return loginProvider;
    }

    public void setLoginProvider(String loginProvider) {
        this.loginProvider = loginProvider;
    }

    public String getRealmPassword() {
        return realmPassword;
    }

    public void setRealmPassword(String realmPassword) {
        this.realmPassword = realmPassword;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

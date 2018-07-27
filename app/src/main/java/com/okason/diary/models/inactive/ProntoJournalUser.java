package com.okason.diary.models.inactive;

/**
 * Created by valokafor on 6/1/17.
 */

public class ProntoJournalUser {
    private String firebaseUid;
    private String name;
    private String fcmTokens;
    private String emailAddress;
    private boolean isEmailValid;
    private long totalFileSize;
    private boolean isPremium;
    private String dateCreated;
    private long dateModified;
    private int pinCode;

    public ProntoJournalUser(){
        firebaseUid = "";
        name = "";
        fcmTokens = "";
        emailAddress = "";
        isEmailValid = false;
        totalFileSize = 0;
        dateCreated = "";
        dateModified = 0;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFcmTokens() {
        return fcmTokens;
    }

    public void setFcmTokens(String fcmTokens) {
        this.fcmTokens = fcmTokens;
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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }
}
package com.okason.diary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/**
 * Singleton for app settings.
 */

public class SettingsHelper {

  private static final String TAG = SettingsHelper.class.getSimpleName();
  private static final String PREFS_FILE = "prefs";
  private static SettingsHelper INSTANCE;

  private static final String MESSAGING_TOKEN = "MessagingToken";


  private Context context;
 // private FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

  // cache expiration in seconds for firebase remote config
  // one minute for debug builds, 60 minutes for production
 // private static final int CACHE_EXPIRATION = BuildConfig.DEBUG ? 60 : 60 * 60;

  public String getDeviceId(Context context) {
    return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
  }




  /**
   * Get helper instance.
   */
  public static SettingsHelper getHelper(Context context) {
    if (INSTANCE == null) {
      // ensure application context is used to prevent leaks
      INSTANCE = new SettingsHelper(context.getApplicationContext());
    }
    return INSTANCE;
  }

  private SettingsHelper(Context context) {
    this.context = context;
  }

  private SharedPreferences getSharedPreferences() {
    return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
  }

  public void setMessagingToken(String token) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putString(MESSAGING_TOKEN, token);
    editor.commit();
  //  EventBus.getDefault().post(new MessagingTokenChangedEvent(token));
  }

  public String getMessagingToken() {
    return getSharedPreferences().getString(MESSAGING_TOKEN, null);
  }


  public boolean isRegisteredUser() {
    return getSharedPreferences().getBoolean(Constants.REGISTERED_USER, false);
  }

  public void setRegisteredUser(boolean registered){
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.REGISTERED_USER, registered).apply();
  }




  public boolean shouldShowNoteDetailExplainer() {
    boolean noteDetailExplainerShownCount = getSharedPreferences().getBoolean(Constants.NOTE_DETAIL_EXPLAINER_COUNT, true);
    return noteDetailExplainerShownCount;
  }

  public void onNoteDetailExplainerShown(boolean show){
    int noteDetailExplainerShownCount = getSharedPreferences().getInt(Constants.NOTE_DETAIL_EXPLAINER_COUNT, 0);
    noteDetailExplainerShownCount++;
    getSharedPreferences().edit().putBoolean(Constants.NOTE_DETAIL_EXPLAINER_COUNT, show).commit();
  }

  public boolean shouldDownloadDataFromFirebase() {
    SharedPreferences mSharedPreferences = getSharedPreferences();
    return mSharedPreferences.getBoolean(Constants.SHOULD_DOWNLOAD_FROM_FIREBASE, true);
  }

  public void setDownloadDataFromFirebase(boolean shouldDownload) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.SHOULD_DOWNLOAD_FROM_FIREBASE, shouldDownload);
    editor.commit();
  }


  public long getLastSyncDate() {
    long lastSync = getSharedPreferences().getLong(Constants.DATE_LAST_SYNC, 0);
    return 0;
  }

  public boolean isPremiumUser() {
    boolean result = getSharedPreferences().getBoolean(Constants.PREMIUM_USER, false);
    return result;
  }

  public void setPremiumUser(boolean paid){
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.PREMIUM_USER, paid).apply();
  }

  public void saveUserPinCode(int pinNumber){
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putInt(Constants.PIN_CODE, pinNumber).apply();
    editor.putBoolean(Constants.PIN_CODE_ENABLED, true).apply();

  }

  public void removePinCode(){
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putInt(Constants.PIN_CODE, 0).apply();
    editor.putBoolean(Constants.PIN_CODE_ENABLED, false).apply();

  }

  public int getSavedPinCode(){
    int result = getSharedPreferences().getInt(Constants.PIN_CODE, 0);
    return result;
  }

  public boolean isPinCodeEnabled(){
    boolean result = getSharedPreferences().getBoolean(Constants.PIN_CODE_ENABLED, false);
    return result;
  }



}

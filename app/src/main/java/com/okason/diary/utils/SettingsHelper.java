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
  private static final String PAIRING_COMPLETE = "PairingComplete";
  private static final String TV_NAME = "TvName";
  private static final String EPG_COUNTRY = "EpgCountry";
  private static final String EPG_AREA = "EpgArea";
  private static final String EPG_ZIP_CODE = "EpgZipCode";
  private static final String EPG_PROVIDER = "EpgProvider";
  private static final String REST_SERVER = "RestServer";

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
}

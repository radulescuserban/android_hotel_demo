package com.example.serbantheodor.android_hotel;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.util.Log;

/**
 * Created by Serban Theodor on 07-Mar-16.
 */
public class MyPreferenceManager {

    public static final String TAG = MyPreferenceManager.class.getSimpleName();

    // Sharedpref file name
    private static final String PREF_NAME = "android_hotel";
    // All Shared Preferences Keys
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_ANDROID_ID = "android_id";
    private static final String KEY_GCM_TOKEN = "token";
    private static final String KEY_REG_DEVICE_ID= "reg_device_id";

    private static final String KEY_RECEPTION_ID= "reception_id";

    private static final String KEY_DIRECTOR_FOR_IMAGES_PATH = "director_for_images_path";

    private static final String KEY_UNREAD_MESSAGES = "unread_messages";
    private static final String KEY_DYNAMIC_PAGE_ID = "dynamic_page_id";

    private static  final String KEY_REGISTRATION_SENT = "registration_sent";

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    Context _context;

    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
        //editor.commit();
    }

    public void add_directory_for_images_path(String directory_for_images_path)
    {
        editor.putString(KEY_DIRECTOR_FOR_IMAGES_PATH, directory_for_images_path);
        Log.d(TAG, "add_directory");
        editor.commit();
    }

    public String get_directory_for_images_path()
    {
        return pref.getString(KEY_DIRECTOR_FOR_IMAGES_PATH, null);
    }

    public void add_registration_sent(boolean registration_sent)
    {
        editor.putBoolean(KEY_REGISTRATION_SENT, registration_sent);
        editor.commit();
    }

    public boolean get_registration_sent()
    {
        return pref.getBoolean(KEY_REGISTRATION_SENT, false);
    }

    public void add_reception_id(String reception_id)
    {
        editor.putString(KEY_RECEPTION_ID, reception_id);
        editor.commit();
    }

    public void add_dynamic_page_id(int dynamic_page_id)
    {
        editor.putInt(KEY_DYNAMIC_PAGE_ID, dynamic_page_id);
        editor.commit();
    }

    public int get_dynamic_page_id()
    {
        return pref.getInt(KEY_DYNAMIC_PAGE_ID, 0);
    }

    public void add_unread_messages(int unread_messages)
    {
        editor.putInt(KEY_UNREAD_MESSAGES, unread_messages);
        editor.commit();
    }

    public int get_unread_messages()
    {
        return pref.getInt(KEY_UNREAD_MESSAGES, 0);
    }

    public String get_reception_id()
    {
        return pref.getString(KEY_RECEPTION_ID, null);
    }

    public void add_reg_device_id(String reg_device_id)
    {
        editor.putString(KEY_REG_DEVICE_ID, reg_device_id);
        editor.commit();
    }

    public void add_android_id(String android_id)
    {
        editor.putString(KEY_ANDROID_ID, android_id);
        editor.commit();
    }

    public void add_gcm_token(String token){
        editor.putString(KEY_GCM_TOKEN, token);
        editor.commit();
    }

    public String get_gcm_token(){
        return pref.getString(KEY_GCM_TOKEN, null);
    }

    public String get_reg_device_id()
    {
        return pref.getString(KEY_REG_DEVICE_ID, null);
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear_messages()
    {
        editor.remove(KEY_NOTIFICATIONS);
        editor.commit();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}

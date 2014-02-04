package com.dharrya.android.notifier.app;


import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
    public static String SENDER_ID = "531611139632";
    public static String LOG_TAG = "DHARRYA_NOTIFIER";

    private static Preferences singleton;
    private Context context;
    private String registrationId;
    private String deviceName;
    private String password;

    public Preferences(Context context) {
        this.context = context;
    }

    public static Preferences getInstance(Context context) {
        if (singleton == null) {
            singleton = new Preferences(context).load();
        }
        return singleton;
    }

    public static Preferences getInstance() {
        return singleton;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPassword() {
        return password;
    }

    public Preferences setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
        return this;
    }

    public Preferences setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public Preferences setPassword(String password) {
        this.password = password;
        return this;
    }

    public Preferences load() {
        final SharedPreferences prefs = context.getSharedPreferences("dharrya_prefs", Context.MODE_PRIVATE);
        this.registrationId = prefs.getString("reg_id", "");
        this.deviceName = prefs.getString("device_name", "");
        this.password = prefs.getString("password", "");
        return this;
    }

    public Preferences save() {
        final SharedPreferences.Editor editor =
                context.getSharedPreferences("dharrya_prefs", Context.MODE_PRIVATE)
                .edit();

        editor.putString("reg_id", this.registrationId);
        editor.putString("device_name", this.deviceName);
        editor.putString("password", this.password);
        editor.commit();
        return this;
    }
}

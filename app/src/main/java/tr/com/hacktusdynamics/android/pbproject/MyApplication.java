package tr.com.hacktusdynamics.android.pbproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import java.util.UUID;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    public static Context sApplicationContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationContext = getApplicationContext();
        SharedPreferences sp = sApplicationContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        if(!sp.contains(Constants.PREF_GUEST_UUID)){
            String guestUUID = UUID.randomUUID().toString();
            SharedPreferences.Editor spe = sp.edit();
            spe.putString(Constants.PREF_GUEST_UUID, guestUUID);
            spe.putString(Constants.PREF_CURRENT_USER_UUID, guestUUID);
            spe.commit();
        }

        Log.d(TAG, "onCreate...");
        Log.d(TAG, "guestUUID: " + sp.getString(Constants.PREF_GUEST_UUID, null));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged...");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory...");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate...");
    }
}
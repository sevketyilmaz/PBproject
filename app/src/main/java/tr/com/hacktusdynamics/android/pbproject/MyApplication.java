package tr.com.hacktusdynamics.android.pbproject;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    public static Context sApplicationContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationContext = getApplicationContext();
        Log.d(TAG, "onCreate...");
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
package tr.com.hacktusdynamics.android.pbproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.models.MyLab;
import tr.com.hacktusdynamics.android.pbproject.models.UserProfile;

import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_USER;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    public static Context sApplicationContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationContext = getApplicationContext();
        SharedPreferences sp = sApplicationContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        /**
         * If app runs in the first time: (!PREF_GUEST_EXIST)
         * Create guest profile and save to the database
         * Set the currentUserUUID to the guest profile uuid
         */
        if(!sp.contains(Constants.PREF_GUEST_EXIST)){
            String guestUUID = UUID.randomUUID().toString();
            SharedPreferences.Editor spe = sp.edit();
            spe.putBoolean(Constants.PREF_GUEST_EXIST, true);
            //Check guestUUID before delete the UserProfile, you should never delete guestProfile!
            spe.putString(Constants.PREF_GUEST_UUID, guestUUID);
            spe.putString(Constants.PREF_CURRENT_USER_UUID, guestUUID);
            spe.commit();

            //add guestProfile to the database
            IProfile guestProfile = new UserProfile(guestUUID,
                    sApplicationContext.getString(R.string.profile_guest_name),
                    sApplicationContext.getString(R.string.profile_guest_email),
                    sApplicationContext.getString(R.string.profile_guest_password)
            );
            guestProfile.withIcon(sApplicationContext.getResources().getDrawable(R.drawable.guest_avatar));
            guestProfile.withIdentifier(IDENTIFIER_USER);

            MyLab.get(sApplicationContext).addUserProfile((UserProfile) guestProfile);
        }

        Log.d(TAG, "onCreate...");
        Log.d(TAG, "currentUserUUID: " + sp.getString(Constants.PREF_CURRENT_USER_UUID, null));
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
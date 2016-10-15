package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.R;

import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_NAME;
import static tr.com.hacktusdynamics.android.pbproject.MyApplication.sApplicationContext;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SharedPreferences sp;
    private SharedPreferences.Editor spe;

    private Switch smsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp = sApplicationContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        spe = sp.edit();

        smsSwitch = (Switch) findViewById(R.id.switch_sms);
        smsSwitch.setChecked(sp.getBoolean(Constants.PREF_SMS_SEND, true));

        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                spe.putBoolean(Constants.PREF_SMS_SEND, b);
                spe.commit();

                Log.d(TAG, "smsSwitch Clicked, sendSMS: " + sp.getBoolean(Constants.PREF_SMS_SEND, true));
            }
        });
    }
}

package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.Box;
import tr.com.hacktusdynamics.android.pbproject.models.MyLab;
import tr.com.hacktusdynamics.android.pbproject.services.MyBluetoothService;
import tr.com.hacktusdynamics.android.pbproject.ui.fragments.PlaceHolderFragment;
import tr.com.hacktusdynamics.android.pbproject.utils.AlarmTimeComparator;

import static tr.com.hacktusdynamics.android.pbproject.MyApplication.sApplicationContext;

public class CreateAlarmActivity extends AppCompatActivity {
    private static final String TAG = CreateAlarmActivity.class.getSimpleName();

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE= 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private List<Box> mAlarms;
    private MyLab myLab = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private MyBluetoothService myBluetoothService = null;

    public List<Box> getAlarms() {
        return mAlarms;
    }

    public String showAlarms() {
        if(mAlarms.size() != 0) {
            Collections.sort(mAlarms, new AlarmTimeComparator());
            StringBuilder sb = new StringBuilder();
            for (Box bf : mAlarms) {
                sb.append("( " + bf.getBoxNumber() + " - " + bf.getAlarmTime() + " - " + bf.getUserProfileId() + " )");
            }
            return sb.toString();
        }else{
            return "";
        }
    }

    public void addOrUpdateAlarm(int boxNumber, long dateTime, String userProfileId) {
        boolean inList = false;
        Box inListBox = null;
        for(Box box : mAlarms){
            if(box.getBoxNumber() == (boxNumber-1)) {
                inList = true;
                inListBox = box;
            }
        }
        if(inList) {
            inListBox.setAlarmTime(new Date(dateTime));
        }else {
            Box b = new Box((boxNumber - 1), new Date(dateTime), userProfileId);
            mAlarms.add(b);
        }
    }
    public void removeAlarm(int boxNumber){
        Box b;
        for(int i = 0; i< mAlarms.size(); i++){
            b = mAlarms.get(i);
            if(b.getBoxNumber() == (boxNumber-1)) {
                mAlarms.remove(b);
            }
        }
    }

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * The {@link TabLayout} that contains the tabs.
     */
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.create_alarm_activity_title);
        //TODO: getSupportActionBar().setSubtitle("subtitle"); add total box number that set for alarm...(CriminalIntent EOC12)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the seven
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons(); //sets icons to the tabs

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Log.d(TAG, showAlarms());
                //TODO: Save Alarms and boxes to the database...
                if(mAlarms.size()!= 0)
                    myLab.saveAlarmAndBoxes(mAlarms);
            }
        });

        myLab = MyLab.get(sApplicationContext);
        mAlarms = new ArrayList<>();
    }

    /**
     * Setup icons for 7 tabs.
     */
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_1));
        tabLayout.getTabAt(1).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_2));
        tabLayout.getTabAt(2).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_3));
        tabLayout.getTabAt(3).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_4));
        tabLayout.getTabAt(4).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_5));
        tabLayout.getTabAt(5).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_6));
        tabLayout.getTabAt(6).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_7));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:{
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.action_bluetooth_scan:{
                Toast.makeText(this, "Bluetooth Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (myBluetoothService == null) {
            //TODO:  setupChat();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    //TODO: connectDevice(data, true);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //TODO: setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceHolderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 7; // Show 7 total pages.
        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*
            switch (position) {
                case 0:
                    return "1. SECTION";
                case 1:
                    return "2. SECTION ";
                case 2:
                    return "3. SECTION";
            }
            */
            return null;
        }
    }
}

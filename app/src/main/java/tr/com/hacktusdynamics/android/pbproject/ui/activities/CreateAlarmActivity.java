package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.telephony.SmsManager;
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

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.Box;
import tr.com.hacktusdynamics.android.pbproject.models.MyLab;
import tr.com.hacktusdynamics.android.pbproject.receivers.AlarmReceiver;
import tr.com.hacktusdynamics.android.pbproject.services.MyBluetoothService;
import tr.com.hacktusdynamics.android.pbproject.ui.fragments.PlaceHolderFragment;
import tr.com.hacktusdynamics.android.pbproject.utils.AlarmTimeComparator;
import tr.com.hacktusdynamics.android.pbproject.utils.BluetoothStringUtils;

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

    private AlarmManager mAlarmManager = null;

    /**
     * The Handler that gets information back from the MyBluetoothService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case MyBluetoothService.STATE_CONNECTED:
                            setStatus(R.string.title_connected);
                            break;
                        case MyBluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case MyBluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, writeMessage);
                    Toast.makeText(CreateAlarmActivity.this, "Send Message \n" + writeMessage, Toast.LENGTH_SHORT).show();
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    //byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    //String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    String readMessage = (String) msg.obj;
                    Log.d(TAG, readMessage);
                    Toast.makeText(CreateAlarmActivity.this, "Read Message \n" + readMessage, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // log the connected device's name
                    Log.d(TAG, "connected device name :" + msg.getData().getString(Constants.DEVICE_NAME));
                    break;
                case Constants.MESSAGE_TOAST:
                        Toast.makeText(CreateAlarmActivity.this, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
        Log.d(TAG, "onCreate()");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.create_alarm_activity_title);
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

        myLab = MyLab.get(sApplicationContext);
        mAlarms = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //If device has no bluetooth adapter close the activity.
        if(mBluetoothAdapter == null){
            Toast.makeText(this, R.string.bluetooth_not_available, Toast.LENGTH_LONG).show();
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check that we're actually connected before trying anything
                if (myBluetoothService.getState() != MyBluetoothService.STATE_CONNECTED) {
                    Snackbar.make(view, R.string.not_connected, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    MediaPlayer.create(CreateAlarmActivity.this, R.raw.not_connected).start();
                    return;
                }
                //Check there is actually something to send
                if(mAlarms.size()!= 0){
                    Collections.sort(mAlarms, new AlarmTimeComparator());//Sort alarms
                    //Save Alarms and boxes to the database...
                    myLab.saveAlarmAndBoxes(mAlarms);

                    //send through bluetooth
                    String json = BluetoothStringUtils.setAllBoxesActionBluetoothString(mAlarms);
                    json += '\n';
                    Log.d(TAG, json);
                    sendMessageToDevice(json);

                    //create Notifications for each alarm.
                    createNotificationsForEachAlarm();

                    //TODO: close activity
                    //finishTheActivity();
                    //finish();
                }else{
                    Snackbar.make(view, R.string.no_alarm_selected, Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()");
        // If BT is not on, request that it be enabled.
        // setupBluetoothService() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else if (myBluetoothService == null) {
            //setup Bluetooth Service;
            setupBluetoothService();
        }

        //Register for broadcast when Bluetooth on and off
        IntentFilter filter =  new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
        if(myBluetoothService != null){
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (myBluetoothService.getState() == MyBluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                myBluetoothService.start();
            }
        }
    }

    private void setupBluetoothService() {
        if(myBluetoothService == null)
            myBluetoothService = new MyBluetoothService(this, mHandler);
    }

    //Updates the status on the action bar.
    @Nullable
    private void setStatus(int subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }
    @Nullable
    private void setStatus(CharSequence subtitle){
        getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:{
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                //TODO This is send a test SMS message to 05313083780 from settings
                //sendSmsMessages();
                return true;
            }
            case R.id.action_bluetooth_scan:{
                //Toast.makeText(this, "Bluetooth Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop()...");

        //unregister Broadcast listeners
        this.unregisterReceiver(mReceiver);

        if(myBluetoothService != null){
            myBluetoothService.stop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    //TODO: setupChat();
                    setupBluetoothService();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        Toast.makeText(this, address, Toast.LENGTH_LONG).show();
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        myBluetoothService.connect(device);
    }
    /**
     * Sends a message.
     *
     * @param message A string of text to send to device.
     */
    private void sendMessageToDevice(String message) {
        // DoubleCheck that we're actually connected before trying anything
        if (myBluetoothService.getState() != MyBluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // DoubleCheck that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            myBluetoothService.write(send);
        }
    }

    /**
     * Create notifications for each alarm in mAlarms
     */
    //@TargetApi(Build.VERSION_CODES.KITKAT)
    private void createNotificationsForEachAlarm() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(Constants.ACTION_ALARM_PILLBOX);
        for (int i = 0; i < mAlarms.size(); i++) {
            Box box = mAlarms.get(i);
            intent.putExtra(Constants.BOX_ID, box.getBoxNumber());
            intent.putExtra(Constants.DATE, box.getAlarmTime().toString());
            PendingIntent operation = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, box.getAlarmTime().getTime(), operation);
            Log.d(TAG, "ALARM CREATED : " + box.getBoxNumber() + "/" + box.getAlarmTime().toString());
        }

    }

    private void sendSmsMessages() {
        try {
            Log.d(TAG, "sendSmsMessage()...");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("00905313083780", null, "sms test", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    private void finishTheActivity() {
        onStop();
    }
*/

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

    /**
     * The BroadcastReceiver that listens for any BluetoothAdapter state changes
     * ON or OFF after the CreateAlarmActivity created... If its OFF, Activity.finish();
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(CreateAlarmActivity.this, R.string.bluetooth_disabled,Toast.LENGTH_SHORT).show();
                        CreateAlarmActivity.this.finish();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(CreateAlarmActivity.this, "BL on",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };
}

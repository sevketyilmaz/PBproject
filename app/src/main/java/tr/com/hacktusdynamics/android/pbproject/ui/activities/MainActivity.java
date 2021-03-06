package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.Alarm;
import tr.com.hacktusdynamics.android.pbproject.models.MyLab;
import tr.com.hacktusdynamics.android.pbproject.models.UserProfile;

import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_HEADER_ADD_ACCOUNT;
import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_HEADER_MANAGE_ACCOUNT;
import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_ITEM_CREATE_ALARM;
import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_ITEM_HOME;
import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_ITEM_MEDICATIONS;
import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_STICKY_HELP;
import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_STICKY_SETTINGS;
import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_USER;
import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_CURRENT_USER_UUID;
import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_NAME;
import static tr.com.hacktusdynamics.android.pbproject.MyApplication.sApplicationContext;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mAlarmRecyclerView;
    private AlarmAdapter mAlarmAdapter;
    private TextView mEmptyElementTextView;

    //save our drawer or header
    private AccountHeader accountHeader = null;
    private Drawer navigationDrawer = null;

    private MyLab myLab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        myLab = MyLab.get(sApplicationContext);

        mAlarmRecyclerView = (RecyclerView) findViewById(R.id.alarm_recycler_view);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEmptyElementTextView = (TextView) findViewById(R.id.empty_element_text_view);

        //create the account header
        buildHeader(false, savedInstanceState);

        //create the drawer
        navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)

                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home)
                                .withIcon(FontAwesome.Icon.faw_home)
                                .withIdentifier(IDENTIFIER_ITEM_HOME),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_create_alarm)
                                .withIcon(FontAwesome.Icon.faw_history)
                                .withIdentifier(IDENTIFIER_ITEM_CREATE_ALARM),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_medications)
                                .withIcon(FontAwesome.Icon.faw_medkit)
                                .withIdentifier(IDENTIFIER_ITEM_MEDICATIONS)
                )
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.action_settings)
                                .withIcon(FontAwesome.Icon.faw_cog)
                                .withIdentifier(IDENTIFIER_STICKY_SETTINGS),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help)
                                .withIcon(FontAwesome.Icon.faw_question_circle)
                                .withIdentifier(IDENTIFIER_STICKY_HELP)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == IDENTIFIER_ITEM_HOME) {
                                //TODO:
                                //since its main activity, do nothing
                                Toast.makeText(MainActivity.this, "Home clicked!", Toast.LENGTH_LONG).show();
                            } else if (drawerItem.getIdentifier() == IDENTIFIER_ITEM_CREATE_ALARM) {
                                intent = new Intent(MainActivity.this, CreateAlarmActivity.class);
                            } else if (drawerItem.getIdentifier() == IDENTIFIER_ITEM_MEDICATIONS) {
                                Toast.makeText(MainActivity.this, "Medications opening !", Toast.LENGTH_LONG).show();
                                intent = new Intent(MainActivity.this, MedicationsActivity.class);
                            } else if (drawerItem.getIdentifier() == IDENTIFIER_STICKY_SETTINGS) {
                                //Toast.makeText(MainActivity.this, "Setting clicked!", Toast.LENGTH_LONG).show();
                                //openUserSettingsActivity();
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                            } else if (drawerItem.getIdentifier() == IDENTIFIER_STICKY_HELP) {
                                //Toast.makeText(MainActivity.this, "Help clicked!", Toast.LENGTH_LONG).show();
                                intent = new Intent(MainActivity.this, HelpActivity.class);
                            }

                            //Start the clicked item activity
                            if (intent != null) {
                                //startActivity(intent);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();


        UpdateContentUI();
    }
/*
    private void openUserSettingsActivity() {
        Toast.makeText(this, "SettingActivity clicked!", Toast.LENGTH_LONG).show();
        //intent = new Intent(MainActivity.this, CreateAlarmActivity.class);

    }
*/
    @Override
    protected void onResume() {
        super.onResume();
        UpdateContentUI();
    }

    /**
     * small helper method to reuse the logic to build the AccountHeader
     * this will be used to replace the header of the drawer with a compact/normal header
     *
     * @param compact
     * @param savedInstanceState
     */
    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_green)
                .withCompactStyle(compact)
                .withProfiles(
                        getUserProfilesAndItems() //gets user profiles and other drawer items
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier ADD_ACCOUNT add a new profile
                        Intent intent = null;
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == IDENTIFIER_HEADER_ADD_ACCOUNT) {
                            intent = new Intent(MainActivity.this, AddAccountActivity.class);
                            //TODO: Save profile
                            //TODO: Create profile drawer item from saved profile and show it in UI
                            /*
                                IProfile profileNew = new ProfileDrawerItem()
                                    .withNameShown(true)
                                    .withName("New Profile")
                                    .withEmail("new@gmail.com")
                                    .withIcon(getResources().getDrawable(R.drawable.profile_new));

                            if (accountHeader.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them
                                accountHeader.addProfile(profileNew, accountHeader.getProfiles().size() - 2);
                            } else {
                                accountHeader.addProfiles(profileNew);
                            }
                            */
                        } else if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == IDENTIFIER_HEADER_MANAGE_ACCOUNT) {
                            intent = new Intent(MainActivity.this, ManageAccountActivity.class);
                            //TODO: update the UI
                        }else if(profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == IDENTIFIER_USER) {
                            Toast.makeText(sApplicationContext, "The User", Toast.LENGTH_SHORT).show();
                            SharedPreferences sp = sApplicationContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                            String currentUserUUIDString = sp.getString(PREF_CURRENT_USER_UUID, null);
                            if(((UserProfile)profile).getId().toString().equalsIgnoreCase(currentUserUUIDString)){
                                Toast.makeText(sApplicationContext, "SAME User", Toast.LENGTH_SHORT).show();
                                //TODO:
                            }else{
                                Toast.makeText(sApplicationContext, "Different User", Toast.LENGTH_SHORT).show();
                                //TODO:
                                SharedPreferences.Editor spe = sp.edit();
                                spe.putString(PREF_CURRENT_USER_UUID, ((UserProfile)profile).getId().toString());
                                spe.commit();

                                UpdateContentUI();
                            }
                        }

                        if(intent != null){
                            MainActivity.this.startActivity(intent);
                        }

                        //false if you have not consumed the event And it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    private ArrayList<IProfile> getUserProfilesAndItems() {
        List<IProfile> p = myLab.getUserProfiles();

        p.add(new ProfileSettingDrawerItem()
                .withName(getString(R.string.add_account))
                .withDescription(getString(R.string.add_account_description))
                .withIcon(GoogleMaterial.Icon.gmd_add)
                //.withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus).actionBar().paddingDp(5).colorRes(R.color.material_drawer_dark_primary_text))
                .withIdentifier(IDENTIFIER_HEADER_ADD_ACCOUNT)
        );
        p.add(new ProfileSettingDrawerItem()
                .withName(getString(R.string.manage_account))
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withIdentifier(IDENTIFIER_HEADER_MANAGE_ACCOUNT)
        );

        return (ArrayList<IProfile>) p;
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Setting clicked!", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
    private class AlarmHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Alarm mAlarm;
        private TextView alarmsNumberView;
        private TextView alarmsDateTimeView;

        public AlarmHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            alarmsNumberView = (TextView) itemView.findViewById(R.id.alarms_number);
            alarmsDateTimeView = (TextView) itemView.findViewById(R.id.alarms_datetime);
        }

        public void bindAlarm(Alarm alarm){
            mAlarm = alarm;
            alarmsNumberView.setText(Integer.toString(mAlarm.getId()));
            alarmsDateTimeView.setText(mAlarm.getCreatedTime().toString());
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(MainActivity.this, "Id: " + mAlarm.getId(), Toast.LENGTH_SHORT).show();
            Intent intent = AlarmPagerActivity.newIntent(sApplicationContext, mAlarm.getId());
            startActivity(intent);
        }
    }

    private class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder>{
        private List<Alarm> mAlarms;

        //Constructor
        public AlarmAdapter(List<Alarm> alarms){
            setAlarms(alarms);
        }
        //setters getters
        public void setAlarms(List<Alarm> alarms){
            mAlarms = alarms;
        }
        /**
         * Called by RecyclerView when its need a new View to display an item.
         * You create a View and wrap it in a ViewHolder.
         */
        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.list_item_alarm, parent, false);
            return new AlarmHolder(itemView);
        }

        /**
         * This method will bind a ViewHolder's View to your model object
         * It receives ViewHolder and a position in your dataset.
         */
        @Override
        public void onBindViewHolder(AlarmHolder alarmHolder, int position) {
            Alarm alarm = mAlarms.get(position);
            alarmHolder.bindAlarm(alarm);
        }

        @Override
        public int getItemCount() {
            return mAlarms.size();
        }
    }

    /** Create AlarmAdapter and set in on the recycler view*/
    private void UpdateContentUI() {
        Log.d(TAG, "UpdateContentUI called");
        //TODO: choose only the alarms for current user
        SharedPreferences sp = sApplicationContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentUserUUIDString = sp.getString(PREF_CURRENT_USER_UUID, null);
        List<Alarm> alarms = myLab.getAlarms();
        if(mAlarmAdapter == null){
            mAlarmAdapter = new AlarmAdapter(alarms);
            mAlarmRecyclerView.setAdapter(mAlarmAdapter);
        }else {
            mAlarmAdapter.setAlarms(alarms);
            mAlarmAdapter.notifyDataSetChanged();
        }

        mEmptyElementTextView.setVisibility(alarms.size() < 1 ? View.VISIBLE : View.GONE);
    }

}

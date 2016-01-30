package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
                                Toast.makeText(MainActivity.this, "Create alarm clicked!", Toast.LENGTH_LONG).show();
                                intent = new Intent(MainActivity.this, CreateAlarmActivity.class);
                                //intent = new Intent(MainActivity.this, CreateAlarmActivity.class);
                            } else if (drawerItem.getIdentifier() == IDENTIFIER_ITEM_MEDICATIONS) {
                                Toast.makeText(MainActivity.this, "Medications clicked!", Toast.LENGTH_LONG).show();
                                //intent = new Intent(MainActivity.this, CreateAlarmActivity.class);
                            } else if (drawerItem.getIdentifier() == IDENTIFIER_STICKY_SETTINGS) {
                                Toast.makeText(MainActivity.this, "Setting clicked!", Toast.LENGTH_LONG).show();
                                //intent = new Intent(MainActivity.this, CreateAlarmActivity.class);
                            } else if (drawerItem.getIdentifier() == IDENTIFIER_STICKY_HELP) {
                                Toast.makeText(MainActivity.this, "Help clicked!", Toast.LENGTH_LONG).show();
                                //intent = new Intent(MainActivity.this, CreateAlarmActivity.class);
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
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == IDENTIFIER_HEADER_ADD_ACCOUNT) {
                            Toast.makeText(sApplicationContext, "add account clicked.", Toast.LENGTH_LONG).show();
                            //TODO: Open add account activity
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
                            Toast.makeText(sApplicationContext, "manage account clicked.", Toast.LENGTH_LONG).show();
                            //TODO: Open manage account activity
                            //TODO: Save changes
                            //TODO: update the UI
                        }else if(profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == IDENTIFIER_USER) {
                            Toast.makeText(sApplicationContext, "The User", Toast.LENGTH_SHORT).show();
                            SharedPreferences sp = sApplicationContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                            String currentUserUUIDString = sp.getString(PREF_CURRENT_USER_UUID, null);
                            if(((UserProfile)profile).getId().toString().equalsIgnoreCase(currentUserUUIDString)){
                                Toast.makeText(sApplicationContext, "SAME User", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(sApplicationContext, "Different User", Toast.LENGTH_SHORT).show();
                            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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


}

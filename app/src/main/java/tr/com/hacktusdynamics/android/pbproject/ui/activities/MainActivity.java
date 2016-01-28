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

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.MyApplication;
import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.UserProfile;

import static tr.com.hacktusdynamics.android.pbproject.MyApplication.sApplicationContext;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IDENTIFIER_HEADER_ADD_ACCOUNT = 1;
    private static final int IDENTIFIER_HEADER_MANAGE_ACCOUNT = 2;

    //main items
    private static final int IDENTIFIER_ITEM_HOME= 10;
    private static final int IDENTIFIER_ITEM_CREATE_ALARM = 11;
    private static final int IDENTIFIER_ITEM_MEDICATIONS= 12;


    //sticky items
    private static final int IDENTIFIER_STICKY_SETTINGS = 20;
    private static final int IDENTIFIER_STICKY_HELP = 21;

    //save our drawer or header
    private AccountHeader accountHeader = null;
    private Drawer navigationDrawer = null;

    //private IProfile profileGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

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
                        getUserProfilesAndItems() //gets guest profile and other drawer items
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1, add a new profile
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
                        }

                        //false if you have not consumed the event And it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    private ArrayList<IProfile> getUserProfilesAndItems() {
        ArrayList<IProfile> p = new ArrayList<>();

        //create profile guest
        IProfile profileGuest = getGuestProfile();
        p.add(profileGuest);
        //TODO: when finish the Add Account activity, get other profiles and add to the list

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

        return p;
    }

    /**Creates guestProfile with SharedPreferences guestUUID*/
    private IProfile getGuestProfile() {
        SharedPreferences sp = MyApplication.sApplicationContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String guestUUID = sp.getString(Constants.PREF_GUEST_UUID, null);
        IProfile p = new UserProfile(guestUUID,
                getString(R.string.profile_guest_name),
                getString(R.string.profile_guest_email),
                getString(R.string.profile_guest_password)
                );
        p.withIcon(getResources().getDrawable(R.drawable.guest_avatar));
        return p;
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

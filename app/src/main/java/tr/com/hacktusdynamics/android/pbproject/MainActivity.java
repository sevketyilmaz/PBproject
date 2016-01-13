package tr.com.hacktusdynamics.android.pbproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IDENTIFIER_ADD_ACCOUNT = 1;
    private static final int IDENTIFIER_MANAGE_ACCOUNT = 2;

    //save our drawer or header
    private AccountHeader accountHeader = null;
    private Drawer navigationDrawer = null;

    private IProfile profileGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        //create profile guest
        profileGuest = new ProfileDrawerItem()
                .withName(getString(R.string.profile_guest_name))
                .withEmail(getString(R.string.profile_guest_email))
                .withIcon(getResources().getDrawable(R.drawable.guest_avatar));

        //create the account header
        buildHeader(false, savedInstanceState);

        //create the drawer
        navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)

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
                .addProfiles(
                        profileGuest,

                        //
                        new ProfileSettingDrawerItem()
                                .withName(getString(R.string.add_account))
                                .withDescription(getString(R.string.add_account_description))
                                .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus).actionBar().paddingDp(5).colorRes(R.color.material_drawer_dark_primary_text))
                                .withIdentifier(IDENTIFIER_ADD_ACCOUNT),
                        new ProfileSettingDrawerItem()
                                .withName(getString(R.string.manage_account))
                                .withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withIdentifier(IDENTIFIER_MANAGE_ACCOUNT)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1, add a new profile
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == IDENTIFIER_ADD_ACCOUNT) {
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
                        } else if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == IDENTIFIER_MANAGE_ACCOUNT) {
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

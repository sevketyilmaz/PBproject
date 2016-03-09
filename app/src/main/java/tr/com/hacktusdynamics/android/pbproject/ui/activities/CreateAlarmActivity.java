package tr.com.hacktusdynamics.android.pbproject.ui.activities;

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

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.form.BoxForm;
import tr.com.hacktusdynamics.android.pbproject.ui.fragments.PlaceHolderFragment;
import tr.com.hacktusdynamics.android.pbproject.utils.AlarmTimeComparator;

public class CreateAlarmActivity extends AppCompatActivity {
    private static final String TAG = CreateAlarmActivity.class.getSimpleName();

    private List<BoxForm> mAlarms;

    public List<BoxForm> getAlarms() {
        return mAlarms;
    }

    public String showAlarms() {
        if(mAlarms.size() != 0) {
            Collections.sort(mAlarms, new AlarmTimeComparator());
            StringBuilder sb = new StringBuilder();
            for (BoxForm bf : mAlarms) {
                sb.append("( " + bf.getBoxNumber() + " - " + bf.getAlarmTime() + " - " + bf.getUserProfileId() + " )");
            }
            return sb.toString();
        }else{
            return "";
        }
    }

    public void addOrUpdateAlarm(int boxNumber, long dateTime, String userProfileId) {
        boolean inList = false;
        BoxForm inListBoxForm = null;
        for(BoxForm boxForm : mAlarms){
            if(boxForm.getBoxNumber() == (boxNumber-1)) {
                inList = true;
                inListBoxForm = boxForm;
            }
        }
        if(inList) {
            inListBoxForm.setAlarmTime(new Date(dateTime));
        }else {
            BoxForm bf = new BoxForm((boxNumber - 1), new Date(dateTime), userProfileId);
            mAlarms.add(bf);
        }
    }
    public void removeAlarm(int boxNumber){
        for(BoxForm bf : mAlarms){
            if(bf.getBoxNumber() == boxNumber)
                mAlarms.remove(bf);
        }
    }
    public void disableAlarm(int boxNumber){
        for(BoxForm bf : mAlarms){
            if(bf.getBoxNumber() == boxNumber)
                bf.setIsActive(false);
        }
    }
    public void enableAlarm(int boxNumber){
        for(BoxForm bf : mAlarms){
            if(bf.getBoxNumber() == boxNumber)
                bf.setIsActive(true);
        }
    }

    public long getAlarmDateTime(int boxNumber) {
        long dt = 0L;
        for(BoxForm boxForm : mAlarms){
            if(boxForm.getBoxNumber() == boxNumber -1)
                dt = boxForm.getAlarmTime().getTime();
        }
        return dt;
    }
    public BoxForm getAlarm(int boxNumber){
        BoxForm bf = null;
        for(BoxForm boxForm : mAlarms){
            if(boxForm.getBoxNumber() == (boxNumber -1))
                bf = boxForm;
        }
        return bf;
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
            }
        });

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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

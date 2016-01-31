package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.MyApplication;
import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.MyLab;
import tr.com.hacktusdynamics.android.pbproject.models.UserProfile;

public class AddAccountActivity extends AppCompatActivity {
    private static final String TAG = AddAccountActivity.class.getSimpleName();

    private static final int REQUEST_CONTACT = 1;

    private AutoCompleteTextView nameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText phoneView;
    private Button contactButton;

    private SharedPreferences sp;
    private MyLab myLab = null;
    private String currentUUID;
    private UserProfile userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);

        myLab = MyLab.get(MyApplication.sApplicationContext);

        /** Initialize the UI*/
        nameView = (AutoCompleteTextView) findViewById(R.id.add_account_name);
        emailView = (AutoCompleteTextView) findViewById(R.id.add_account_email);
        passwordView = (EditText) findViewById(R.id.add_account_password);
        phoneView = (EditText) findViewById(R.id.add_account_dependent_phone);
        contactButton = (Button) findViewById(R.id.add_account_contact_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            userProfile = new UserProfile(
                    UUID.randomUUID().toString(),
                    nameView.getText().toString(),
                    emailView.getText().toString(),
                    passwordView.getText().toString()
            );
            userProfile.setDependentPhone(phoneView.getText().toString());
            myLab.addUserProfile(userProfile);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}

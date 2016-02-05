package tr.com.hacktusdynamics.android.pbproject.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.MyLab;
import tr.com.hacktusdynamics.android.pbproject.models.UserProfile;

import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_NAME;
import static tr.com.hacktusdynamics.android.pbproject.MyApplication.sApplicationContext;

public class ManageAccountActivity extends AppCompatActivity {
    private static final String TAG = ManageAccountActivity.class.getSimpleName();

    private static final int REQUEST_CONTACT = 1;

    private AutoCompleteTextView nameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText phoneView;
    private Button contactButton;

    private SharedPreferences sp;
    private MyLab myLab = null;
    private String guestUUID;
    private String currentUUID;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);

        /** Getting current UserProfile for UI */
        sp = sApplicationContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        guestUUID = sp.getString(Constants.PREF_GUEST_UUID, null);
        currentUUID = sp.getString(Constants.PREF_CURRENT_USER_UUID, null);
        myLab = MyLab.get(sApplicationContext);
        userProfile = myLab.getUserProfile(UUID.fromString(currentUUID));

        /** Initialize the UI*/
        nameView = (AutoCompleteTextView) findViewById(R.id.manage_account_name);
        nameView.setText(userProfile.getName().toString());

        emailView = (AutoCompleteTextView) findViewById(R.id.manage_account_email);
        emailView.setText(userProfile.getEmail().toString());

        passwordView = (EditText) findViewById(R.id.manage_account_password);
        passwordView.setText(userProfile.getPassword());

        phoneView = (EditText) findViewById(R.id.manage_account_dependent_phone);
        phoneView.setText(userProfile.getDependentPhone());

        final Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        contactButton = (Button) findViewById(R.id.manage_account_contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT);
            }
        });
        //Handling the crash if there is no contact application on device
        PackageManager packageManager = getPackageManager();
        if(packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY) == null)
            contactButton.setEnabled(false);

    }

    public void imageClick(View view){
        //TODO: let user change the icon on the userProfile
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;
        String contactID = null;
        String contactPhone = null;
        String[] queryFields;

        if(requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();
            queryFields = new String[]{ContactsContract.Contacts._ID};
            //perform query - the concatUri is like a where clause here
            Cursor cursorID = getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (cursorID.getCount() == 0)
                    return;
                cursorID.moveToFirst();
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            } finally {
                cursorID.close();
            }

            Cursor cursorPhone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    new String[]{contactID},
                    null);
            try {
                if(cursorPhone.getCount() == 0)
                    return;
                cursorPhone.moveToFirst();
                contactPhone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //set the PhoneView with the phone number
                phoneView.setText(contactPhone);
            }finally {
                cursorPhone.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_account, menu);
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
                    currentUUID,
                    nameView.getText().toString(),
                    emailView.getText().toString(),
                    passwordView.getText().toString()
            );
            userProfile.setDependentPhone(phoneView.getText().toString());
            myLab.updateUserProfile(userProfile);
            finish();
        }

        if(id == R.id.action_delete){
            if(guestUUID.equalsIgnoreCase(currentUUID)) {
                Toast.makeText(this, R.string.cannot_delete_guest, Toast.LENGTH_SHORT).show();
            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(getString(R.string.delete_message));

                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myLab.deleteUserProfile(currentUUID);
                        finish();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

}

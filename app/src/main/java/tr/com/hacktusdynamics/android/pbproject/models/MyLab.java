package tr.com.hacktusdynamics.android.pbproject.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.database.PillBoxBaseHelper;
import tr.com.hacktusdynamics.android.pbproject.database.PillBoxDbSchema.UserProfileTable;
import tr.com.hacktusdynamics.android.pbproject.database.UserProfileCursorWrapper;

import static tr.com.hacktusdynamics.android.pbproject.Constants.IDENTIFIER_USER;
import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_CURRENT_USER_UUID;
import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_NAME;
import static tr.com.hacktusdynamics.android.pbproject.MyApplication.sApplicationContext;

/**
 * Singleton class that holds all model class instances
 * method get(Context c) returns the MyLab singleton instance
 */
public class MyLab {
    private static final String TAG = MyLab.class.getSimpleName();

    private static MyLab sMyLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //Constructor private
    private MyLab(Context context){
        mContext = context;
        //its call the onCreate method on PillBoxBaseHelper class to create tables, if is not exist
        mDatabase = new PillBoxBaseHelper(mContext).getWritableDatabase();

        //create10DummyBoxes(); //seed data
    }

    /** Singleton method to create MyLab instance */
    public static MyLab get(Context context){
        if(sMyLab == null)
            sMyLab = new MyLab(context);

        return sMyLab;
    }

    //setters getters
    /**Box portion of getters setters*/
    public List<Box> getBoxes(){
        return null;
    }
    public Box getBox(UUID id){
        return null;
    }
    public void addBox(Box box){

    }
    public void updateBox(Box box){

    }

    /** UserProfile portion of getters setters*/
    public List<IProfile> getUserProfiles(){
        List<IProfile> profiles = new ArrayList<>();
        UserProfileCursorWrapper cursorWrapper = queryUserProfiles(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                IProfile p = cursorWrapper.getUserProfile();
                p.withIcon(sApplicationContext.getResources().getDrawable(R.drawable.guest_avatar));
                p.withIdentifier(IDENTIFIER_USER);
                profiles.add(p);
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        return profiles;
    }

    public UserProfile getUserProfile(UUID id){
        UserProfileCursorWrapper cursorWrapper = queryUserProfiles(
                UserProfileTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursorWrapper.getCount() == 0)
                return null;
            cursorWrapper.moveToFirst();
            UserProfile p =(UserProfile)cursorWrapper.getUserProfile();
            p.withIcon(sApplicationContext.getResources().getDrawable(R.drawable.guest_avatar));
            p.withIdentifier(IDENTIFIER_USER);
            return p;
        }finally {
            cursorWrapper.close();
        }
    }
    public void addUserProfile(UserProfile userProfile){
        ContentValues contentValues = getUserProfileContentValues(userProfile);
        mDatabase.insert(UserProfileTable.NAME, null, contentValues);
    }
    public void updateUserProfile(UserProfile userProfile){
        String uuidString = userProfile.getId().toString();
        ContentValues contentValues = getUserProfileContentValues(userProfile);
        mDatabase.update(UserProfileTable.NAME, contentValues,
                UserProfileTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private static ContentValues getUserProfileContentValues(UserProfile userProfile){
        ContentValues values = new ContentValues();
        values.put(UserProfileTable.Cols.UUID, userProfile.getId().toString());
        values.put(UserProfileTable.Cols.NAME, userProfile.getName().toString());
        values.put(UserProfileTable.Cols.EMAIL, userProfile.getEmail().toString());
        values.put(UserProfileTable.Cols.PASSWORD, userProfile.getPassword());
        values.put(UserProfileTable.Cols.DEPENDENT_PHONE, userProfile.getDependentPhone());
        return values;
    }

    private UserProfileCursorWrapper queryUserProfiles(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                UserProfileTable.NAME,
                null, //null select all columns
                whereClause,
                whereArgs,
                null, //group by
                null, //having
                null //order by
        );
        return new UserProfileCursorWrapper(cursor);
    }

    private void create10DummyBoxes(){
        SharedPreferences sp = sApplicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String guestProfile = sp.getString(PREF_CURRENT_USER_UUID, null);
        Box box;
        Date d;
        for(int i = 0; i < 10; i++){
            d = new Date((System.currentTimeMillis() + (i * 1000 * 60 * 60)));
            box = new Box(i, d, guestProfile);
            box.setBoxState(Box.BoxStates.FULL_CLOSE);
            addBox(box);
        }
    }

}

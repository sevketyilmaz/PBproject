package tr.com.hacktusdynamics.android.pbproject.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.database.BoxCursorWrapper;
import tr.com.hacktusdynamics.android.pbproject.database.PillBoxBaseHelper;
import tr.com.hacktusdynamics.android.pbproject.database.PillBoxDbSchema.BoxTable;
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
        List<Box> boxes = new ArrayList<>();
        BoxCursorWrapper cursorWrapper = queryBox(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                Box box = cursorWrapper.getBox();
                boxes.add(box);
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        return boxes;
    }

    public Box getBox(UUID id){
        BoxCursorWrapper cursorWrapper = queryBox(
                BoxTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursorWrapper.getCount() == 0)
                return null;
            cursorWrapper.moveToFirst();
            Box box = cursorWrapper.getBox();
            return box;
        }finally {
            cursorWrapper.close();
        }
    }
    public void addBox(Box box){
        ContentValues contentValues = getBoxContentValues(box);
        mDatabase.insert(BoxTable.NAME, null, contentValues);
    }
    public void updateBox(Box box){
        String uuidString = box.getId().toString();
        ContentValues contentValues = getBoxContentValues(box);
        mDatabase.update(BoxTable.NAME, contentValues,
                BoxTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }
    public void deleteBox(String boxUUID){
        mDatabase.delete(UserProfileTable.NAME,
                BoxTable.Cols.UUID + " = ?",
                new String[]{boxUUID});
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

    public void deleteUserProfile(String uuidString){
        mDatabase.delete(UserProfileTable.NAME,
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

    private static ContentValues getBoxContentValues(Box box){
        ContentValues values = new ContentValues();
        values.put(BoxTable.Cols.UUID, box.getId().toString());
        values.put(BoxTable.Cols.BOX_NUMBER, box.getBoxNumber());
        values.put(BoxTable.Cols.ALARM_DATE, box.getAlarmTime().getTime());
        values.put(BoxTable.Cols.CREATED_DATE, box.getCreatedTime().getTime());
        values.put(BoxTable.Cols.BOX_STATE, box.getBoxStateInt());
        values.put(BoxTable.Cols.USER_PROFILE_ID, box.getUserProfileId());
        return values;
    }

    private BoxCursorWrapper queryBox(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                BoxTable.NAME,
                null, //null select all columns
                whereClause,
                whereArgs,
                null, //group by
                null, //having
                null //order by
        );
        return new BoxCursorWrapper(cursor);
    }

    private void create10DummyBoxes(){
        SharedPreferences sp = sApplicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userProfile = sp.getString(PREF_CURRENT_USER_UUID, null);
        Log.d(TAG, "CurrentUserProfile :" + userProfile);
        Box box;
        Date d;
        Date createdDate = new Date(System.currentTimeMillis());
        for(int i = 0; i < 10; i++){
            d = new Date((System.currentTimeMillis() + (i * 1000 * 60 * 60)));
            box = new Box(null, i, d, createdDate, -1, userProfile);
            box.setBoxState(Box.BoxStates.FULL_CLOSE);
            addBox(box);
        }
    }

}

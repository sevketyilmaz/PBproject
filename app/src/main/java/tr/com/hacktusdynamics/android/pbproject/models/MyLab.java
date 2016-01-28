package tr.com.hacktusdynamics.android.pbproject.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.MyApplication;
import tr.com.hacktusdynamics.android.pbproject.database.PillBoxBaseHelper;
import tr.com.hacktusdynamics.android.pbproject.database.PillBoxDbSchema.UserProfileTable;

/**
 * Singleton class that holds all model class instances
 * method get(Context c) returns the MyLab singleton instance
 */
public class MyLab {
    private static final String TAG = MyLab.class.getSimpleName();

    private static MyLab sMyLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private List<Box> mBoxes;//move to db

    //Constructor private
    private MyLab(Context context){
        mContext = context;
        //its call the onCreate method on PillBoxBaseHelper class to create tables, if is not exist
        mDatabase = new PillBoxBaseHelper(mContext).getWritableDatabase();

        mBoxes = new ArrayList<>();//move to db

        create10DummyBoxes(); //seed data
    }

    /** Singleton method to create MyLab instance */
    public static MyLab get(Context context){
        if(sMyLab == null)
            sMyLab = new MyLab(context);

        return sMyLab;
    }

    //setters getters
    public List<Box> getBoxes(){
        return mBoxes;//move to db
    }

    public Box getBox(UUID id){//read from db
        for(Box box : mBoxes){
            if(box.getId().equals(id)) return box;
        }
        return null;
    }

    public void addBox(Box box){//move to db
        mBoxes.add(box);
    }

    /** UserProfile portion of getters setters*/
    public List<UserProfile> getUserProfiles(){
        return new ArrayList<>();
    }
    public UserProfile getUserProfile(UUID id){
        return null;
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
        return values;
    }

    private void create10DummyBoxes(){
        SharedPreferences sp = MyApplication.sApplicationContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String guestProfile = sp.getString(Constants.PREF_GUEST_UUID, null);
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

package tr.com.hacktusdynamics.android.pbproject.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.MyApplication;

/**
 * Singleton class that holds all model class instances
 * method get(Context c) returns the MyLab singleton instance
 */
public class MyLab {
    private static final String TAG = MyLab.class.getSimpleName();

    private static MyLab sMyLab;
    private Context mContext;

    private List<Box> mBoxes;

    //Constructor private
    private MyLab(Context context){
        mContext = context;
        mBoxes = new ArrayList<>();

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
        return mBoxes;
    }

    public Box getBox(UUID id){
        for(Box box : mBoxes){
            if(box.getId().equals(id)) return box;
        }
        return null;
    }

    public void addBox(Box box){
        mBoxes.add(box);
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

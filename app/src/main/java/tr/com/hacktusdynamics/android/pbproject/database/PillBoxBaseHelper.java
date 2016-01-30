package tr.com.hacktusdynamics.android.pbproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static tr.com.hacktusdynamics.android.pbproject.database.PillBoxDbSchema.*;

public class PillBoxBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "pillboxBase.db";

    public PillBoxBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create userprofiles table
        db.execSQL("create table " + UserProfileTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        UserProfileTable.Cols.UUID + ", " +
                        UserProfileTable.Cols.NAME + ", " +
                        UserProfileTable.Cols.EMAIL + ", " +
                        UserProfileTable.Cols.PASSWORD + ", " +
                        UserProfileTable.Cols.DEPENDENT_PHONE + ")"
        );

        //Create box table
        db.execSQL("create table " + BoxTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        BoxTable.Cols.UUID + ", " +
                        BoxTable.Cols.BOX_NUMBER + ", " +
                        BoxTable.Cols.ALARM_DATE + ", " +
                        BoxTable.Cols.CREATED_DATE + ", " +
                        BoxTable.Cols.BOX_STATE + ", " +
                        BoxTable.Cols.USER_PROFILE_ID + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

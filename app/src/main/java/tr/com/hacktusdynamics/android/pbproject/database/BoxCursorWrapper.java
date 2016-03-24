package tr.com.hacktusdynamics.android.pbproject.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

import tr.com.hacktusdynamics.android.pbproject.models.Box;

public class BoxCursorWrapper extends CursorWrapper {
    public BoxCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Box getBox(){
        String uuidString = getString(getColumnIndex(PillBoxDbSchema.BoxTable.Cols.UUID));
        int boxNumber = getInt(getColumnIndex(PillBoxDbSchema.BoxTable.Cols.BOX_NUMBER));
        long alarmDate = getLong(getColumnIndex(PillBoxDbSchema.BoxTable.Cols.ALARM_DATE));
        long createdDate = getLong(getColumnIndex(PillBoxDbSchema.BoxTable.Cols.CREATED_DATE));
        int boxState = getInt(getColumnIndex(PillBoxDbSchema.BoxTable.Cols.BOX_STATE));
        String userProfileId = getString(getColumnIndex(PillBoxDbSchema.BoxTable.Cols.USER_PROFILE_ID));
        int foreignKeyId = getInt(getColumnIndex(PillBoxDbSchema.BoxTable.Cols.FOREIGN_KEY_ID));

        Box box = new Box(uuidString, boxNumber, new Date(alarmDate), new Date(createdDate), boxState, userProfileId, foreignKeyId);

        return box;
    }
}

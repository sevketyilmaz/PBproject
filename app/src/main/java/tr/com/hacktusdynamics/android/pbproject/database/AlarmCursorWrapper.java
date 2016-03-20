package tr.com.hacktusdynamics.android.pbproject.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

import tr.com.hacktusdynamics.android.pbproject.models.Alarm;

public class AlarmCursorWrapper extends CursorWrapper{
    public AlarmCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Alarm getAlarm(){
        int id = getInt(getColumnIndex(PillBoxDbSchema.AlarmTable.Cols.KEY_ID));
        long createdDate = getLong(getColumnIndex(PillBoxDbSchema.AlarmTable.Cols.CREATED_DATE));
        String userProfileId = getString(getColumnIndex(PillBoxDbSchema.AlarmTable.Cols.USER_PROFILE_ID));

        Alarm alarm = new Alarm(id, new Date(createdDate), userProfileId);

        return alarm;
    }
}

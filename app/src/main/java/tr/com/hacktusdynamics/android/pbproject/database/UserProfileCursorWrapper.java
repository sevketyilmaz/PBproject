package tr.com.hacktusdynamics.android.pbproject.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import tr.com.hacktusdynamics.android.pbproject.database.PillBoxDbSchema.UserProfileTable;
import tr.com.hacktusdynamics.android.pbproject.models.UserProfile;

public class UserProfileCursorWrapper extends CursorWrapper {

    public UserProfileCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public IProfile getUserProfile(){
        String uuidString = getString(getColumnIndex(UserProfileTable.Cols.UUID));
        String name = getString(getColumnIndex(UserProfileTable.Cols.NAME));
        String email = getString(getColumnIndex(UserProfileTable.Cols.EMAIL));
        String password = getString(getColumnIndex(UserProfileTable.Cols.PASSWORD));
        String dependentPhone = getString(getColumnIndex(UserProfileTable.Cols.DEPENDENT_PHONE));

        IProfile profile = new UserProfile(uuidString, name, email, password);
        ((UserProfile)profile).setDependentPhone(dependentPhone);
        return profile;
    }
}

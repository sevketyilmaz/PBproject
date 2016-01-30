package tr.com.hacktusdynamics.android.pbproject.models;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.util.UUID;

/**
 * UserProfile extends from ProfileDrawerItem
 *  */
public class UserProfile extends ProfileDrawerItem {
    private static final String TAG = UserProfile.class.getSimpleName();

    private UUID mId;
    private String mPassword;
    private String mDependentPhone;

    //constructors
    /** Constructor
     * @param uuid if null, creates random UUID
     */
    public UserProfile(String uuid){
        if(uuid == null)
            mId = UUID.randomUUID();
        mId = UUID.fromString(uuid);
    }
    public UserProfile(String uuid, String name, String email, String password){
        this(uuid);
        withName(name);
        withEmail(email);
        setPassword(password);
    }

    //setters getters
    public UUID getId() {
        return mId;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getDependentPhone() {
        return mDependentPhone;
    }

    public void setDependentPhone(String dependentPhone) {
        mDependentPhone = dependentPhone;
    }
}

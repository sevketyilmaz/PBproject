package tr.com.hacktusdynamics.android.pbproject.models.form;

import java.util.Date;

import tr.com.hacktusdynamics.android.pbproject.models.Box;

public class BoxForm extends Box {
    private static final String TAG = BoxForm.class.getSimpleName();

    /** BoxForm created with initial active state */
    private boolean isActive = true;

    //Constructors
    public BoxForm(int boxNumber, Date alarmDateTime, String userProfileId) {
        super(boxNumber, alarmDateTime, userProfileId);
    }
    public BoxForm(String uuid, int boxNumber, Date alarmDateTime, Date createdTime, int boxS, String userProfileId) {
        super(uuid, boxNumber, alarmDateTime, createdTime, boxS, userProfileId);
    }

    //getters setters

    public boolean isActive() {
        return isActive;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

}

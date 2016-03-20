package tr.com.hacktusdynamics.android.pbproject.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Asus on 20.3.2016.
 */
public class Alarm implements Serializable {
    private static final String TAG = Alarm.class.getSimpleName();

    private Date createdTime; // Alarm set Created time
    private String userProfileId; //user that created this alarm set

    //constructors
    public Alarm(Date createdTime, String userProfileId){
        setCreatedTime(createdTime);
        setUserProfileId(userProfileId);
    }

    //getters setters
    public Date getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        if(createdTime == null){
            this.createdTime = new Date();
        }else{
            this.createdTime = new Date(createdTime.getTime());
        }
    }

    public String getUserProfileId() {
        return userProfileId;
    }
    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }
}

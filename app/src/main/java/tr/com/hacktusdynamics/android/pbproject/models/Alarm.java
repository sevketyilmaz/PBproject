package tr.com.hacktusdynamics.android.pbproject.models;

import java.io.Serializable;
import java.util.Date;

public class Alarm implements Serializable {
    private static final String TAG = Alarm.class.getSimpleName();

    private int id;
    private Date createdTime; // Alarm set Created time
    private String userProfileId; //user that created this alarm set

    //constructors
    public Alarm(Date createdTime, String userProfileId){
        setCreatedTime(createdTime);
        setUserProfileId(userProfileId);
    }
    public Alarm(int id, Date createdTime, String userProfileId){
        setId(id);
        setCreatedTime(createdTime);
        setUserProfileId(userProfileId);
    }

    //getters setters
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

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

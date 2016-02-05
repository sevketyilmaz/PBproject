package tr.com.hacktusdynamics.android.pbproject.models;


import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Box implements Serializable, Comparable<Box> {
    private static final String TAG = Box.class.getSimpleName();
    private final int MAX_BOX_SIZE = 28;

    private UUID mId;
    private int boxNumber;
    private Date alarmDateTime;
    private Date createdTime;
    private BoxStates boxState;
    private String userProfileId; // user that created this box

    public static enum BoxStates{
        EMPTY_CLOSE("EMPTY_CLOSE"),
        EMPTY_OPEN("EMPTY OPEN"),
        FULL_CLOSE("FULL CLOSE"),
        FULL_OPEN("FULL OPEN");

        private final String tag;

        BoxStates(String tag){this.tag = tag;}

        @Override
        public String toString() {
            return tag;
        }
    }

    //Constructors
    public Box(int boxNumber, Date alarmDateTime, String userProfileId){
        this(null, boxNumber, alarmDateTime, null, -1, userProfileId);
    }

    /**
     * @param uuid null for random UUID
     * @param boxNumber box number for creation
     * @param alarmDateTime null for current time
     * @param createdTime null for current time
     * @param boxS integer box state, if less then zero set EMPTY_CLOSE
     * @param userProfileId which user created the box
     *
     * @return Returns the box object
     */
    public Box(String uuid, int boxNumber, Date alarmDateTime, Date createdTime, int boxS, String userProfileId){
        setId(uuid); //null for random UUID
        setBoxNumber(boxNumber);
        setAlarmTime(alarmDateTime); //null for current datetime
        setCreatedTime(createdTime); //null for current datetime
        setBoxState(getBoxStateFromInt(boxS)); //any value other than 0,1,2,3 is EMPTY_CLOSE
        setUserProfileId(userProfileId);
    }

    //setters getters
    public UUID getId(){
        return mId;
    }
    public void setId(String uuid){
        if(uuid == null)
            this.mId = UUID.randomUUID();
        else
            this.mId = UUID.fromString(uuid);
    }

    public Date getCreatedTime(){
        return createdTime;
    }

    public int getBoxNumber() {
        return boxNumber;
    }
    public void setCreatedTime(Date createdTime){
        if(createdTime == null){
            this.createdTime = new Date();
        }else{
            this.createdTime = new Date(createdTime.getTime());
        }
    }

    public void setBoxNumber(int boxNumber) {
        if(boxNumber >= 0 && boxNumber < MAX_BOX_SIZE) //between 0 and MAX_BOX_SIZE
            this.boxNumber = boxNumber;
        else
            this.boxNumber = -1;
    }

    public Date getAlarmTime() {
        return alarmDateTime;
    }
    public void setAlarmTime(Date newDateTime) {
        if(newDateTime == null)
            this.alarmDateTime = new Date();
        else
            this.alarmDateTime = new Date(newDateTime.getTime());
    }

    public String getUserProfileId(){ return userProfileId; }
    public void setUserProfileId(String userProfileId){
        this.userProfileId = userProfileId;
    }

    public BoxStates getBoxState() {
        return boxState;
    }
    public void setBoxState(BoxStates boxState) {
        this.boxState = boxState;
    }
    public int getBoxStateInt(){
        int bState = 0; //initially EMPTY_CLOSE
        switch (boxState){
            case EMPTY_CLOSE:
                bState = 0;
                break;
            case EMPTY_OPEN:
                bState = 1;
                break;
            case FULL_CLOSE:
                bState = 2;
                break;
            case FULL_OPEN:
                bState = 3;
                break;
        }
        return bState;
    }

    public BoxStates getBoxStateFromInt(int boxS){
        BoxStates state = BoxStates.EMPTY_CLOSE;
        switch (boxS){
            case 0:
                state = BoxStates.EMPTY_CLOSE;
                break;
            case 1:
                state = BoxStates.EMPTY_OPEN;
                break;
            case 2:
                state = BoxStates.FULL_CLOSE;
                break;
            case 3:
                state = BoxStates.FULL_OPEN;
                break;
            default: //boxS = -1
                break;
        }
        return state;
    }

    @Override
    public int compareTo(Box otherBox) {
        if(getAlarmTime() == null || otherBox.getAlarmTime() == null)
            return 0;

        return getAlarmTime().compareTo(otherBox.getAlarmTime());
    }

}
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
    private int foreignKeyId; //Alarm class id

    public static enum BoxStates{
        CLOSE_EMPTY("CLOSE_EMPTY"),
        CLOSE_FULL("CLOSE FULL"),
        CLOSE_DONE("CLOSE DONE"),
        OPEN_FULL("OPEN FULL");

        private final String tag;

        BoxStates(String tag){this.tag = tag;}

        @Override
        public String toString() {
            return tag;
        }
    }

    //Constructors
    public Box(int boxNumber, Date alarmDateTime, String userProfileId){
        this(null, boxNumber, alarmDateTime, null, -1, userProfileId, -1);
    }
    public Box(int boxNumber, Date alarmDateTime, String userProfileId, int foreignKeyId){
        this(null, boxNumber, alarmDateTime, null, -1, userProfileId, foreignKeyId);
    }

    /**
     * @param uuid null for random UUID
     * @param boxNumber box number for creation
     * @param alarmDateTime null for current time
     * @param createdTime null for current time
     * @param boxS integer box state, if less then zero set CLOSE_EMPTY
     * @param userProfileId which user created the box
     * @param foreignKeyId Alarm class primary key
     *
     * @return Returns the box object
     */
    public Box(String uuid, int boxNumber, Date alarmDateTime, Date createdTime, int boxS, String userProfileId, int foreignKeyId){
        setId(uuid); //null for random UUID
        setBoxNumber(boxNumber);
        setAlarmTime(alarmDateTime); //null for current datetime
        setCreatedTime(createdTime); //null for current datetime
        setBoxState(getBoxStateFromInt(boxS)); //any value other than 0,1,2,3 is CLOSE_EMPTY
        setUserProfileId(userProfileId);
        setForeignKeyId(foreignKeyId); // -1 default
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

    public int getForeignKeyId() { return foreignKeyId; }
    public void setForeignKeyId(int foreignKeyId) {
        this.foreignKeyId = foreignKeyId;
    }

    public BoxStates getBoxState() {
        return boxState;
    }
    public void setBoxState(BoxStates boxState) {
        this.boxState = boxState;
    }
    public int getBoxStateInt(){
        int bState = 0; //initially CLOSE_EMPTY
        switch (boxState){
            case CLOSE_EMPTY:
                bState = 0;
                break;
            case CLOSE_FULL:
                bState = 1;
                break;
            case CLOSE_DONE:
                bState = 2;
                break;
            case OPEN_FULL:
                bState = 3;
                break;
        }
        return bState;
    }

    public BoxStates getBoxStateFromInt(int boxS){
        BoxStates state = BoxStates.CLOSE_EMPTY;
        switch (boxS){
            case 0:
                state = BoxStates.CLOSE_EMPTY;
                break;
            case 1:
                state = BoxStates.CLOSE_FULL;
                break;
            case 2:
                state = BoxStates.CLOSE_DONE;
                break;
            case 3:
                state = BoxStates.OPEN_FULL;
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

    /** Return SudoJson string for box instance with alarmTime is long:
     * boxNumber:bN; boxState:bS; alarmTime:aT
     * JsonString: {"bN":1,"bS":0,"aT":1445258741847}
     */
    public String toBluetoothJson(){
        long alarmTime = getAlarmTime().getTime();
        int bState = 0; //all boxes initially CLOSE_EMPTY
        switch (getBoxState()) {
            case CLOSE_EMPTY:
                bState = 0;
                break;

            case CLOSE_FULL:
                bState = 1;
                break;

            case CLOSE_DONE:
                bState = 2;
                break;

            case OPEN_FULL:
                bState = 3;
                break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"bN\":");sb.append(getBoxNumber());sb.append(",");
        sb.append("\"bS\":");sb.append(bState);sb.append(",");
        sb.append("\"aT\":");sb.append((alarmTime/1000));
        sb.append("}");

        return sb.toString();
    }
}
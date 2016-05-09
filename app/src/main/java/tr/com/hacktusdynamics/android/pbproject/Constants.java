package tr.com.hacktusdynamics.android.pbproject;

public class Constants {
    /**
     * Shared Prefs and Keys for Values
     * */
    public static final String PREF_NAME = "PillBoxApp";
    public static final String PREF_GUEST_EXIST = "guestExist";
    public static final String PREF_GUEST_UUID = "guestUUID";
    public static final String PREF_CURRENT_USER_UUID = "currentUUID";


    /***/
    //header items
    public static final int IDENTIFIER_HEADER_ADD_ACCOUNT = 1;
    public static final int IDENTIFIER_HEADER_MANAGE_ACCOUNT = 2;
    public static final int IDENTIFIER_USER = 3;

    //main items
    public static final int IDENTIFIER_ITEM_HOME= 10;
    public static final int IDENTIFIER_ITEM_CREATE_ALARM = 11;
    public static final int IDENTIFIER_ITEM_MEDICATIONS= 12;


    //sticky items
    public static final int IDENTIFIER_STICKY_SETTINGS = 20;
    public static final int IDENTIFIER_STICKY_HELP = 21;

    /***/

    // ** Define constants for using MyBlueetothService and the UI **//
    // Message types sent from the MyBluetoothService
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // ** Define constants for using AlarmReceiver ** //
    public static String ACTION_ALARM_PILLBOX = "tr.com.hacktusdynamics.android.pbproject.action.alarm.pillbox";
    public static final String BOX_ID = "BOX_ID";
    public static final String DATE = "DATE";

}

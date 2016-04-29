package tr.com.hacktusdynamics.android.pbproject.utils;

import java.util.List;

import tr.com.hacktusdynamics.android.pbproject.models.Box;

public class BluetoothStringUtils {
    private static final String TAG = BluetoothStringUtils.class.getSimpleName();

    private static final String KEY_MESSGE = "\"mS\"";
    private static final String KEY_CURRENT_TIME = "\"cT\"";

    private static final int VALUE_SET_ALL_BOXES = 1;
    private static final int VALUE_SYNCH = 2;

    /**
     * */
    public static String setAllBoxesActionBluetoothString(List<Box>alarms){
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        sb.append("{");
        sb.append(KEY_MESSGE+":");sb.append(VALUE_SET_ALL_BOXES);sb.append(",");
        sb.append(KEY_CURRENT_TIME+":");sb.append(System.currentTimeMillis()/1000);
        sb.append("}");sb.append(",");

        sb.append(getBluetoothJsonForAlarmBoxes(alarms));

        sb.append("]");
        return sb.toString();
    }

    /**
     * Return Json array string for all boxes with long
     * Json array String:
     *  {"bN":0,"bS":0,"aT":1445587889660},
     *  {"bN":1,"bS":0,"aT":1445599889660}
     * */
    private static String getBluetoothJsonForAlarmBoxes(List<Box> alarms){
        StringBuilder sb = new StringBuilder();
        int i = 0;

        for(Box box : alarms){
            sb.append(box.toBluetoothJson());
            if(i < alarms.size() - 1)
                sb.append(",");
            i++;
        }

        return sb.toString();
    }

    public static String getAlarmsFromPillboxBluetoothString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        sb.append("{");
        sb.append(KEY_MESSGE+":");sb.append(VALUE_SYNCH);sb.append(",");
        sb.append(KEY_CURRENT_TIME+":");sb.append(System.currentTimeMillis()/1000);
        sb.append("}");

        sb.append("]");
        return sb.toString();
    }

}

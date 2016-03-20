package tr.com.hacktusdynamics.android.pbproject.utils;

import java.util.Comparator;

import tr.com.hacktusdynamics.android.pbproject.models.Box;

/**
 * AlarmTimeComparator used for sorting Box objects with alarmTime
 */
public class AlarmTimeComparator implements Comparator<Box> {

    @Override
    public int compare(Box box1, Box box2) {
        if(box1.getAlarmTime() == null || box2.getAlarmTime() == null)
            return 0;

        return box1.getAlarmTime().compareTo(box2.getAlarmTime());
    }
}

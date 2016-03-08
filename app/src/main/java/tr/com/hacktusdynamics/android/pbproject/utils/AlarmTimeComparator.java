package tr.com.hacktusdynamics.android.pbproject.utils;

import java.util.Comparator;

import tr.com.hacktusdynamics.android.pbproject.models.form.BoxForm;

/**
 * AlarmTimeComparator used for sorting BoxForm objects with alarmTime
 */
public class AlarmTimeComparator implements Comparator<BoxForm> {

    @Override
    public int compare(BoxForm boxForm1, BoxForm boxForm2) {
        if(boxForm1.getAlarmTime() == null || boxForm2.getAlarmTime() == null)
            return 0;

        return boxForm1.getAlarmTime().compareTo(boxForm2.getAlarmTime());
    }
}

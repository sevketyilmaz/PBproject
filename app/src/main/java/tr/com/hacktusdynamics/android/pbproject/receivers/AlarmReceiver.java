package tr.com.hacktusdynamics.android.pbproject.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    String title,description;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()...");
        String action  = intent.getAction();
        if(Constants.ACTION_ALARM_PILLBOX.equals(action)){
            Bundle bundle = intent.getExtras();
            int boxId = bundle.getInt(Constants.BOX_ID);
            String date = bundle.getString(Constants.DATE);

            generateNotification(context, boxId, date);
            //TODO: genrate SMS message
            // generateSmsMessage();
        }
    }

    private void generateNotification(Context context, int boxId, String date) {
        StringBuilder detail = new StringBuilder();
        detail.append(context.getString(R.string.notification_detail));detail.append(boxId);detail.append("\n\n");
        String notificationContent = detail.toString();
        long[] myVibratePattern = {0,200,200,300};

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setTicker(context.getString(R.string.notification_tickertext))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(notificationContent)
                .setAutoCancel(true)
                .setVibrate(myVibratePattern);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(boxId, notificationBuilder.build());
        Log.d(TAG,"NOTIFICATION CREATED:" + notificationContent);
    }

    private void generateSmsMessage() {
        try {
            Log.d(TAG, "sendSmsMessage()...");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("00905313083780", null, "sms test", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

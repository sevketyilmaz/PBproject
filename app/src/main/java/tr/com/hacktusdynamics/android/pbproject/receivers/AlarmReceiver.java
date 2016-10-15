package tr.com.hacktusdynamics.android.pbproject.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import tr.com.hacktusdynamics.android.pbproject.Constants;
import tr.com.hacktusdynamics.android.pbproject.R;
import tr.com.hacktusdynamics.android.pbproject.models.MyLab;

import static android.content.Context.MODE_PRIVATE;
import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_NAME;
import static tr.com.hacktusdynamics.android.pbproject.Constants.PREF_SMS_SEND;
import static tr.com.hacktusdynamics.android.pbproject.MyApplication.sApplicationContext;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    String title,description;
    String destinationAddress;//notified user phone number

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()...");
        destinationAddress = MyLab.get(context).getCurrentUserProfile().getDependentPhone();
        String action  = intent.getAction();
        if(Constants.ACTION_ALARM_PILLBOX.equals(action)){
            Bundle bundle = intent.getExtras();
            int boxId = bundle.getInt(Constants.BOX_ID);
            String date = bundle.getString(Constants.DATE);

            //generateNotification
            String notificationContent = generateNotification(context, boxId, date);
            //TODO: generate SMS message
            SharedPreferences sp = sApplicationContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            boolean sendSms = sp.getBoolean(PREF_SMS_SEND, true);
            Log.d(TAG, "sendSMS: " + sendSms);
            if(sendSms) {
                generateSmsMessage(destinationAddress, notificationContent);
            }
        }
    }

    /**
     * Generate notification for alarm
     * return notificationContent
     */
    private String generateNotification(Context context, int boxId, String date) {
        StringBuilder detail = new StringBuilder();
        detail.append(context.getString(R.string.notification_detail));detail.append(boxId+1);detail.append("\n\n");
        String notificationContent = detail.toString();
        long[] myVibratePattern = {0,200,200,300};
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setTicker(context.getString(R.string.notification_tickertext))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(notificationContent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(myVibratePattern);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(boxId+1, notificationBuilder.build());
        Log.d(TAG,"NOTIFICATION CREATED:" + notificationContent);
        return notificationContent;
    }

    private void generateSmsMessage(String destinationAddress, String smsContent) {
        try {
            Log.d(TAG, "generateSmsMessage()..to: " + destinationAddress + " with: " + smsContent);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destinationAddress , null, smsContent, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

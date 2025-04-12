package com.satyajitghosh.mediclock.medicine;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AlarmManagerHandler extends AppCompatActivity {


    public static final String CHANNEL_ID = "10";

    /**
     * This method is used to add an alarm .
     *
     * @param context        it provides the context
     * @param hour           it provides the hour to set the alarm
     * @param minute         it provides the minute to set the alarm
     * @param medicineName   it provides the medicineName to set up the alert , this further passed to the BroadCastReceiver to show the detail on Notifications.
     * @param notificationId it provides the notificationID
     * @param Food           it tells whether the medicine is to consume after food/before food.
     */
    public static void addAlert(Context context, int hour, int minute, String medicineName, int notificationId, String Food) {


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        //if alarm time has already passed, increment day by 1
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        }

        long time = cal.getTimeInMillis();

        Intent intent = new Intent(context, MyBroadcastReceiver.class)
                .putExtra("MedicineName", medicineName)
                .putExtra("Food", Food)
                .putExtra("time", time)
                .putExtra("notificationId", notificationId)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } catch (Exception e) {
            Log.d("AlarmManagerError", e.toString());
            Toast.makeText(context.getApplicationContext(), "We cannot setup reminder on your Device", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is used for setting the repeated alarms
     *
     * @param context        It provides the context
     * @param time           It provides the time in milliseconds of last fired alarm
     * @param medicineName   It provides the medicine name
     * @param Food           It provides the food name
     * @param notificationId It provides the notification id
     */
    public static void addAlert(Context context, long time, String medicineName, String Food, int notificationId) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        
        // Perbaiki flag PendingIntent
        Intent intent = new Intent(context, MyBroadcastReceiver.class)
                .putExtra("MedicineName", medicineName)
                .putExtra("Food", Food)
                .putExtra("time", time)
                .putExtra("notificationId", notificationId)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * This method is used to Create the notification Channel.
     *
     * @param context it provides the context
     */

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Medicine Reminder";
            String description = "This is to send notification about Medicine reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * This method is used to cancel the alarms.
     *
     * @param context it provides the context
     * @param mrh     it is the object of MedicineRecordHandler class. It is used here to fetch the notificationId of a alarm to cancel that particular alarm.
     */
    public static void cancelAlarm(Context context, MedicineRecordHandler mrh) {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);

        for (TIME.AlarmBundle i : mrh.getReminder()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i.getNotificationID(), intent, 0);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

    }

    /**
     * This method is used to initiate the alarms. It traverse for all the time selected for a single Record , then based on it sets all the alarm for a Record.
     *
     * @param mrh     it is the Object of a MedicineRecordHandler class . It holds all the information regarding a single Entry .
     * @param context it provides the context .
     */
    public static void initAlarm(MedicineRecordHandler mrh, Context context) {

        for (TIME.AlarmBundle i : mrh.getReminder()) {
            int hour = Integer.parseInt(i.getTime().substring(0, 2));
            int minutes = Integer.parseInt(i.getTime().substring(2, 4));
            String Food;

            if (mrh.getBeforeFood()) {
                Food = "before food";
            } else {
                Food = "after food";
            }

            AlarmManagerHandler.addAlert(context, hour, minutes, mrh.getName(), i.getNotificationID(), Food);
        }
    }

    /**
     * This method is used to generate unique set of RequestCode for the PendingIntents of AlarmManager.
     * This method is also used along with medicineName to construct the child name in FireBase DataBase & to give unique id to notifications.
     *
     * @return a random generated number
     */
    public static int setUniqueNotificationId() {
        return ((int) ((Math.random() * (99999 - 11111)) + 11111)) + ((int) ((Math.random() * (9999 - 1111)) + 1111));
    }


}

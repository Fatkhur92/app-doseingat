package com.satyajitghosh.mediclock;

import static com.satyajitghosh.mediclock.medicine.AlarmManagerHandler.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.satyajitghosh.mediclock.medicine.AlarmManagerHandler;

/**
 * This is a broadcast Receiver for LabTests and Doctor Appointments.
 * @author SATYAJIT GHOSH
 * @since 2.0.0
 */
public class CommonBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CommonBroadCastReceiver", "Receiver Started");

        boolean flag = intent.getBooleanExtra("flag", true); //true mean doc , false lab
        if (flag) {
            String DoctorName = intent.getStringExtra("DoctorName");
            String reason = intent.getStringExtra("reason");
            showNotificationDoc(context, DoctorName, reason);
        } else {
            String TestName = intent.getStringExtra("TestName");
            showNotificationLab(context, TestName);
        }

    }

    @SuppressLint("MissingPermission")
    public void showNotificationDoc(Context context, String DoctorName, String reason) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.mediclock_icon)
                .setContentTitle("MediClock Alert")
                .setContentText("You have an Appointment")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Please go to " + DoctorName + " on time for " + reason))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(AlarmManagerHandler.setUniqueNotificationId(), builder.build());
    }

    @SuppressLint("MissingPermission")
    public void showNotificationLab(Context context, String TestName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.mediclock_icon)
                .setContentTitle("MediClock Alert")
                .setContentText("You have a Lab Test")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Please go for " + TestName + " test"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(AlarmManagerHandler.setUniqueNotificationId(), builder.build());
    }
}

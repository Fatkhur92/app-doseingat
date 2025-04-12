package com.satyajitghosh.mediclock.medicine;

import static com.satyajitghosh.mediclock.medicine.AlarmManagerHandler.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.satyajitghosh.mediclock.R;

/**
 * This service is started by the broadcastReceiver. It it is used for showing the notifications and alarms .
 *
 * @author SATYAJIT GHOSH
 * @since 1.5.0
 */
public class MyAlarmService extends Service {

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        mediaPlayer.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String MedicineName = intent.getStringExtra("MedicineName");
        String Food = intent.getStringExtra("Food");
        long time = intent.getLongExtra("time", 0);
        int notificationId = intent.getIntExtra("notificationId", 0);
        AlarmManagerHandler.addAlert(getApplicationContext(), time, MedicineName, Food, notificationId); // It initiates the next alarm.
        long[] pattern = {0, 100, 500};
        vibrator.vibrate(pattern, 0);

        mediaPlayer.start();
        AlarmManagerHandler.createNotificationChannel(this);
        showNotification(this, MedicineName, Food);
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void showNotification(Context context, String MedicineName, String Food) {
        Intent notificationIntent = new Intent(this, RingActivity.class).putExtra("MedicineName", MedicineName).putExtra("food", Food);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, AlarmManagerHandler.setUniqueNotificationId(), notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("MediClock Reminder")
                .setContentText("Hey, Take your medicine " + MedicineName + " " + Food + ".")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true)
                .build();


        // notificationId is a unique id for each notification
        startForeground(AlarmManagerHandler.setUniqueNotificationId(), notification);
    }


}

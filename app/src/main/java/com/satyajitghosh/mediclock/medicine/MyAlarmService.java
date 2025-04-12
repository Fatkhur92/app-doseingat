package com.satyajitghosh.mediclock.medicine;

import static com.satyajitghosh.mediclock.medicine.AlarmManagerHandler.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.satyajitghosh.mediclock.R;

public class MyAlarmService extends Service {
    private static final String TAG = "MyAlarmService";
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        initMediaPlayer();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AlarmManagerHandler.createNotificationChannel(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        String medicineName = intent.getStringExtra("MedicineName");
        String food = intent.getStringExtra("Food");
        long time = intent.getLongExtra("time", 0);
        int notificationId = intent.getIntExtra("notificationId", 0);

        // Main change: Play alarm immediately when service starts
        playAlarm();

        // Start foreground service with notification
        startForeground(notificationId, buildNotification(medicineName, food));

        // Schedule next alarm
        AlarmManagerHandler.addAlert(getApplicationContext(), time, medicineName, food, notificationId);

        return START_STICKY;
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f, 1.0f); // Set volume to max
        } catch (Exception e) {
            Log.e(TAG, "MediaPlayer initialization failed", e);
        }
    }

    private void playAlarm() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 100, 500};
                vibrator.vibrate(pattern, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to play alarm", e);
        }
    }

    private Notification buildNotification(String medicineName, String food) {
        Intent ringIntent = new Intent(this, RingActivity.class)
                .putExtra("MedicineName", medicineName)
                .putExtra("food", food)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent ringPendingIntent = PendingIntent.getActivity(
                this,
                AlarmManagerHandler.setUniqueNotificationId(),
                ringIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
    
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("MediClock Reminder")
                .setContentText("Hey, take your medicine " + medicineName + " " + food + ".")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(ringPendingIntent, true)
                .setAutoCancel(true)
                .setContentIntent(ringPendingIntent);
    
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
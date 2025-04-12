package com.satyajitghosh.mediclock.medicine;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.satyajitghosh.mediclock.R;

public class RingActivity extends AppCompatActivity {
    private TextView textView;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Keep screen on and show over lockscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED 
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON 
            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON 
            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            
        setContentView(R.layout.activity_ring);
        
        // Initialize alarm sound and vibration
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        // Start alarm
        playAlarm();
        
        Intent intent = getIntent();
        String Medicine = intent.getStringExtra("MedicineName");
        String food = intent.getStringExtra("food");
        String text = "Take the doses of \n" + Medicine + " \n" + food;
        textView = findViewById(R.id.medicine_name_view);
        textView.setText(text);

        findViewById(R.id.button_dismiss).setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));

        findViewById(R.id.button_dismiss).setOnClickListener(view -> {
            stopAlarm();
            finish();
        });
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
            e.printStackTrace();
        }
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        if (vibrator != null) {
            vibrator.cancel();
        }
        
        // Stop the service
        stopService(new Intent(this, MyAlarmService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
}
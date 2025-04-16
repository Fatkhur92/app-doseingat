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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED 
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON 
            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON 
            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            
        setContentView(R.layout.activity_ring);

        Intent intent = getIntent();
        String Medicine = intent.getStringExtra("MedicineName");
        String food = intent.getStringExtra("food");
        String text = "Silahkan minum obat \n" + Medicine + " \n" + food;
        textView = findViewById(R.id.medicine_name_view);
        textView.setText(text);

        findViewById(R.id.button_dismiss).setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));

        findViewById(R.id.button_dismiss).setOnClickListener(view -> {
            stopService(new Intent(this, MyAlarmService.class));
            finish();
        });
    }
}
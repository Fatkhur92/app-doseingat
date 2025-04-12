package com.satyajitghosh.mediclock.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.satyajitghosh.mediclock.R;

/**
 * This activity is used when the user receives alarms with a full screen intent. It has a dismiss button that helps user to dismiss the alarm
 *
 * @author SATYAJIT GHOSH
 * @since 1.6.0
 */
public class RingActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.activity_ring);
        Intent intent = getIntent();
        String Medicine = intent.getStringExtra("MedicineName");
        String food = intent.getStringExtra("food");
        String text = "Take the doses of \n" + Medicine + " \n" + food;
        textView = findViewById(R.id.medicine_name_view);
        textView.setText(text);

        findViewById(R.id.button_dismiss).setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));

        findViewById(R.id.button_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentService = new Intent(getApplicationContext(), MyAlarmService.class);
                getApplicationContext().stopService(intentService);
                finish();
            }
        });
    }
}
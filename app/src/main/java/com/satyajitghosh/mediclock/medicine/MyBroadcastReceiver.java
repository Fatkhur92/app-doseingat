package com.satyajitghosh.mediclock.medicine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        String MedicineName = intent.getStringExtra("MedicineName");
        String Food = intent.getStringExtra("Food");
        long time = intent.getLongExtra("time", 0);
        int notificationId = intent.getIntExtra("notificationId", 0);

        context.startService(new Intent(context, MyAlarmService.class)
                .putExtra("MedicineName", MedicineName)
                .putExtra("Food", Food)
                .putExtra("time", time)
                .putExtra("notificationId", notificationId)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

        );

    }


}

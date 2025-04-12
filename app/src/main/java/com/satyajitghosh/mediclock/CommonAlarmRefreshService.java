package com.satyajitghosh.mediclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.satyajitghosh.mediclock.doctor.DoctorDataModel;
import com.satyajitghosh.mediclock.lab.LabTestDataModel;
import com.satyajitghosh.mediclock.medicine.AlarmRefreshService;

import java.util.Calendar;
import java.util.Objects;

/**
 * This service is used to add,delete,update and refresh the alarms of all the sections.
 * @author SATYAJIT GHOSH
 * @since 2.0.0
 *
 */
public class CommonAlarmRefreshService extends Service {

    protected GoogleSignInAccount account;
    private DatabaseReference mDatabaseDoc;
    private DatabaseReference mDatabaseLab;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("CommonAlarmRefreshService", "CommonAlarmRefreshService Started");
        startService(new Intent(this, AlarmRefreshService.class));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        mDatabaseDoc = FirebaseDatabase.getInstance().getReference().child("AppointmentRecord").child(Objects.requireNonNull(user.getUid()));
        mDatabaseLab = FirebaseDatabase.getInstance().getReference().child("LabTestRecord").child(Objects.requireNonNull(user.getUid()));
        mDatabaseDoc.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DocrefreshData(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DocrefreshData(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                onDeleteDoc(snapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabaseLab.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LabrefreshData(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LabrefreshData(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                onDeleteLab(snapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void LabrefreshData(DataSnapshot snapshot) {
        LabTestDataModel labTestDataModel = snapshot.getValue(LabTestDataModel.class);
        int date = labTestDataModel.getDay();
        int month = labTestDataModel.getMonth();
        int year = labTestDataModel.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        long time = calendar.getTimeInMillis();

        if (time <= System.currentTimeMillis()) {
            return;
        }
        Intent intent = new Intent(this, CommonBroadCastReceiver.class)
                .putExtra("TestName", labTestDataModel.getTestName()).putExtra("flag", false);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, labTestDataModel.getNotificationId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);


    }

    public void DocrefreshData(DataSnapshot snapshot) {

        DoctorDataModel doctorDataModel = snapshot.getValue(DoctorDataModel.class);
        assert doctorDataModel != null;
        int date = doctorDataModel.getDate();
        int month = doctorDataModel.getMonth();
        int year = doctorDataModel.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        long time = calendar.getTimeInMillis();

        if (time <= System.currentTimeMillis()) {
            return;
        }
        Intent intent = new Intent(this, CommonBroadCastReceiver.class)
                .putExtra("DoctorName", doctorDataModel.getName())
                .putExtra("reason", doctorDataModel.getReason());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, doctorDataModel.getNotificationId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);


    }

    public void onDeleteDoc(DataSnapshot snapshot) {
        DoctorDataModel doctorDataModel = snapshot.getValue(DoctorDataModel.class);

        Intent intent = new Intent(this, CommonAlarmRefreshService.class);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, doctorDataModel.getNotificationId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    public void onDeleteLab(DataSnapshot snapshot) {
        LabTestDataModel labTestDataModel = snapshot.getValue(LabTestDataModel.class);

        Intent intent = new Intent(this, CommonAlarmRefreshService.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, labTestDataModel.getNotificationId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

}

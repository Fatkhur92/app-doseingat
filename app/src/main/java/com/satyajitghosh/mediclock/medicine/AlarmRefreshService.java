package com.satyajitghosh.mediclock.medicine;

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

import java.util.Objects;

/**
 * This service starts at the program launch from the MainActivity and it refreshes all the alarms after fetching the details from the fireBase Database.
 * It is also used for adding and updating data to the firebase.
 * AlarmManager class doesn't store the alarms on the device restart , so it is also necessary.
 *
 * @author SATYAJIT GHOSH
 * @since 1.5.0
 */
public class AlarmRefreshService extends Service {
    protected GoogleSignInAccount account;
    private DatabaseReference mDatabase;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("AlarmRefreshService", "AlarmRefreshService Started");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("MedicineRecord").child(Objects.requireNonNull(user.getUid()));
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                refreshData(snapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                refreshData(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                deleteData(snapshot);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void refreshData(DataSnapshot snapshot) {

        MedicineRecordHandler mrd = snapshot.getValue(MedicineRecordHandler.class);
        AlarmManagerHandler.initAlarm(mrd, getApplicationContext());
    }

    public void deleteData(DataSnapshot snapshot) {
        MedicineRecordHandler mrd = snapshot.getValue(MedicineRecordHandler.class);
        AlarmManagerHandler.cancelAlarm(getApplicationContext(), mrd);
    }


}

package com.satyajitghosh.mediclock.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.satyajitghosh.mediclock.R;
import com.satyajitghosh.mediclock.lab.labActivity;
import com.satyajitghosh.mediclock.medicine.DisplayMedicineActivity;

import java.util.ArrayList;
import java.util.Objects;

public class DoctorActivity extends AppCompatActivity {

    protected GoogleSignInAccount account;
    private ListView listview;
    private DatabaseReference mDatabase;
    private ArrayList<DoctorDataModel> arrayList;
    private DocCustomAdapter c;
    private BottomAppBar bottomAppBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("AppointmentRecord").child(Objects.requireNonNull(user.getUid())); //Takes the relative path of the user to get the instance of only that user not others.
        listview = findViewById(R.id.doctor_listview);
        bottomAppBar = findViewById(R.id.doctor_bottomAppBar);

        arrayList = new ArrayList<>();
        TextView account_user_name_view = findViewById(R.id.doctor_account_user_name_view);
        account_user_name_view.setText("Hi, " + user.getDisplayName()); //This is used to show the name of user on screen
        account_user_name_view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));

        c = new DocCustomAdapter(getApplicationContext(), arrayList);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DoctorDataModel doctorDataModel = snapshot.getValue(DoctorDataModel.class);
                assert doctorDataModel != null;
                doctorDataModel.key = snapshot.getKey();

                arrayList.add(doctorDataModel);
                c.notifyDataSetChanged();
                emptyImage();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                c.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                DoctorDataModel doctorDataModel = snapshot.getValue(DoctorDataModel.class);
                assert doctorDataModel != null;
                doctorDataModel.key = snapshot.getKey();

                for (int i = 0; i < arrayList.size(); i++) {
                    if (doctorDataModel.key.equals(arrayList.get(i).key)) {
                        arrayList.remove(i);
                    }
                }
                //  arrayList.remove(doctorDataModel);

                c.notifyDataSetChanged();
                emptyImage();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listview.setAdapter(c);

        findViewById(R.id.doctor_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorActivity.this, DoctorAddActivity.class));
            }
        });

//        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_medicine:
//                        startActivity(new Intent(DoctorActivity.this, DisplayMedicineActivity.class));
//                        break;
//                    case R.id.nav_lab:
//                        startActivity(new Intent(DoctorActivity.this, labActivity.class));
//                        break;
//                    default:
//                        return false;
//                }
//                return true;
//            }
//        });


    }

    public void emptyImage() {
        if (c.isEmpty()) {
            findViewById(R.id.doctor_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.doctor_emptyText).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.doctor_empty).setVisibility(View.INVISIBLE);
            findViewById(R.id.doctor_emptyText).setVisibility(View.INVISIBLE);
        }
    }


}
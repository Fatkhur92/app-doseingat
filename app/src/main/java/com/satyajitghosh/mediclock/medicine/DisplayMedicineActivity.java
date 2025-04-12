package com.satyajitghosh.mediclock.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.satyajitghosh.mediclock.R;
import com.satyajitghosh.mediclock.doctor.DoctorActivity;
import com.satyajitghosh.mediclock.lab.labActivity;

import java.util.ArrayList;
import java.util.Objects;


public class DisplayMedicineActivity extends AppCompatActivity {
    protected GoogleSignInAccount account;
    private ListView listview;
    private DatabaseReference mDatabase;
    private ArrayList<MedicineRecordHandler> arrayList;
    private CustomAdapter c;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_medicine);

        account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("MedicineRecord").child(Objects.requireNonNull(user.getUid())); //Takes the relative path of the user to get the instance of only that user not others.
        listview = findViewById(R.id.listview);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        arrayList = new ArrayList<>();
        TextView account_user_name_view = findViewById(R.id.account_user_name_view);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference("user_data");
            
            // Query untuk mencari data user berdasarkan user_id
            Query query = userDataRef.orderByChild("user_id").equalTo(uid);
            
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Ambil nama dari child "name"
                            String name = snapshot.child("name").getValue(String.class);
                            if (name != null && !name.isEmpty()) {
                                account_user_name_view.setText(String.format("Hi, %s", name));
                            } else {
                                account_user_name_view.setText("Hi, User");
                            }
                            return; // Keluar setelah menemukan data pertama
                        }
                    }
                    account_user_name_view.setText("Hi, User");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching user data", databaseError.toException());
                    account_user_name_view.setText("Hi, User");
                }
            });
        }
        c = new CustomAdapter(getApplicationContext(), arrayList);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MedicineRecordHandler medicineRecordHandler = snapshot.getValue(MedicineRecordHandler.class);
                medicineRecordHandler.key = snapshot.getKey();
                arrayList.add(medicineRecordHandler);
                emptyImage();
                c.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                c.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MedicineRecordHandler medicineRecordHandler = snapshot.getValue(MedicineRecordHandler.class);
                medicineRecordHandler.key = snapshot.getKey();

                for (int i = 0; i < arrayList.size(); i++) {
                    if (medicineRecordHandler.key.equals(arrayList.get(i).key)) {
                        arrayList.remove(i);
                    }
                }
                emptyImage();
                c.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Check you internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        listview.setAdapter(c);

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayMedicineActivity.this, HomeActivity.class).putExtra("UserName", user.getDisplayName()).putExtra("Id", user.getUid())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                );
            }
        });

    }


    public void emptyImage() {
        if (c.isEmpty()) {
//            findViewById(R.id.empty).setVisibility(View.VISIBLE);
            findViewById(R.id.emptyText).setVisibility(View.VISIBLE);
        } else {
//            findViewById(R.id.empty).setVisibility(View.INVISIBLE);
            findViewById(R.id.emptyText).setVisibility(View.INVISIBLE);

        }
    }

}


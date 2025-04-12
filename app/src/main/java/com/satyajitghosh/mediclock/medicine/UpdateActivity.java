package com.satyajitghosh.mediclock.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.satyajitghosh.mediclock.R;
import com.satyajitghosh.mediclock.medicine.AlarmManagerHandler;
import com.satyajitghosh.mediclock.medicine.InputValidationHandler;
import com.satyajitghosh.mediclock.medicine.MedicineRecordHandler;
import com.satyajitghosh.mediclock.medicine.TIME;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {
    MedicineRecordHandler oldRecord;
    private TextInputLayout name;
    private TextInputLayout note;
    private Button updateBtn;
    private RadioGroup radioGroup;
    private MaterialButtonToggleGroup materialButtonToggleGroup;
    private MaterialButtonToggleGroup materialButtonToggleGroup1;
    private boolean before_food;
    private GoogleSignInClient mGoogleSignInClient;
    private Button cancelBtn;
    private Button up_custom_time;
    private String custom_time_value = "0000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent intent = getIntent();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        String KEY = intent.getStringExtra("key");
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference().child("MedicineRecord").child(Objects.requireNonNull(user.getUid()));

        myRef.child(KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MedicineRecordHandler mrd = snapshot.getValue(MedicineRecordHandler.class);
                assert mrd != null;
                prefillData(mrd);
                oldRecord = mrd;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        name = findViewById(R.id.up_name);
        note = findViewById(R.id.up_note);
        radioGroup = findViewById(R.id.up_radioGroup);
        materialButtonToggleGroup = findViewById(R.id.up_toggleButton);
        materialButtonToggleGroup1 = findViewById(R.id.up_toggleButton1);
        updateBtn = findViewById(R.id.up_updatebtn);
        cancelBtn = findViewById(R.id.up_cancel);
        up_custom_time = findViewById(R.id.up_custom_time);


        //This code is for update button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oldRecord != null) {
                    AlarmManagerHandler.cancelAlarm(getApplicationContext(), oldRecord);
                }
        
                MedicineRecordHandler newData = getData();
                if (InputValidationHandler.inputValidation(newData.getName(), newData.getReminder())) {
                    myRef.child(KEY).setValue(newData) // Updates the data to FireBase
                        .addOnSuccessListener(aVoid -> {
                            // Set up new alarms after successful update
                            AlarmManagerHandler.initAlarm(newData, getApplicationContext());
                            
                            startActivity(
                                new Intent(UpdateActivity.this, DisplayMedicineActivity.class)
                                    .putExtra("UserName", user.getDisplayName())
                                    .putExtra("Id", user.getUid())
                            );
                        })
                        .addOnFailureListener(e -> {
                            // Handle update failure
                            Toast.makeText(UpdateActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                } else {
                    InputValidationHandler.showDialog(UpdateActivity.this);
                }
            }
        });

        up_custom_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create final variables for hour and minute
                final int[] initialHour = {0};
                final int[] initialMinute = {0};
                
                // Parse current time if available
                String currentTime = up_custom_time.getText().toString();
                if (!currentTime.contains("Custom")) {
                    try {
                        String[] parts = currentTime.split(":");
                        initialHour[0] = Integer.parseInt(parts[0]);
                        initialMinute[0] = Integer.parseInt(parts[1]);
                    } catch (Exception e) {
                        // Use default values if parsing fails
                        initialHour[0] = 0;
                        initialMinute[0] = 0;
                    }
                }
        
                MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(initialHour[0])
                    .setMinute(initialMinute[0])
                    .setTitleText(currentTime.contains("Custom") ? "Select Alarm Time" : "Edit Alarm Time")
                    .build();
        
                picker.show(getSupportFragmentManager(), "timePicker");
                
                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the selected hour and minute
                        int hour = picker.getHour();
                        int minute = picker.getMinute();
                        
                        // Format the time for display
                        String formattedTime = String.format("%02d:%02d", hour, minute);
                        up_custom_time.setText(formattedTime);
                        
                        // Store the time value in 24-hour format (e.g., "0830")
                        custom_time_value = String.format("%02d%02d", hour, minute);
                        
                        // Auto-select the custom time button
                        materialButtonToggleGroup1.check(R.id.up_custom_time);
                    }
                });
            }
        });


        //This code is for Cancel Button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(UpdateActivity.this, DisplayMedicineActivity.class)
                                .putExtra("UserName", user.getDisplayName()).putExtra("Id", user.getUid())
                );
            }
        });


    }

    /**
     * It prefills the data to the fields for convenience of user when the activity starts
     */
    public void prefillData(MedicineRecordHandler mrd) {
        String nameValue = mrd.getName();
        String noteValue = mrd.getNotes();
        Boolean beforeFood = mrd.getBeforeFood();

        if (beforeFood) {
            radioGroup.check(R.id.up_radio_button_1);
        } else {
            radioGroup.check(R.id.up_radio_button_2);
        }

        name.getEditText().setText(nameValue);
        note.getEditText().setText(noteValue);

        for (TIME.AlarmBundle i : mrd.getReminder()) {
            if (i.time.equals(TIME.MORNING)) {
                materialButtonToggleGroup.check(R.id.up_morning);

            } else if (i.time.equals(TIME.AFTERNOON)) {
                materialButtonToggleGroup.check(R.id.up_lunch);
            } else if (i.time.equals(TIME.NIGHT)) {
                materialButtonToggleGroup1.check(R.id.up_night);
            } else {
                Button button = findViewById(R.id.up_custom_time);
                button.setText(new StringBuilder().append(i.time.substring(0, 2)).append(":").append(i.time.substring(2, 4)).toString());
                materialButtonToggleGroup1.check(R.id.up_custom_time);
            }
        }

    }

    /**
     * It captures all the data from different fields
     *
     * @return it returns a object of MedicineRecordHandler class
     */
    public MedicineRecordHandler getData() {

        String nameValue = name.getEditText().getText().toString();
        String noteValue = note.getEditText().getText().toString();
        Boolean beforeFoodValue = false;
        if (radioGroup.getCheckedRadioButtonId() == R.id.up_radio_button_1) {
            beforeFoodValue = true;
        }

        List<Integer> arr = materialButtonToggleGroup.getCheckedButtonIds();
        arr.addAll(materialButtonToggleGroup1.getCheckedButtonIds());

        ArrayList<TIME.AlarmBundle> time = new ArrayList<>();
        for (Integer i : arr) {
            if (i == R.id.up_morning) {
                time.add(new TIME.AlarmBundle(TIME.MORNING, AlarmManagerHandler.setUniqueNotificationId()));
            } else if (i == R.id.up_lunch) {
                time.add(new TIME.AlarmBundle(TIME.AFTERNOON, AlarmManagerHandler.setUniqueNotificationId()));
            } else if (i == R.id.up_night) {
                time.add(new TIME.AlarmBundle(TIME.NIGHT, AlarmManagerHandler.setUniqueNotificationId()));
            } else if (i == R.id.up_custom_time) {
                time.add(new TIME.AlarmBundle(custom_time_value, AlarmManagerHandler.setUniqueNotificationId()));
            }

        }


        MedicineRecordHandler mrh = new MedicineRecordHandler(
                nameValue,
                noteValue,
                beforeFoodValue,
                time
        );

        return mrh;
    }

}



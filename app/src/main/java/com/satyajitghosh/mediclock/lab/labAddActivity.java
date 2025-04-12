package com.satyajitghosh.mediclock.lab;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.satyajitghosh.mediclock.R;
import com.satyajitghosh.mediclock.medicine.AlarmManagerHandler;
import com.satyajitghosh.mediclock.medicine.InputValidationHandler;

import java.util.Calendar;
import java.util.Objects;

public class labAddActivity extends AppCompatActivity {
    private static final String[] TESTNAME = new String[]{
            "amniocentesis", "blood analysis", "blood count", "blood typing", "bone marrow aspiration", "cephalin-cholesterol flocculation", "enzyme analysis", "epinephrine tolerance test", "glucose tolerance test", "hematocrit", "immunologic blood test", "inulin clearance", "serological test", "thymol turbidity", "gastric fluid analysis", "kidney function test", "liver function test", "lumbar puncture", "malabsorption test", "Pap smear", "phenolsulfonphthalein test", "pregnancy test", "prenatal testing", "protein-bound iodine test", "syphilis test", "thoracentesis", "thyroid function test", "toxicology test", "urinalysis", "angiocardiography", "angiography", "cerebral angiography", "brain scanning", "echoencephalography", "magnetoencephalography", "pneumoencephalography", "cholecystography", "echocardiography", "endoscopic retrograde cholangiopancreatoscopy", "lung ventilation/perfusion scan", "magnetic resonance imaging", "cardiac magnetic resonance imaging", "functional magnetic resonance imaging", "magnetic resonance spectroscopy", "mammography", "myelography", "prenatal testing", "tomography", "computed tomography", "positron emission tomography", "single photon emission computed tomography", "ultrasound", "urography"
    };
    private static TextView lab_date_view;
    private static final int[] arr = new int[3];
    private DatabaseReference mDatabase;
    private TextInputLayout lab_doctor;
    private Button lab_add_btn;
    private AutoCompleteTextView lab_name_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_add);
        lab_date_view = findViewById(R.id.lab_date_view);
        lab_doctor = findViewById(R.id.lab_doctor);
        lab_add_btn = findViewById(R.id.lab_add_btn);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("LabTestRecord").child(Objects.requireNonNull(user.getUid()));


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TESTNAME);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.lab_name_edit);
        textView.setAdapter(adapter);


        lab_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testName = textView.getText().toString();
                String docName = lab_doctor.getEditText().getText().toString();

                if (inputValidation(testName, docName)) {

                    LabTestDataModel obj = new LabTestDataModel(arr[0], arr[1], arr[2], testName, docName, AlarmManagerHandler.setUniqueNotificationId());
                    mDatabase.child(obj.getTestName() + AlarmManagerHandler.setUniqueNotificationId()).setValue(obj);
                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(labAddActivity.this, labActivity.class));
                } else {
                    InputValidationHandler.showDialog(labAddActivity.this);
                }

            }
        });
    }

    public boolean inputValidation(String testName, String docName) {
        if (testName.contains(".") || testName.contains("[") || testName.contains("]") || testName.contains("$") || testName.contains("#")) {
            return false;
        }
        if (docName.contains(".") || docName.contains("[") || docName.contains("]") || docName.contains("$") || docName.contains("#")) {
            return false;
        }

        return !testName.isEmpty() && !docName.isEmpty() && arr[0] != 0 && testName.length() < 25 && docName.length() < 40;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new labAddActivity.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date+1 as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

            // Create a new instance of DatePickerDialog and return it
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String result = day + "/" + (month + 1) + "/" + year;
            lab_date_view.setText(result);
            arr[0] = day;
            arr[1] = month + 1;
            arr[2] = year;
        }

    }

}
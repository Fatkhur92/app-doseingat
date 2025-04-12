package com.satyajitghosh.mediclock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Data_Diri extends AppCompatActivity {

    private TextInputLayout nameInput, usiaInput, tekananDarahInput, notelInput;
    private RadioGroup kelaminRadioGroup, durasiRadioGroup, pendidikanRadioGroup, pekerjaanRadioGroup;
    private Button submitButton;

    private FirebaseDatabase mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datadiri);

        mDatabase = FirebaseDatabase.getInstance();

        // Initialize Input Layouts
        nameInput = findViewById(R.id.name);
        usiaInput = findViewById(R.id.usia);
        tekananDarahInput = findViewById(R.id.tekanan_darah);
        notelInput = findViewById(R.id.notel);

        // Initialize RadioGroups
        kelaminRadioGroup = findViewById(R.id.kelamin);
        durasiRadioGroup = findViewById(R.id.durasi);
        pendidikanRadioGroup = findViewById(R.id.pendidikan);
        pekerjaanRadioGroup = findViewById(R.id.status_pekerjaan);
        submitButton = findViewById(R.id.simpan_button);

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener(v -> submitData());
    }

    private void submitData() {
        String name = nameInput.getEditText().getText().toString().trim();
        String usia = usiaInput.getEditText().getText().toString().trim();
        String tekananDarah = tekananDarahInput.getEditText().getText().toString().trim();
        String notel = notelInput.getEditText().getText().toString().trim();

        // Get selected gender
        int selectedKelaminId = kelaminRadioGroup.getCheckedRadioButtonId();
        String kelamin = selectedKelaminId != -1 ? ((RadioButton) findViewById(selectedKelaminId)).getText().toString() : "";

        // Get selected disease duration
        int selectedDurasiId = durasiRadioGroup.getCheckedRadioButtonId();
        String durasi = selectedDurasiId != -1 ? ((RadioButton) findViewById(selectedDurasiId)).getText().toString() : "";

        // Get selected education
        int selectedPendidikanId = pendidikanRadioGroup.getCheckedRadioButtonId();
        String pendidikan = selectedPendidikanId != -1 ? ((RadioButton) findViewById(selectedPendidikanId)).getText().toString() : "";

        // Get selected job status
        int selectedPekerjaanId = pekerjaanRadioGroup.getCheckedRadioButtonId();
        String pekerjaan = selectedPekerjaanId != -1 ? ((RadioButton) findViewById(selectedPekerjaanId)).getText().toString() : "";

        // Validate all inputs
        if (name.isEmpty() || usia.isEmpty() || tekananDarah.isEmpty() || notel.isEmpty() ||
                kelamin.isEmpty() || durasi.isEmpty() || pendidikan.isEmpty() || pekerjaan.isEmpty()) {
            Toast.makeText(this, "Semua bidang harus diisi!", Toast.LENGTH_SHORT).show();
        } else {
            // If validation passes, save data to Firebase
            saveDataToFirebase(name, usia, tekananDarah, notel, kelamin, durasi, pendidikan, pekerjaan);
        }
    }

    private void saveDataToFirebase(String name, String usia, String tekananDarah, String notel,
                                    String kelamin, String durasi, String pendidikan, String pekerjaan) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = (user != null) ? user.getUid() : "Unknown";

        // Prepare the data to save in Firebase as a HashMap
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("usia", usia);
        userData.put("tekanan_darah", tekananDarah);
        userData.put("notel", notel);
        userData.put("kelamin", kelamin);
        userData.put("durasi", durasi);
        userData.put("pendidikan", pendidikan);
        userData.put("pekerjaan", pekerjaan);
        userData.put("user_id", uid);

        // Save data to Firebase Database
        DatabaseReference userRef = mDatabase.getReference("user_data").push();
        userRef.setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Data_Diri.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Data_Diri.this, kuisioner.class));
                finish();
            } else {
                Toast.makeText(Data_Diri.this, "Gagal menyimpan data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Data_Diri", "Gagal menyimpan data", task.getException());
            }
        });
    }
}

package com.satyajitghosh.mediclock;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.satyajitghosh.mediclock.MainActivity;

public class kuisioner extends AppCompatActivity {

    private RadioGroup jawaban1, jawaban2, jawaban3, jawaban4, jawaban5, jawaban6;
    private Button submitButton;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuisioner);

        mDatabase = FirebaseDatabase.getInstance();

        jawaban1 = findViewById(R.id.jawaban1);
        jawaban2 = findViewById(R.id.jawaban2);
        jawaban3 = findViewById(R.id.jawaban3);
        jawaban4 = findViewById(R.id.jawaban4);
        jawaban5 = findViewById(R.id.jawaban5);
        jawaban6 = findViewById(R.id.jawaban6);

        submitButton = findViewById(R.id.submitbtn);

        submitButton.setOnClickListener(v -> {
            String jawaban1Selected = getSelectedAnswer(jawaban1);
            String jawaban2Selected = getSelectedAnswer(jawaban2);
            String jawaban3Selected = getSelectedAnswer(jawaban3);
            String jawaban4Selected = getSelectedAnswer(jawaban4);
            String jawaban5Selected = getSelectedAnswer(jawaban5);
            String jawaban6Selected = getSelectedAnswer(jawaban6);

            if (jawaban1Selected.isEmpty() || jawaban2Selected.isEmpty() || jawaban3Selected.isEmpty() || jawaban4Selected.isEmpty() || jawaban5Selected.isEmpty() || jawaban6Selected.isEmpty()) {
                Toast.makeText(kuisioner.this, "Please answer all questions", Toast.LENGTH_SHORT).show();
            } else {
                saveKuisionerData(jawaban1Selected, jawaban2Selected, jawaban3Selected, jawaban4Selected, jawaban5Selected, jawaban6Selected);
            }
        });
    }

    private String getSelectedAnswer(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        return ""; // If no radio button is selected
    }

    private void saveKuisionerData(String jawaban1, String jawaban2, String jawaban3, String jawaban4, String jawaban5, String jawaban6) {
        KuisionerData kuisionerData = new KuisionerData(jawaban1, jawaban2, jawaban3, jawaban4, jawaban5, jawaban6);

        DatabaseReference kuisionerRef = mDatabase.getReference("kuisioner_data").push();
        kuisionerRef.setValue(kuisionerData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(kuisioner.this, "Kuisioner data saved successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(kuisioner.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(kuisioner.this, "Failed to save data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class KuisionerData {
        public String jawaban1, jawaban2, jawaban3, jawaban4, jawaban5, jawaban6;

        public KuisionerData(String jawaban1, String jawaban2, String jawaban3, String jawaban4, String jawaban5, String jawaban6) {
            this.jawaban1 = jawaban1;
            this.jawaban2 = jawaban2;
            this.jawaban3 = jawaban3;
            this.jawaban4 = jawaban4;
            this.jawaban5 = jawaban5;
            this.jawaban6 = jawaban6;
        }
    }
}

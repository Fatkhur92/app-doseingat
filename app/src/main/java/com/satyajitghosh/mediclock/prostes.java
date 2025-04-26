package com.satyajitghosh.mediclock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.satyajitghosh.mediclock.PostTestData;
import com.satyajitghosh.mediclock.medicine.DisplayMedicineActivity;

import java.util.HashMap;
import java.util.Map;

public class prostes extends AppCompatActivity {

    private RadioGroup jawaban1, jawaban2, jawaban3, jawaban4, jawaban5, jawaban6, jawaban7, jawaban8;
    private TextInputLayout jawaban9;
    private Button submitButton;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prostes);

        initializeFirebase();
        initializeViews();
        setupSubmitButton();
    }

    private void initializeFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializeViews() {
        jawaban1 = findViewById(R.id.jawaban1);
        jawaban2 = findViewById(R.id.jawaban2);
        jawaban3 = findViewById(R.id.jawaban3);
        jawaban4 = findViewById(R.id.jawaban4);
        jawaban5 = findViewById(R.id.jawaban5);
        jawaban6 = findViewById(R.id.jawaban6);
        jawaban7 = findViewById(R.id.jawaban7);
        jawaban8 = findViewById(R.id.jawaban8);
        jawaban9 = findViewById(R.id.jawaban9);
        submitButton = findViewById(R.id.submitbtn);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateAllAnswers()) {
                savePostTestDataToFirebase();
            } else {
                Toast.makeText(prostes.this, "Please answer all questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateAllAnswers() {
        return !getSelectedAnswer(jawaban1).isEmpty() &&
                !getSelectedAnswer(jawaban2).isEmpty() &&
                !getSelectedAnswer(jawaban3).isEmpty() &&
                !getSelectedAnswer(jawaban4).isEmpty() &&
                !getSelectedAnswer(jawaban5).isEmpty() &&
                !getSelectedAnswer(jawaban6).isEmpty() &&
                !getSelectedAnswer(jawaban7).isEmpty() &&
                !getSelectedAnswer(jawaban8).isEmpty()&&
                !getSelectedAnswerText(jawaban9).isEmpty();
    }

    private String getSelectedAnswer(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        return "";
    }

    private void savePostTestDataToFirebase() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        PostTestData postTestData = createPostTestData();

        DatabaseReference postTestRef = mDatabase.getReference("post_test_results").child(userId);
        postTestRef.setValue(postTestData)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateUserCompletionStatus(userId);
                } else {
                    Toast.makeText(prostes.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private String getSelectedAnswerText(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString().trim();
    }

    private PostTestData createPostTestData() {
        return new PostTestData(
            getSelectedAnswer(jawaban1),
            getSelectedAnswer(jawaban2),
            getSelectedAnswer(jawaban3),
            getSelectedAnswer(jawaban4),
            getSelectedAnswer(jawaban5),
            getSelectedAnswer(jawaban6),
            getSelectedAnswer(jawaban7),
            getSelectedAnswer(jawaban8),
            getSelectedAnswerText(jawaban9),
            System.currentTimeMillis()
        );
    }

    private void updateUserCompletionStatus(String userId) {
        DatabaseReference userRef = mDatabase.getReference("users").child(userId);
        userRef.child("hasCompletedPostTest").setValue(true)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(prostes.this, "Post-test completed successfully", Toast.LENGTH_SHORT).show();
                    navigateToMainApp();
                } else {
                    Toast.makeText(prostes.this, "Failed to update user status", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void navigateToMainApp() {
        Intent intent = new Intent(this, DisplayMedicineActivity.class);
        startActivity(intent);
        finish();
    }
}
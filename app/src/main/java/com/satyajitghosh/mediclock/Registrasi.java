package com.satyajitghosh.mediclock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registrasi extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        // Initializing Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Registrasi.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Registrasi.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(email, password);
                }
            }
        });
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Create a new user object with email and registration date
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        user.put("registrationDate", System.currentTimeMillis()); // Tambahkan timestamp registrasi
                        user.put("hasCompletedPostTest", false); // Flag untuk menandai apakah sudah selesai post-test
    
                        // Get user ID from Firebase Auth
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        
                        // Save user data to Firebase Realtime Database under users/{userId}
                        DatabaseReference userRef = mDatabase.getReference("users").child(userId);
                        userRef.setValue(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(Registrasi.this, "Registration and user data saved", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Registrasi.this, Data_Diri.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Registrasi.this, "Failed to save user data: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(Registrasi.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseAuth", "Error creating user", e));
    }
}
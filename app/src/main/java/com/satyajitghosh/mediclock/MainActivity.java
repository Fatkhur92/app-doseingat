package com.satyajitghosh.mediclock;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.satyajitghosh.mediclock.Edukasi;
import com.satyajitghosh.mediclock.medicine.AlarmManagerHandler;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private TextView forgotPasswordTextView, registerAccountTextView;
//    Button testBtn = findViewById(R.id.test_btn);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password);
        registerAccountTextView = findViewById(R.id.register_account);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });

        // Set OnClickListener for Forgot Password TextView
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your Forgot Password action, for example, opening a new activity
                Intent intent = new Intent(MainActivity.this, Registrasi.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for Register Account TextView
        registerAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your Register action, for example, opening RegisterActivity
                Intent intent = new Intent(MainActivity.this, Registrasi.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Check if it's time for post-test
                            checkPostTestRequirement(user.getUid());
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void checkPostTestRequirement(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long registrationTimestamp = dataSnapshot.child("registrationDate").getValue(Long.class);
                    Boolean hasCompletedPostTest = dataSnapshot.child("hasCompletedPostTest").getValue(Boolean.class);
                    
                    if (registrationTimestamp != null && (hasCompletedPostTest == null || !hasCompletedPostTest)) {
                        long twoWeeksInMillis = 14 * 24 * 60 * 60 * 1000L; // 2 minggu dalam milidetik
                        long currentTime = System.currentTimeMillis();
                        
                        if (currentTime - registrationTimestamp >= twoWeeksInMillis) {
                            // Sudah 2 minggu, tampilkan post-test
                            showPostTest();
                        } else {
                            // Belum 2 minggu, lanjut ke main activity
                            proceedToMainApp();
                        }
                    } else {
                        // Sudah menyelesaikan post-test atau data tidak valid
                        proceedToMainApp();
                    }
                } else {
                    // Data user tidak ditemukan
                    proceedToMainApp();
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error handling
                proceedToMainApp();
            }
        });
    }
    
    private void showPostTest() {
        Intent intent = new Intent(MainActivity.this, prostes.class);
        startActivity(intent);
        finish();
    }
    
    private void proceedToMainApp() {
        Intent intent = new Intent(MainActivity.this, Edukasi.class);
        startActivity(intent);
        finish();
    }
}

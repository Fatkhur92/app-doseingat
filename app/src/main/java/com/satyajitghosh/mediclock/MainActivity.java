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
import com.satyajitghosh.mediclock.medicine.DisplayMedicineActivity;
import com.satyajitghosh.mediclock.medicine.AlarmManagerHandler;


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



//        testBtn.setOnClickListener(v -> {
//            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//            int minute = Calendar.getInstance().get(Calendar.MINUTE) + 1;
//
//            AlarmManagerHandler.addAlert(
//                    this,
//                    hour,
//                    minute,
//                    "TEST MEDICINE",
//                    12345, // ID tetap untuk testing
//                    "after food"
//            );
//
//            Toast.makeText(this, "Alarm dijadwalkan", Toast.LENGTH_SHORT).show();
//        });

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
                            Intent intent = new Intent(MainActivity.this, DisplayMedicineActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

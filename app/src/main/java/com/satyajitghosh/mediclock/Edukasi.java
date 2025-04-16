package com.satyajitghosh.mediclock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.satyajitghosh.mediclock.medicine.DisplayMedicineActivity;


public class Edukasi extends AppCompatActivity {
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edukasi);
        submitButton = findViewById(R.id.submitbtn);
        setupSubmitButton();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
           proceedToMainApp();
        });
    }

    private void proceedToMainApp() {
        Intent intent = new Intent(Edukasi.this, DisplayMedicineActivity.class);
        startActivity(intent);
        finish();
    }


}
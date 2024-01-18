package com.example.theva;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        final EditText emailEditText = findViewById(R.id.emailEditText);
        final EditText fullNameEditText = findViewById(R.id.fullNameEditText);
        final RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        final EditText ageEditText = findViewById(R.id.ageEditText);
        final EditText houseAddressEditText = findViewById(R.id.houseAddressEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);
        final EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button submitButton = findViewById(R.id.submitButton);
        TextView loginTextView = findViewById(R.id.loginTextView);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString().trim();
                final String fullName = fullNameEditText.getText().toString().trim();
                final String age = ageEditText.getText().toString().trim();
                final String houseAddress = houseAddressEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                final RadioButton selectedRadioButton = findViewById(selectedId);

                if (email.isEmpty() || fullName.isEmpty() || age.isEmpty() || password.isEmpty() || houseAddress.isEmpty() || confirmPassword.isEmpty() || selectedRadioButton == null) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password and Confirm Password must match", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String gender = selectedRadioButton.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("fullName", fullName);
                                    user.put("gender", gender);
                                    user.put("age", age);
                                    user.put("houseAddress", houseAddress);
                                    user.put("password", password);  // It's not recommended to store passwords in Firestore

                                    db.collection("users").document(uid).collection("accountDetails").document("userInformation")
                                            .set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                                        // Navigate to DashboardActivity
                                                        startActivity(new Intent(RegisterActivity.this, UserDashboardActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Failed to register. Try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}


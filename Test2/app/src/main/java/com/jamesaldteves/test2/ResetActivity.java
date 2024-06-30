package com.jamesaldteves.test2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

/** @noinspection ALL*/
public class ResetActivity extends AppCompatActivity {

    private EditText mEmail;
    private Button mReset;
    private TextView mSignIn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        initializeViews();
        setupListeners();

    }

    private void initializeViews() {
        mEmail = findViewById(R.id.email_reset);
        mReset = findViewById(R.id.btn_resetpassword);
        mSignIn = findViewById(R.id.already_haveaccount);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupListeners() {
        mSignIn.setOnClickListener(v -> navigateToMainActivity());
        mReset.setOnClickListener(v -> attemptPasswordReset());
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void attemptPasswordReset() {

        String email = mEmail.getText().toString().trim();
        if (!isEmailValid(email)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        checkUserExists(email);

    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            showToast("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email");
            return false;
        }
        return true;
    }

    private void checkUserExists(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);  // Moved here for visibility in all cases
            if (task.isSuccessful()) {
                if (!task.getResult().getSignInMethods().isEmpty()) {
                    resetPassword(email);
                } else {
                    showToast("No user found with this email");
                }
            } else {
                showToast("Failed to check user: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                showToast("Check your email to reset your password");
                clearInputFields();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                showToast("Failed to send reset email: " + task.getException().getMessage());
            }
        });
    }

    private void clearInputFields() {
        mEmail.setText("");
    }

    private void showToast(String message) {
        Toast.makeText(ResetActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}


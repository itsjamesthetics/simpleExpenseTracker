package com.jamesaldteves.test2;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {


    enum ThemeMode {
        DARK, LIGHT
    }

    private Spinner themeSelectionSpinner;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch offlineModeSwitch;
    private Button customizeLayoutButton;
    private Button logoutButton;
    private Button deleteAccountButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        setupListeners();
        
    }


    private void initializeViews() {
        themeSelectionSpinner = findViewById(R.id.theme_selection);
        offlineModeSwitch = findViewById(R.id.offline_mode_switch); // Replace with your switch's ID
        customizeLayoutButton = findViewById(R.id.customize_layout_button); // Replace with your button's ID
        logoutButton = findViewById(R.id.logout_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
    }

    private void setupThemeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.theme_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSelectionSpinner.setAdapter(adapter);
        themeSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Dark Theme Mode
                        setThemeMode(ThemeMode.DARK);
                        break;
                    case 1: // Light Theme Mode
                        setThemeMode(ThemeMode.LIGHT);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no theme is selected
            }
        });
    }


    private void setThemeMode(ThemeMode mode) {
        // Save the selected theme mode to preferences
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (mode == ThemeMode.DARK) {
            // Set dark theme
            setTheme(R.style.AppTheme_Dark);
            editor.putString("Theme", "Dark");
        } else {
            // Set light theme
            setTheme(R.style.AppTheme);
            editor.putString("Theme", "Light");
        }

        editor.apply();

        recreate(); // To apply the theme change
    }

    private void setupListeners() {
        // Example: Setting a click listener for the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(SettingsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        offlineModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is on - Set Firebase to offline mode
                FirebaseDatabase.getInstance().goOffline();
            } else {
                // Switch is off - Set Firebase to online mode
                FirebaseDatabase.getInstance().goOnline();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAndDeleteAccount();
            }
        });

        // Other listeners for deleteAccountButton, offlineModeSwitch, etc.
    }

    private void confirmAndDeleteAccount() {


        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUserAccount();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void deleteUserAccount() {

        String userId = mAuth.getCurrentUser().getUid();

        // Delete user data from database
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userId)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Then delete user authentication record
                        mAuth.getCurrentUser().delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Account deleted successfully", LENGTH_LONG).show();
                                // Redirect to login screen or close the app
                            } else {
                                // Handle failure in deleting authentication record
                                Toast.makeText(SettingsActivity.this, "Failed to delete account: " + task1.getException().getMessage(), LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // Handle failure in deleting user data from the database
                        Toast.makeText(SettingsActivity.this, "Failed to delete user data: " + task.getException().getMessage(), LENGTH_LONG).show();
                    }
                });

    }

}
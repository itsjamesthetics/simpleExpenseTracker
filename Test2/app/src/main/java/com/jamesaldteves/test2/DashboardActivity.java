package com.jamesaldteves.test2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

/** @noinspection ALL*/
public class DashboardActivity extends AppCompatActivity {

    private CardView mTransaction, mView, mReminders, mCategories, mSettings, mLogout;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mTransaction = findViewById(R.id.transaction);
        mView = findViewById(R.id.view_transaction);
        mReminders = findViewById(R.id.reminders);
        mCategories = findViewById(R.id.categories);
        mSettings = findViewById(R.id.settings);
        mLogout = findViewById(R.id.logout);

        mTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewOutputActivity.class));
            }
        });

        mReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BillremindersActivity.class));
            }
        });

        mCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CategoriesActivity.class));
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> performLogout())
                .setNegativeButton("No", null)
                .show();
    }
    private void performLogout() {
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        Toast.makeText(DashboardActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
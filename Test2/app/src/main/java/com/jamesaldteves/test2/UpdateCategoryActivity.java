package com.jamesaldteves.test2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateCategoryActivity extends AppCompatActivity {

    private EditText editTextName, editTextDailyBudget, editTextWeeklyBudget, editTextMonthlyBudget, editTextYearlyBudget, editTextNote;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchBudget;
    private Button buttonUpdate, buttonCancel;
    private DatabaseReference databaseReference;
    private String categoryId; // Assume category ID is passed via Intent


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        initializeUI();
        setupFirebase();
        loadCategoryData();

        initializeUI();
        setupFirebase();
        loadCategoryData();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCategory();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Closes the activity
            }
        });

    }
    private void initializeUI() {
        editTextName = findViewById(R.id.update_category_name);
        editTextDailyBudget = findViewById(R.id.update_category_daily);
        editTextWeeklyBudget = findViewById(R.id.update_category_weekly);
        editTextMonthlyBudget = findViewById(R.id.update_category_monthly);
        editTextYearlyBudget = findViewById(R.id.update_category_yearly);
        editTextNote = findViewById(R.id.update_category_note);
        switchBudget = findViewById(R.id.category_switch);
        buttonUpdate = findViewById(R.id.btn_updatecategory);
        buttonCancel = findViewById(R.id.btn_updatecategorycancel);
    }

    private void setupFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("UserCategories");
    }

    private void loadCategoryData() {
        // Fetch category data using categoryId and populate the EditText fields
        // This will depend on how you pass and retrieve the category ID
    }

    private void updateCategory() {
        String name = editTextName.getText().toString().trim();
        String dailyBudget = editTextDailyBudget.getText().toString().trim();
        String weeklyBudget = editTextWeeklyBudget.getText().toString().trim();
        String monthlyBudget = editTextMonthlyBudget.getText().toString().trim();
        String yearlyBudget = editTextYearlyBudget.getText().toString().trim();
        String note = editTextNote.getText().toString().trim();
        boolean isBudgetEnabled = switchBudget.isChecked();

        if (!name.isEmpty()) {
            databaseReference.child(categoryId).child("name").setValue(name);
            databaseReference.child(categoryId).child("dailyBudget").setValue(dailyBudget);
            databaseReference.child(categoryId).child("weeklyBudget").setValue(weeklyBudget);
            databaseReference.child(categoryId).child("monthlyBudget").setValue(monthlyBudget);
            databaseReference.child(categoryId).child("yearlyBudget").setValue(yearlyBudget);
            databaseReference.child(categoryId).child("note").setValue(note);
            databaseReference.child(categoryId).child("isBudgetEnabled").setValue(isBudgetEnabled);
            Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
        }
    }

}
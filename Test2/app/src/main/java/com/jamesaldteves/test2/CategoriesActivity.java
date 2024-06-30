package com.jamesaldteves.test2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText mCategoryName, mCategoryDailyBudget, mCategoryWeeklyBudget, mCategoryMonthlyBudget, mCategoryYearlyBudget, mCategoryNote;

    private LinearLayout mLinearDaily, mLinearWeekly, mLinearMonthly, mLinearYearly;
    private Button  mCategoryCancel, mCategorySave;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch budgetSwitch;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        
        initializeViews();
        setupSwitch();
        setupButtons();
    }

    private void initializeViews() {
        mCategoryName = findViewById(R.id.category_name);
        mCategoryDailyBudget = findViewById(R.id.category_daily);
        mCategoryWeeklyBudget = findViewById(R.id.category_weekly);
        mCategoryMonthlyBudget = findViewById(R.id.category_monthly);
        mCategoryYearlyBudget = findViewById(R.id.category_yearly);
        mLinearDaily = findViewById(R.id.linear_daily);
        mLinearWeekly = findViewById(R.id.linear_weekly);
        mLinearMonthly = findViewById(R.id.linear_monthly);
        mLinearYearly = findViewById(R.id.linear_yearly);
        mCategoryNote = findViewById(R.id.category_note);
        mCategoryCancel = findViewById(R.id.btn_categorycancel);
        mCategorySave = findViewById(R.id.btn_categorysave);
        budgetSwitch = findViewById(R.id.category_switch);
    }

    private void setupButtons() {
        mCategorySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoryData();
            }
        });

        mCategoryCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFieldsAndExit();
                CategoriesActivity.super.onBackPressed();
            }
        });
    }

    private void saveCategoryData() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();
        String name = mCategoryName.getText().toString().trim();
        String dailyBudget = mCategoryDailyBudget.getText().toString().trim();
        String weeklyBudget = mCategoryWeeklyBudget.getText().toString().trim();
        String monthlyBudget = mCategoryMonthlyBudget.getText().toString().trim();
        String yearlyBudget = mCategoryYearlyBudget.getText().toString().trim();
        String categoryNote = mCategoryNote.getText().toString().trim();

        if (name.isEmpty() || (budgetSwitch.isChecked() &&
                (dailyBudget.isEmpty() || weeklyBudget.isEmpty() || monthlyBudget.isEmpty() || yearlyBudget.isEmpty()))) {
            Toast.makeText(this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if category already exists
        firebaseDatabase.getReference("Users")
                .child(uid)
                .child("UserCategories")
                .orderByChild("name")
                .equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(CategoriesActivity.this, "Category already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Category doesn't exist, proceed to save
                            createNewCategory(uid, name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CategoriesActivity.this, "Error checking category: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewCategory(String uid, String name) {

        String dailyBudget = mCategoryDailyBudget.getText().toString().trim();
        String weeklyBudget = mCategoryWeeklyBudget.getText().toString().trim();
        String monthlyBudget = mCategoryMonthlyBudget.getText().toString().trim();
        String yearlyBudget = mCategoryYearlyBudget.getText().toString().trim();
        String categoryNote = mCategoryNote.getText().toString().trim();

        // Prepare the category data
        Map<String, Object> category = new HashMap<>();
        category.put("name", name);
        category.put("dailyBudget", dailyBudget);
        category.put("weeklyBudget", weeklyBudget);
        category.put("monthlyBudget", monthlyBudget);
        category.put("yearlyBudget", yearlyBudget);
        category.put("note", categoryNote);

        // Save the new category
        firebaseDatabase.getReference("Users")
                .child(uid)
                .child("UserCategories")
                .push()
                .setValue(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CategoriesActivity.this, "Category saved successfully", Toast.LENGTH_SHORT).show();
                    clearFieldsAndExit();
                })
                .addOnFailureListener(e -> Toast.makeText(CategoriesActivity.this, "Error saving category: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupSwitch() {
        budgetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int visibility = isChecked ? View.VISIBLE : View.GONE;
                mLinearDaily.setVisibility(visibility);
                mLinearWeekly.setVisibility(visibility);
                mLinearMonthly.setVisibility(visibility);
                mLinearYearly.setVisibility(visibility);
            }
        });
        // Set the initial state of the switch and associated views
        budgetSwitch.setChecked(false);
        mLinearDaily.setVisibility(View.GONE);
        mLinearWeekly.setVisibility(View.GONE);
        mLinearMonthly.setVisibility(View.GONE);
        mLinearYearly.setVisibility(View.GONE);
    }

    private void clearFieldsAndExit() {
        mCategoryName.setText("");
        mCategoryDailyBudget.setText("");
        mCategoryWeeklyBudget.setText("");
        mCategoryMonthlyBudget.setText("");
        mCategoryYearlyBudget.setText("");
        budgetSwitch.setChecked(false);
    }
}
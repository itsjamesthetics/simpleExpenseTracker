package com.jamesaldteves.test2;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** @noinspection ALL*/
public class TransactionActivity extends AppCompatActivity {

    private EditText mTransactionAmount, mTransactionNote, mTransactionDate;
    private Spinner categorySpinner;
    private Button mTransactionCancel, mTransactionSave;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        initializeViews();
        setupEventListeners();
        fetchCategories();

    }

    private void initializeViews() {
        mTransactionAmount = findViewById(R.id.transaction_amount);
        mTransactionNote = findViewById(R.id.trans_note);
        mTransactionDate = findViewById(R.id.transaction_date);
        categorySpinner = findViewById(R.id.transaction_category_spinner);
        mTransactionCancel = findViewById(R.id.btn_transactioncancel);
        mTransactionSave = findViewById(R.id.btn_transactionsave);
        setCurrentDate();
    }

    private void setupEventListeners() {
        mTransactionDate.setOnClickListener(this::showDatePickerDialog);
        mTransactionSave.setOnClickListener(v -> saveTransaction());
        mTransactionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
                TransactionActivity.super.onBackPressed();
            }
        });
    }
    private void fetchCategories() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(uid)
                    .child("UserCategories")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<String> categories = new ArrayList<>();
                            categories.add("General Expenses");
                            for (DataSnapshot categorySnapshot: dataSnapshot.getChildren()) {
                                String categoryName = categorySnapshot.child("name").getValue(String.class);
                                if (categoryName != null) {
                                    categories.add(categoryName);
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(TransactionActivity.this, android.R.layout.simple_spinner_item, categories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categorySpinner.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(TransactionActivity.this, "Error fetching category data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTransaction() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        String amount = mTransactionAmount.getText().toString();
        String note = mTransactionNote.getText().toString();
        String date = mTransactionDate.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();

        if (amount.isEmpty() || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("amount", amount);
        transactionData.put("note", note);
        transactionData.put("date", date);
        transactionData.put("category", category);

        firebaseDatabase.getReference("Users")
                .child(userId)
                .child("Transactions")
                .push()
                .setValue(transactionData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TransactionActivity.this, "CTransaction saved successfully", Toast.LENGTH_SHORT).show();
                    clearFields(); // Clear fields after successful input
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        mTransactionAmount.setText("");
        mTransactionNote.setText("");
        mTransactionDate.setText("");
        if (categorySpinner.getAdapter() != null) {
            categorySpinner.setSelection(0);
        }
    }

    private void showDatePickerDialog(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, this::onDateSet, year, month, day).show();
    }

    private void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
        mTransactionDate.setText(selectedDate);
    }

    private void setCurrentDate() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        mTransactionDate.setText(currentDate);
    }
}
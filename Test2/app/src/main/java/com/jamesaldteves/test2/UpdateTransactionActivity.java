package com.jamesaldteves.test2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** @noinspection ALL*/
public class UpdateTransactionActivity extends AppCompatActivity {

    private EditText mTAmount, mTDate, mTNote;
    private Spinner mTSpinner;
    private Button TCancel, TUpdate;
    private FirebaseDatabase firebaseDatabase;
    private String TransactionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mTAmount = findViewById(R.id.update_transaction_amount);
        mTDate = findViewById(R.id.update_transaction_date);
        mTNote = findViewById(R.id.update_trans_note);
        mTSpinner = findViewById(R.id.update_transaction_category_spinner); // Ensure this ID matches your layout
        TCancel = findViewById(R.id.btn_transactioncancelupdate);
        TUpdate = findViewById(R.id.btn_transactionupdate);

        // Get the transaction ID from the intent
        TransactionID = getIntent().getStringExtra("TRANSACTION_ID");
        if (TransactionID != null) {
            loadTransactionData(TransactionID);
        }

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        TUpdate.setOnClickListener(v -> updateTransaction());
        TCancel.setOnClickListener(v -> finish());
    }

    private void updateTransaction() {
        String amount = mTAmount.getText().toString();
        String date = mTDate.getText().toString();
        String note = mTNote.getText().toString();
        String category = (String) mTSpinner.getSelectedItem();

        if (amount.isEmpty() || date.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference transactionRef = firebaseDatabase.getReference("Transactions").child(TransactionID);
        transactionRef.child("amount").setValue(amount);
        transactionRef.child("date").setValue(date);
        transactionRef.child("note").setValue(note);
        transactionRef.child("category").setValue(category).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateTransactionActivity.this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(UpdateTransactionActivity.this, "Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTransactionData(String transactionID) {
        DatabaseReference transactionRef = firebaseDatabase.getReference("Transactions").child(transactionID);

        transactionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Transaction transaction = dataSnapshot.getValue(Transaction.class);
                if (transaction != null) {
                    mTAmount.setText(transaction.getAmount());
                    mTDate.setText(transaction.getDate());
                    mTNote.setText(transaction.getNote());
                    // Set the spinner selection
                    // This assumes that your spinner adapter is correctly set up with category names
                    setSpinnerSelection(mTSpinner, transaction.getCategory());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateTransactionActivity.this, "Failed to load transaction data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String category) {
        // Implement logic to set the spinner's selection based on the category
        // This depends on how your spinner's adapter is set up
    }

    // Define your Transaction model class here or in a separate file
    public static class Transaction {
        private String amount;
        private String date;
        private String note;
        private String category; // Add a field for category

        public Transaction() {
            // Default constructor required for Firebase
        }

        // Getters
        public String getAmount() { return amount; }
        public String getDate() { return date; }
        public String getNote() { return note; }
        public String getCategory() { return category; } // Add getter for category

        // Setters (if needed)
        // ...
    }
}
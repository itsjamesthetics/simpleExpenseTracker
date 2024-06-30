package com.jamesaldteves.test2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** @noinspection ALL*/
public class BillremindersActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText mDescription, mBillAmount;
    private Button mBillCancel, mBillSave;
    private String selectedDate;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billreminders);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to set reminders", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        calendarView = findViewById(R.id.calendarView);
        mDescription = findViewById(R.id.bill_description);
        mBillAmount = findViewById(R.id.bill_amount);
        mBillCancel = findViewById(R.id.btn_billcancel);
        mBillSave = findViewById(R.id.btn_billsave);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                selectedDate = dateFormat.format(new Date(year - 1900, month, dayOfMonth));
            }
        });

        mBillSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = mDescription.getText().toString();
                String amount = mBillAmount.getText().toString();
                if (description.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(BillremindersActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkForExistingBillAndSave(description, amount, selectedDate);
                Bill bill = new Bill(description, amount);
                saveBillToFirebase(bill, selectedDate);
                setReminder(selectedDate);
            }
        });

        mBillCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
                BillremindersActivity.super.onBackPressed();
            }
        });
    }

    private void checkForExistingBillAndSave(final String description, final String amount, final String dateStr) {
        String userId = mAuth.getCurrentUser().getUid();
        firebaseDatabase.getReference("Users")
                .child(userId)
                .child("BillReminders")
                .child(dateStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Entry already exists for this date
                            Toast.makeText(BillremindersActivity.this, "A bill reminder for this date already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            // No entry exists, save new bill reminder
                            saveBillToFirebase(new Bill(description, amount), dateStr);
                            setReminder(dateStr);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(BillremindersActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveBillToFirebase(Bill bill, String date) {
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> billData = new HashMap<>();
        billData.put("description", bill.getDescription());
        billData.put("amount", bill.getAmount());

        firebaseDatabase.getReference("Users")
                .child(userId)
                .child("BillReminders")
                .push()
                .child(date)
                .setValue(billData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BillremindersActivity.this, "CBill saved and reminder set successfully", Toast.LENGTH_SHORT).show();
                    clearFields(); // Clear fields after successful input
                })
                .addOnFailureListener(e -> Toast.makeText(BillremindersActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
    private void setReminder(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            if (date == null) return;

            Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
            intent.putExtra("bill_description", mDescription.getText().toString());
            int requestCode = (int) (date.getTime() / 1000);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);

            Toast.makeText(this, "Reminder set for: " + dateStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to set reminder: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void clearFields() {
        mDescription.setText("");
        mBillAmount.setText("");
    }

    static class Bill {
        private String description;
        private String amount;

        public Bill() {
            // Firestore deserialization
        }

        public Bill(String description, String amount) {
            this.description = description;
            this.amount = amount;
        }

        // Getters
        public String getDescription() {
            return description;
        }

        public String getAmount() {
            return amount;
        }
    }
}

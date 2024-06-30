package com.jamesaldteves.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

/** @noinspection ALL*/
public class ViewSummaryActivity extends AppCompatActivity {


    // UI Components
    private Spinner summaryCategorySpinner;
    private TextView spentDaily, dailyBudget, dailyRatio;
    private TextView spentWeekly, weeklyBudget, weeklyRatio;
    private TextView spentMonthly, monthlyBudget, monthlyRatio;
    private TextView spentYearly, yearlyBudget, yearlyRatio;

    // Data Handling
    private ArrayAdapter<String> adapter;
    private List<String> categoryNames;
    private List<TransactionModel> transactionList;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener currentTransactionListener;

    // Selected Data
    private String selectedCategory;
    private double currentDailyBudget, currentWeeklyBudget, currentMonthlyBudget, currentYearlyBudget;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summary);

        initializeViews();
        setupFirebase();
        setupSpinner();
        loadCategoryNames();
    }

    private void initializeViews() {
        summaryCategorySpinner = findViewById(R.id.summary_category_spinner);
        spentDaily = findViewById(R.id.spentDaily);
        dailyBudget = findViewById(R.id.dailyBudget);
        dailyRatio = findViewById(R.id.dailyRatio);
        spentWeekly = findViewById(R.id.spentWeekly);
        weeklyBudget = findViewById(R.id.weeklyBudget);
        weeklyRatio = findViewById(R.id.weeklyRatio);
        spentMonthly = findViewById(R.id.spentMonthly);
        monthlyBudget = findViewById(R.id.monthlyBudget);
        monthlyRatio = findViewById(R.id.monthlyRatio);
        spentYearly = findViewById(R.id.spentYearly);
        yearlyBudget = findViewById(R.id.yearlyBudget);
        yearlyRatio = findViewById(R.id.yearlyRatio);
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        categoryNames = new ArrayList<>();
        transactionList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        summaryCategorySpinner.setAdapter(adapter);
    }

    private void setupSpinner() {
        // Setup spinner item selection handling
        summaryCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoryNames.get(position);
                fetchBudgetsAndTransactions(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
    }

    private void fetchBudgetsAndTransactions(String selectedCategory) {
        // Fetch budgets and transactions for the selected category
        fetchCurrentDailyBudget(selectedCategory);
        fetchCurrentWeeklyBudget(selectedCategory);
        fetchCurrentMonthlyBudget(selectedCategory);
        fetchCurrentYearlyBudget(selectedCategory);
    }

    private void loadCategoryNames() {
        // Load category names from Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userCategoriesRef = firebaseDatabase.getReference("Users").child(userId).child("UserCategories");
            userCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    categoryNames.clear();
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryName = categorySnapshot.child("name").getValue(String.class);
                        if (categoryName != null && !categoryName.isEmpty()) {
                            categoryNames.add(categoryName);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewSummaryActivity.this, "Error loading categories: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchTransactionsForCategory(String selectedCategory) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference transactionsRef = firebaseDatabase.getReference("Users").child(userId).child("Transactions");

            // Remove any existing listener
            if (currentTransactionListener != null) {
                transactionsRef.removeEventListener(currentTransactionListener);
            }

            // Create a new listener
            currentTransactionListener = new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    transactionList.clear();
                    double totalSpentDaily = 0;
                    double totalSpentWeekly = 0;
                    double totalSpentMonthly = 0;
                    double totalSpentYearly = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TransactionModel transaction = snapshot.getValue(TransactionModel.class);
                        if (transaction != null) {
                            double amount = Double.parseDouble(transaction.getAmount());
                            if (isTransactionForToday(transaction.getDate())) {
                                totalSpentDaily += amount;
                            }
                            if (isTransactionInCurrentWeek(transaction.getDate())) {
                                totalSpentWeekly += amount;
                            }
                            if (isTransactionInCurrentMonth(transaction.getDate())) {
                                totalSpentMonthly += amount;
                            }
                            if (isTransactionInCurrentYear(transaction.getDate())) {
                                totalSpentYearly += amount;
                            }
                        }
                    }
                    updateDailyTotals(totalSpentDaily);
                    updateWeeklyTotals(totalSpentWeekly);
                    updateMonthlyTotals(totalSpentMonthly);
                    updateYearlyTotals(totalSpentYearly);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ViewSummaryActivity.this, "Error loading transactions: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

            // Add the new listener
            transactionsRef.orderByChild("category").equalTo(selectedCategory).addValueEventListener(currentTransactionListener);
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isTransactionForToday(String transactionDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        return todayDate.equals(transactionDate);
    }

    private boolean isTransactionInCurrentWeek(String transactionDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(transactionDate);
            Calendar transactionCalendar = Calendar.getInstance();
            transactionCalendar.setTime(date);

            Calendar currentCalendar = Calendar.getInstance();
            transactionCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);

            return transactionCalendar.get(Calendar.WEEK_OF_YEAR) == currentCalendar.get(Calendar.WEEK_OF_YEAR) &&
                    transactionCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isTransactionInCurrentMonth(String transactionDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(transactionDate);
            Calendar transactionCalendar = Calendar.getInstance();
            transactionCalendar.setTime(date);

            Calendar currentCalendar = Calendar.getInstance();
            return transactionCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                    transactionCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isTransactionInCurrentYear(String transactionDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(transactionDate);
            Calendar transactionCalendar = Calendar.getInstance();
            transactionCalendar.setTime(date);

            Calendar currentCalendar = Calendar.getInstance();
            return transactionCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void fetchCurrentDailyBudget(String category) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userCategoryRef = firebaseDatabase.getReference("Users").child(userId).child("UserCategories");
            userCategoryRef.orderByChild("name").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        CategoryModel categoryModel = categorySnapshot.getValue(CategoryModel.class);
                        if (categoryModel != null && categoryModel.getDailyBudget() != null) {
                            try {
                                currentDailyBudget = Double.parseDouble(categoryModel.getDailyBudget());
                                fetchTransactionsForCategory(selectedCategory); // Fetch transactions after budget
                            } catch (NumberFormatException e) {
                                Toast.makeText(ViewSummaryActivity.this, "Error parsing daily budget", Toast.LENGTH_LONG).show();
                            }
                            break; // Assuming only one category matches the name
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewSummaryActivity.this, "Error loading budget: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void fetchCurrentWeeklyBudget(String category) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userCategoryRef = firebaseDatabase.getReference("Users").child(userId).child("UserCategories");
            userCategoryRef.orderByChild("name").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        CategoryModel categoryModel = categorySnapshot.getValue(CategoryModel.class);
                        if (categoryModel != null && categoryModel.getWeeklyBudget() != null) {
                            try {
                                currentWeeklyBudget = Double.parseDouble(categoryModel.getWeeklyBudget());
                                fetchTransactionsForCategory(selectedCategory);
                            } catch (NumberFormatException e) {
                                Toast.makeText(ViewSummaryActivity.this, "Error parsing weekly budget", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewSummaryActivity.this, "Error loading budget: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void fetchCurrentMonthlyBudget(String category) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userCategoryRef = firebaseDatabase.getReference("Users").child(userId).child("UserCategories");
            userCategoryRef.orderByChild("name").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        CategoryModel categoryModel = categorySnapshot.getValue(CategoryModel.class);
                        if (categoryModel != null && categoryModel.getMonthlyBudget() != null) {
                            try {
                                currentMonthlyBudget = Double.parseDouble(categoryModel.getMonthlyBudget());
                                fetchTransactionsForCategory(selectedCategory);
                            } catch (NumberFormatException e) {
                                Toast.makeText(ViewSummaryActivity.this, "Error parsing monthly budget", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewSummaryActivity.this, "Error loading budget: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void fetchCurrentYearlyBudget(String category) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userCategoryRef = firebaseDatabase.getReference("Users").child(userId).child("UserCategories");
            userCategoryRef.orderByChild("name").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        CategoryModel categoryModel = categorySnapshot.getValue(CategoryModel.class);
                        if (categoryModel != null && categoryModel.getYearlyBudget() != null) {
                            try {
                                currentYearlyBudget = Double.parseDouble(categoryModel.getYearlyBudget());
                                fetchTransactionsForCategory(selectedCategory);
                            } catch (NumberFormatException e) {
                                Toast.makeText(ViewSummaryActivity.this, "Error parsing yearly budget", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewSummaryActivity.this, "Error loading budget: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateDailyTotals(double totalSpentDaily) {
        dailyBudget.setText(String.format(Locale.getDefault(), "₱%.2f", currentDailyBudget));

        if (currentDailyBudget > 0) {
            double ratio = (totalSpentDaily / currentDailyBudget) * 100;
            dailyRatio.setText(String.format(Locale.getDefault(), "%.2f%%", ratio));
        } else {
            dailyRatio.setText("No budget set");
        }

        spentDaily.setText(String.format(Locale.getDefault(), "₱%.2f", totalSpentDaily));
    }

    @SuppressLint("SetTextI18n")
    private void updateWeeklyTotals(double totalSpentWeekly) {
        spentWeekly.setText(String.format(Locale.getDefault(), "₱%.2f", totalSpentWeekly));
        weeklyBudget.setText(String.format(Locale.getDefault(), "₱%.2f", currentWeeklyBudget));

        if (currentWeeklyBudget > 0) {
            double ratio = (totalSpentWeekly / currentWeeklyBudget) * 100;
            weeklyRatio.setText(String.format(Locale.getDefault(), "%.2f%%", ratio));
        } else {
            weeklyRatio.setText("No budget set");
        }
    }

    private void updateMonthlyTotals(double totalSpentMonthly) {
        monthlyBudget.setText(String.format(Locale.getDefault(), "₱%.2f", currentMonthlyBudget));

        if (currentMonthlyBudget > 0) {
            double ratio = (totalSpentMonthly / currentMonthlyBudget) * 100;
            monthlyRatio.setText(String.format(Locale.getDefault(), "%.2f%%", ratio));
        } else {
            monthlyRatio.setText("No budget set");
        }

        spentMonthly.setText(String.format(Locale.getDefault(), "₱%.2f", totalSpentMonthly));
    }

    @SuppressLint("SetTextI18n")
    private void updateYearlyTotals(double totalSpentYearly) {
        yearlyBudget.setText(String.format(Locale.getDefault(), "₱%.2f", currentYearlyBudget));

        if (currentYearlyBudget > 0) {
            double ratio = (totalSpentYearly / currentYearlyBudget) * 100;
            yearlyRatio.setText(String.format(Locale.getDefault(), "%.2f%%", ratio));
        } else {
            yearlyRatio.setText("No budget set");
        }
        spentYearly.setText(String.format(Locale.getDefault(), "₱%.2f", totalSpentYearly));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listener to avoid memory leaks
        if (currentTransactionListener != null) {
            DatabaseReference transactionsRef = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Transactions");
            transactionsRef.removeEventListener(currentTransactionListener);
        }
    }



}

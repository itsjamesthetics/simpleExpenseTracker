package com.jamesaldteves.test2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ViewCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryModel> categoryList;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);


        setupUI();
        firebaseDatabase = FirebaseDatabase.getInstance();
        loadCategories();

    }

    private void setupUI() {
        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);
    }
    private void onCategoryClicked(CategoryModel category) {
        if (category != null) {
            // Logic to handle the category click
            // Example: Navigate to an update activity
            Intent intent = new Intent(this, UpdateCategoryActivity.class);
            startActivity(intent);
        }
    }


    private void loadCategories() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseDatabase.getReference("Users").child(userId).child("UserCategories")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categoryList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CategoryModel category = snapshot.getValue(CategoryModel.class);
                            if (category != null) {
                                categoryList.add(category);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ViewCategoryActivity.this, "Error loading categories: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
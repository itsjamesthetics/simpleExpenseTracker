package com.jamesaldteves.test2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ViewOutputActivity extends AppCompatActivity {

    private CardView mTView, mBView, mCategoryView, mSummaryView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_output);

        mSummaryView = findViewById(R.id.view_Summary);
        mTView = findViewById(R.id.view_transactions);
        mBView = findViewById(R.id.backPressed);
        mCategoryView = findViewById(R.id.view_categories);

        mTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewTransactionActivity.class));
            }
        });

        mCategoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewCategoryActivity.class));
            }
        });

        mSummaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewSummaryActivity.class));
            }
        });

        mBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewOutputActivity.super.onBackPressed();
                finish();
            }
        });


    }
}
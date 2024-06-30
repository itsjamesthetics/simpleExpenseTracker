package com.jamesaldteves.test2;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
import retrofit2.*;

import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    Call<List<User>> call = RetrofitClient.getInstance().getApi().getUsers();
    call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();
                // Do something with the users
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle failure
            }
    });
}
package com.jamesaldteves.test2;

import java.util.*;
import retrofit2.*;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import org.chromium.base.Callback;

/** @noinspection ALL*/


public class MainActivity extends AppCompatActivity implements OnSuccessListener<AuthResult> {

    private EditText mPass, mEmail;
    private Button btnLogin;
    private TextView mSignup, mForgetPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //Functions
        loginDetails ();

    }

    private void loginDetails (){

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mForgetPassword = findViewById(R.id.forget_password);
        mSignup = findViewById(R.id.signup_here);


        btnLogin.setOnClickListener(view -> {

            String email = mEmail.getText().toString().trim();
            String pass = mPass.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(MainActivity.this, "Email and Password are required", Toast.LENGTH_SHORT).show();
            }
            else if (email.length() < 6 || pass.length() < 6) {
                Toast.makeText(MainActivity.this, "Email and Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                                //Login is successful
                                Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                clearInputFields();
                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        }
                        else {
                            //Login Failed
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                            Toast.makeText(MainActivity.this, "Unsuccessful login: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        mForgetPassword.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ResetActivity.class)));
        mSignup.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignupActivity.class)));
    }
    private void clearInputFields() {
        mEmail.setText(null);
        mPass.setText(null);
    }
    @Override
    public void onSuccess(AuthResult authResult) {}

    Call<List<User>> call = RetrofitClient.getInstance().getApi().getUsers();

}


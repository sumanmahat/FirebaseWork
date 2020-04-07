package com.example.firebaseauthentication.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseauthentication.MainActivity;
import com.example.firebaseauthentication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button btnlogin;
    TextView forgetPass, createAcc;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login Here");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnlogin = findViewById(R.id.loginBtn);
        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);

        fAuth = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uEmail = email.getText().toString();
                String upassword = password.getText().toString();

                if (uEmail.isEmpty() || upassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.signInWithEmailAndPassword(uEmail, upassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Login succesfull", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Login not success" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}

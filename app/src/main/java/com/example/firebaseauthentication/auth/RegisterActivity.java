package com.example.firebaseauthentication.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseauthentication.MainActivity;
import com.example.firebaseauthentication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    EditText username, userEmail, userPass, conPass;
    Button btnCreate;
    TextView txtlogin;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPass = findViewById(R.id.password);
        conPass = findViewById(R.id.passwordConfirm);
        btnCreate = findViewById(R.id.createAccount);
        txtlogin = findViewById(R.id.login);
        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        progressBar = findViewById(R.id.progressBar4);

        fAuth = FirebaseAuth.getInstance();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uUserName = username.getText().toString();
                String uUserEmail = userEmail.getText().toString();
                String uUserPassword = userPass.getText().toString();
                String uUserConP = conPass.getText().toString();

                if (uUserName.isEmpty() || uUserEmail.isEmpty() || uUserPassword.isEmpty() || uUserConP.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!uUserPassword.equals(uUserConP)){
                    conPass.setError("Password do not match");
                }

                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(uUserEmail,uUserPassword);
                fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterActivity.this, "Note are saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        //to save user's full name
                        FirebaseUser user = fAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uUserName)
                                .build();
                        user.updateProfile(request);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });


            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}

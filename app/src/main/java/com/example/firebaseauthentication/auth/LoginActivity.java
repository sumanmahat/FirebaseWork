package com.example.firebaseauthentication.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText lemail,lpassword;
    Button btnlogin;
    TextView forgetPass, createAcc;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login Here");

        lemail = findViewById(R.id.email);
        lpassword = findViewById(R.id.lPassword);
        btnlogin = findViewById(R.id.loginBtn);
        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);
        spinner =findViewById(R.id.progressBar3);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        
        showWarning();


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uEmail = lemail.getText().toString();
                String upassword = lpassword.getText().toString();

                if (uEmail.isEmpty() || upassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                spinner.setVisibility(View.VISIBLE);

                //delete note first
                if (fAuth.getCurrentUser().isAnonymous()){
                    FirebaseUser user = fAuth.getCurrentUser();
                    fStore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "All temp notes are deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //to delete user
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Temp user delted", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                fAuth.signInWithEmailAndPassword(uEmail, upassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Login succesfull", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Login not success" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }
                });
            }
        });
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });


    }

    private void showWarning() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Linking Existing Account will delete all the temp notes. Create new account to save them")
                .setPositiveButton("Save Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                        finish();
                    }
                }).setNegativeButton("It's ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing
                    }
                });

        warning.show();
    }
}

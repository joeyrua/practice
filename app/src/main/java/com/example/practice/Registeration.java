package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registeration extends AppCompatActivity {
    TextInputLayout  regUsername, regEmail, regPhoneNo, regPassword;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    Button GOTO_LOGIN_BTN;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        regUsername = findViewById(R.id.username);
        regEmail = findViewById(R.id.email);
        regPhoneNo = findViewById(R.id.phoneNo);
        regPassword = findViewById(R.id.password);
        GOTO_LOGIN_BTN = findViewById(R.id.to_login);
        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),UserProfile.class));
            finish();
        }

        GOTO_LOGIN_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(Registeration.this,login.class));
                finish();
            }
        });

    }

    private Boolean _username() {
        String Username = regUsername.getEditText().getText().toString();
        if (Username.isEmpty()) {
            regUsername.setError("本欄位未填寫");
            return false;
        } else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean _email() {
        String email = regEmail.getEditText().getText().toString();
        if (email.isEmpty()) {
            regEmail.setError("本欄位未填寫");
            return false;
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean _PhoneNo() {
        String PhoneNo = regPhoneNo.getEditText().getText().toString();
        if (PhoneNo.isEmpty()) {
            regPhoneNo.setError("本欄位未填寫");
            return false;
        } else {
            regPhoneNo.setError(null);
            regPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean _Password() {
        String PhoneNo = regPassword.getEditText().getText().toString();
        if (PhoneNo.isEmpty()) {
            regPassword.setError("本欄位未填寫");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    //Save data in FireBase on button click
    public void re(View v) {
        if (!_username() | !_PhoneNo() | !_Password() | !_email() ) {
            return;
        }


        //Get all the values
        String username = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String phoneNo = regPhoneNo.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();



        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                userID = firebaseAuth.getCurrentUser().getUid();
                reference = FirebaseDatabase.getInstance().getReference();
                UserHelperClass helperClass = new UserHelperClass(username, email, phoneNo, password);
                reference.child("users").child(userID).setValue(helperClass);
                Toast.makeText(Registeration.this,"Tour Account has been created successfully!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Registeration.this, UserProfile.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registeration.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
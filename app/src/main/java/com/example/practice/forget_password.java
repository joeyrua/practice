package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class forget_password extends AppCompatActivity {
    TextInputLayout to_email;
    Button send_email ,back_login;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        to_email = findViewById(R.id.to_email);
        send_email = findViewById(R.id.send_email);
        back_login = findViewById(R.id.back_login);

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");

        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (to_email.getEditText().getText().toString().isEmpty()) {
                    to_email.setError("Not Email Address");
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(to_email.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(forget_password.this, "Password send to your email", Toast.LENGTH_LONG).show();
                            startActivity(new Intent().setClass(forget_password.this,login.class));
                            finish();
                        } else {
                            Toast.makeText(forget_password.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(forget_password.this,login.class));
                finish();
            }
        });
    }

    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(KeyCode, event);
    }
}
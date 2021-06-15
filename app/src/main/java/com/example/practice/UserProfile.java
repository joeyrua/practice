package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserProfile extends AppCompatActivity {
    TextInputLayout Username,email,phoneNO, password;
    Button update,verifyEmail;
    ImageView user_img;
    String _USERNAME,_EMAIL,_PHONENO, _PASSWORD,user_img_id,user_id;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseAuth auth;
    Button logout_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Username= findViewById(R.id.username_profile);
        email= findViewById(R.id.email_profile);
        phoneNO = findViewById(R.id.phoneNo_profile);
        password = findViewById(R.id.password_profile);
        verifyEmail = findViewById(R.id.email_ver);
        update = findViewById(R.id.update_profile);
        logout_btn = findViewById(R.id.logout_id);
        user_img = findViewById(R.id.user_img);
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();


        showAllUserData();

        if (!auth.getCurrentUser().isEmailVerified()) {
            verifyEmail.setVisibility(View.VISIBLE);
        }

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),login.class));
                finish();
            }
        });

        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserProfile.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                        verifyEmail.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void showAllUserData() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                _USERNAME = snapshot.child(user_id).child("username").getValue(String.class);
                _EMAIL = snapshot.child(user_id).child("email").getValue(String.class);
                _PHONENO = snapshot.child(user_id).child("phoneNo").getValue(String.class);
                _PASSWORD = snapshot.child(user_id).child("password").getValue(String.class);
                //user_img_id = snapshot.child(user_id).child("image_id").getValue(String.class);
                download_img();
                Username.getEditText().setText(_USERNAME);
                email.getEditText().setText(_EMAIL);
                phoneNO.getEditText().setText(_PHONENO);
                password.getEditText().setText(_PASSWORD);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                
            }
        });
    }

    public void download_img(){
        StorageReference imageRef2 = storageReference.child("Images/1623492912521.png");

        imageRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Load image from uri using any third party sdk
                Glide.with(UserProfile.this)
                        .load(uri)
                        .into(user_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    public void update(View v) {
        reference.child(user_id).child("username").setValue( Username.getEditText().getText().toString());
        _USERNAME = Username.getEditText().getText().toString();

        reference.child(user_id).child("password").setValue(password.getEditText().getText().toString());
        _PASSWORD = password.getEditText().getText().toString();


        reference.child(user_id).child("email").setValue(email.getEditText().getText().toString());
        _EMAIL = email.getEditText().getText().toString();

        FirebaseUser user = auth.getCurrentUser();
        user.updateEmail(email.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UserProfile.this,"Email Update Success",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        user.updatePassword(password.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UserProfile.this, "Password Update Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        reference.child(user_id).child("phoneNo").setValue(phoneNO.getEditText().getText().toString());
        _PHONENO = phoneNO.getEditText().getText().toString();
    }


    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(KeyCode, event);
    }


    /*private boolean isPhoneNoChange() { //備用
        if (!_PHONENO.equals(phoneNO.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("phoneNo").setValue(phoneNO.getEditText().getText().toString());
            _PHONENO = phoneNO.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }*/

}
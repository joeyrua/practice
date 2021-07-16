package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;


public class login extends AppCompatActivity {
    Button login_btn,to_register_btn,to_forget_password_btn;
    TextInputLayout email, password;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressBar login_pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn = findViewById(R.id.login_go);
        to_register_btn = findViewById(R.id.to_signup);
        to_forget_password_btn = findViewById(R.id.forget_password);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password);
        login_pro = findViewById(R.id.login_progress);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");

            boolean externalHasGone = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ==PackageManager.PERMISSION_GRANTED;

            boolean cameraHasGone = checkSelfPermission(Manifest.permission.CAMERA)
                    ==PackageManager.PERMISSION_GRANTED;

        String[] permissions;

        if(!cameraHasGone && !externalHasGone){//如果存取權限和相機未取得
            permissions = new String[2];
            permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            permissions[1] = Manifest.permission.CAMERA;
        }
        else if(!cameraHasGone){//如果相機未取得
            permissions = new String[1];
            permissions[0] = Manifest.permission.CAMERA;
        }
        else if(!externalHasGone){//如果存取權限未取得
            permissions = new String[1];
            permissions[0]= Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }
        else{
            //Toast.makeText(login.this,"相機權限已取得\n儲存權限已取得",Toast.LENGTH_SHORT).show();

            login_btn.setOnClickListener(v -> {
                if (!vaildateEmail() | !vaildatePassword()) {
                    login_pro.setVisibility(View.GONE);
                    return;
                } else {
                    login_pro.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //isUser();
                                Toast.makeText(login.this, "Login Complete", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent().setClass(getApplicationContext(),UserProfile.class));
                                finish();
                            } else if(connect_network()) {
                                    login_pro.setVisibility(View.GONE);
                                    return;
                            }
                        }
                    });
                }
            });

            to_register_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!connect_network()){
                        return;
                    }
                    else {
                        Intent intent = new Intent(login.this, Registeration.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            to_forget_password_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!connect_network()){
                        return;
                    }
                    else {
                        Intent intent = new Intent(login.this, forget_password.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });

            return;
        }
        requestPermissions(permissions,100);


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ConfirmExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void ConfirmExit() {
        AlertDialog.Builder ad = new AlertDialog.Builder(login.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開嗎?");
        ad.setPositiveButton("是", (dialog, which) -> System.exit(0));
        ad.setNegativeButton("否", (dialog, which) -> {

        });
        ad.setCancelable(false);//禁用返回
        ad.show();
    }

    private  Boolean vaildateEmail() {
        String val = Objects.requireNonNull(email.getEditText()).getText().toString();
        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }
    private  Boolean vaildatePassword() {
        String val = Objects.requireNonNull(password.getEditText()).getText().toString();

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }


    private Boolean connect_network(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }
        else{
            AlertDialog.Builder no_connect = new AlertDialog.Builder(this);
            no_connect.setMessage("你沒有網路沒連線");
            no_connect.setPositiveButton("確定", (dialog, which) -> {

            });
            no_connect.setCancelable(false);
            no_connect.show();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StringBuilder word = new StringBuilder();
        switch (permissions.length){
            case 1:
                if(permissions[0].equals(Manifest.permission.CAMERA)){
                    word.append("相機權限");
                }
                else{
                    word.append("儲存權限");
                }
                if(grantResults[0] == 0){
                    word.append("已取得");
                }
                else{
                    word.append("未取得\n");
                }
                if(permissions[0].equals(Manifest.permission.CAMERA)){
                    word.append("儲存權限");
                }else{
                    word.append("相機權限");
                    word.append("已取得");
                }
                break;
            case 2:
                for(int i =0;i<permissions.length;i++){
                    if(permissions[i].equals(Manifest.permission.CAMERA)){
                        word.append("相機權限");
                    }
                    else{
                        word.append("儲存權限");
                    }
                    if(grantResults[i]==0){
                        word.append("已取得");
                    }
                    else{
                        word.append("未取得");
                    }
                    if(i<permissions.length-1) word.append("\n");
                }
                break;
        }
        Toast.makeText(login.this,word.toString(),Toast.LENGTH_SHORT).show();
    }

    /*private void isUser() {
        final String userEnteredEmail = email.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.child("email").equalTo(userEnteredEmail);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email.setError(null);
                    email.setErrorEnabled(false);
                    String passwordFromDB = snapshot.child(userEnteredEmail).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)) {
                        email.setError(null);
                        email.setErrorEnabled(false);
                        String nameFromDB = snapshot.child(userEnteredEmail).child("name").getValue(String.class);
                        String usernameFromDB = snapshot.child(userEnteredEmail).child("username").getValue(String.class);
                        String phoneNoFromDB = snapshot.child(userEnteredEmail).child("phoneNo").getValue(String.class);
                        String emailFromDB = snapshot.child(userEnteredEmail).child("email").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(),UserProfile.class);

                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("phoneNo", phoneNoFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                        finish();

                    } else {
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }

                } else {
                    email.setError("No such User exist");
                    email.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),UserProfile.class));
            finish();
        }

    }
}
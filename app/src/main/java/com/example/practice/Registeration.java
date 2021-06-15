package com.example.practice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class Registeration extends AppCompatActivity {
    TextInputLayout  regUsername, regEmail, regPhoneNo, regPassword;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    Button GOTO_LOGIN_BTN,register,SELECT_PICTURE;
    ImageView imageView;
    Uri img_uri;
    StorageTask uploadTask;
    StorageReference storageReference;
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
        register = findViewById(R.id.register);
        imageView = findViewById(R.id.image_view);
        SELECT_PICTURE = findViewById(R.id.sele_pic);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Images");


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

        //Save data in FireBase on button click
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_username() | !_PhoneNo() | !_Password() | !_email() ) {
                    return;
                }
                //Get all the values
                String username = regUsername.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String phoneNo = regPhoneNo.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String Image_id = System.currentTimeMillis()+"."+getExtension(img_uri);

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        userID = firebaseAuth.getCurrentUser().getUid();
                        reference = FirebaseDatabase.getInstance().getReference();
                        UserHelperClass helperClass = new UserHelperClass(username, email, phoneNo, password, Image_id);
                        reference.child("users").child(userID).setValue(helperClass);
                        Fileuploader();
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
        });

        SELECT_PICTURE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File_choose();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//圖片區
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData()!=null){
            img_uri = data.getData();
            imageView.setImageURI(img_uri);
        }
    }

    private void File_choose(){//選擇圖片
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private String getExtension(Uri uri){//上傳圖片前
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    
    private void Fileuploader(){//上傳圖片
        StorageReference Ref = storageReference.child(System.currentTimeMillis()+"."+getExtension(img_uri));

        // Register observers to listen for when the download is done or if it fails
        uploadTask=Ref.putFile(img_uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(Registeration.this,"Image Upload successfully",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean _username() {
        String Username = regUsername.getEditText().getText().toString();
        if (Username.isEmpty()) {
            regUsername.setError("本欄位未填寫");
            return false;
        }
        else if(Username.length()>15){
            regUsername.setError("必須大於15個字");
            return false;
        }
        else {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
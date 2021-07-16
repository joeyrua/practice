package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registeration extends AppCompatActivity {
    TextInputLayout  regUsername, regEmail, regPhoneNo, regPassword;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    Button GOTO_LOGIN_BTN,register,SELECT_PICTURE,Capture;
    ImageView imageView;
    Uri contentUri;
    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE=102;
    static final int Gallery_IMAGE_CODE = 105;
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
        Capture = findViewById(R.id.capture_btn);
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


                if (!_username() | !_PhoneNo() | !_Password() | !_email() | !_Img() ) {
                    return;
                }
                //Get all the values
                String username = regUsername.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String phoneNo = regPhoneNo.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_"+timeStamp+"."+getExtension(contentUri);


                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        userID = firebaseAuth.getCurrentUser().getUid();
                        reference = FirebaseDatabase.getInstance().getReference();
                        UserHelperClass helperClass = new UserHelperClass(username, email, phoneNo, password, imageFileName);
                        reference.child("users").child(userID).setValue(helperClass);

                            StorageReference Ref = storageReference.child(imageFileName);
                            uploadTask=Ref.putFile(contentUri).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(Registeration.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Intent intent = new Intent(Registeration.this, UserProfile.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

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
               Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               startActivityForResult(gallery,Gallery_IMAGE_CODE);
            }
        });

        Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//顯示(回傳)圖片
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_IMAGE_CODE && resultCode==RESULT_OK && data != null && data.getData()!=null){
            contentUri = data.getData();

            imageView.setImageURI(contentUri);
            //UploadImageToFirebase(imageFileName,contentUri);
        }
        else if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            File f = new File(currentPhotoPath);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//ADD IMAGE TO GALLERY
            contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            imageView.setImageURI(contentUri);
            //UploadImageToFirebase(f.getName(),contentUri);
        }
    }

    private void UploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child(name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Registeration.this,"Image Upload Successful",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registeration.this,"Image Upload Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getExtension(Uri uri){//上傳圖片前
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.file_provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private Boolean _username() {
        String Username = regUsername.getEditText().getText().toString();
        if (Username.isEmpty()) {
            regUsername.setError("本欄位未填寫");
            return false;
        }
        else if(Username.length()> 15){
            regUsername.setError("必須小於15個字");
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

    private Boolean _Img(){
        if(contentUri==null){
            Toast.makeText(Registeration.this,"請勿必選擇圖片",Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void progress(){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("跑進度中");
        dialog.setMax(120);
        dialog.show();
        new Thread(()->{
            for (int i = 0; i <120 ; i++) {
                /**更新進度*/
                dialog.setProgress(i);
                SystemClock.sleep(50);
            }
            dialog.dismiss();
        }).start();
    }
}
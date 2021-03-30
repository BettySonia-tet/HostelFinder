package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    EditText etFName, etLName, etPhoneNo, etEmail, etPass;
    Button btnSignUp;
    TextView txtLogin;
    FirebaseAuth fAuth;
    //private DatabaseReference ref;
    private CircleImageView profile_image;

    private ProgressDialog loader;
    private Uri resultUri;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);




        etFName = findViewById(R.id.etFName);
        etLName = findViewById(R.id.etLName);
        etPhoneNo = findViewById(R.id.etPhoneNo);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        profile_image = findViewById(R.id.profile_image);

        btnSignUp = findViewById(R.id.btnSignUp);

        txtLogin = findViewById(R.id.txtLogin);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });



        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

       btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
             final String fName, lName, phoneNO, email, password;
             email = etEmail.getText().toString().trim();
             password = etPass.getText().toString().trim();
             fName = etFName.getText().toString();
             lName = etLName.getText().toString();
             phoneNO = etPhoneNo.getText().toString();


             if (TextUtils.isEmpty(email)){
                 etEmail.setError("Email is required");
                 return;
             }

             if (TextUtils.isEmpty(password)){
                 etPass.setError("Password is required");
                 return;
             }

             if (password.length()<8){
                 etPass.setError("Password must be >=8 Characters");
                 return;
             }

             if (TextUtils.isEmpty(fName)){
                 etFName.setError("First name required");
             }

             if (TextUtils.isEmpty(lName)){
                 etLName.setError("Last name required");
             }
               if (resultUri ==null){
                   Toast.makeText(RegisterActivity.this, "Your profile Image is required!", Toast.LENGTH_SHORT).show();
                   return;
               }
             if (TextUtils.isEmpty(phoneNO)){
                 etPhoneNo.setError("Phone number is required");
             } else {
                 loader.setMessage("Creating user");
                 loader.show();
                 fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()){

                             //send verfication link
                             FirebaseUser fuser = fAuth.getCurrentUser();
                             fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent. " , Toast.LENGTH_SHORT).show();
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                                 }
                             });

                             ref =  FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                             HashMap<String, Object> map = new HashMap();
                             map.put("firstname", fName);
                             map.put("lastname", lName);
                             map.put("phone", phoneNO);
                             map.put("email", email);

                             ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                         Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                         startActivity(intent);
                                         finish();
                                     }else {
                                         Toast.makeText(RegisterActivity.this, "user could not be created "+task.getException().toString(),
                                                 Toast.LENGTH_SHORT).show();
                                     }
                                     loader.dismiss();
                                 }
                             });

                             if (resultUri !=null){
                                 final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile pictures").child(FirebaseAuth.getInstance().
                                         getCurrentUser().getUid());
                                 Bitmap bitmap = null;
                                 try {
                                     bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 }
                                 ByteArrayOutputStream byteArrayOutputStStream = new ByteArrayOutputStream();
                                 bitmap.compress(Bitmap.CompressFormat.JPEG, 20,byteArrayOutputStStream);
                                 byte[] data = byteArrayOutputStStream.toByteArray();
                                 UploadTask uploadTask = filepath.putBytes(data);

                                 uploadTask.addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         finish();
                                         return;
                                     }
                                 });

                                 uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                     @Override
                                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                         if (taskSnapshot.getMetadata() != null) {
                                             if (taskSnapshot.getMetadata().getReference() != null) {
                                                 Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                 result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                     @Override
                                                     public void onSuccess(Uri uri) {
                                                         String imageUrl = uri.toString();
                                                         Map newImageMap = new HashMap();
                                                         newImageMap.put("profilepictureurl", imageUrl);
                                                         ref.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                             @Override
                                                             public void onComplete(@NonNull Task task) {
                                                                 if (task.isSuccessful()){
                                                                     Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                 }else {
                                                                     String error = task.getException().toString();
                                                                     Toast.makeText(RegisterActivity.this, "Process failed "+ error, Toast.LENGTH_SHORT).show();
                                                                 }
                                                             }
                                                         });
                                                         finish();
                                                     }
                                                 });
                                             }
                                         }
                                     }
                                 });

                                 Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                 startActivity(intent);
                                 finish();
                                 loader.dismiss();
                             }

                         }
                     }
                 });
             }


           }
       });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profile_image.setImageURI(resultUri);
        }
    }
}
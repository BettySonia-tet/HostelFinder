package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private CircleImageView settingsProfileImage;
    private EditText settingsLastName, settingsFirstName, settingsPhone;
    private Button settingsUpdateDetailsButton,settingsBackButton;
    private EditText settingsEmail;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private String userID;

    private ProgressDialog loader;

    private String mName = "";
    private String mEmail = "";
    private String mPhoneNumber = "";
    private String mProfilePicture = "";
    private Uri resultUri;

    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        settingsProfileImage = findViewById(R.id.settingsProfileImage);
        settingsLastName  = findViewById(R.id.settingsLastName);
        settingsFirstName = findViewById(R.id.settingsFirstName);
        settingsPhone = findViewById(R.id.settingsPhone);
        settingsUpdateDetailsButton = findViewById(R.id.settingsUpdateDetailsButton);
        settingsBackButton = findViewById(R.id.settingsBackButton);
        settingsEmail = findViewById(R.id.settingsEmail);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        getUserInfo();

        settingsProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        settingsUpdateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInformation();
            }
        });


        settingsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyGoingBack();
            }
        });
    }


    private void getUserInfo(){
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("firstname") !=null){
                        mName = map.get("firstname").toString();
                        settingsFirstName.setText(mName);
                        settingsFirstName.setSelection(mName.length());
                    }
                    if (map.get("lastname") !=null){
                        mName = map.get("lastname").toString();
                        settingsLastName.setText(mName);
                        settingsLastName.setSelection(mName.length());
                    }
                    if (map.get("phone") !=null){
                        mEmail = map.get("phone").toString();
                        settingsPhone.setText(mEmail);
                    }
                    if (map.get("email") !=null){
                        mEmail = map.get("email").toString();
                        settingsEmail.setText(mEmail);
                    }

                    if (map.get("profilepictureurl") !=null){
                        mProfilePicture = map.get("profilepictureurl").toString();
                        Glide.with(getApplication()).load(mProfilePicture).into(settingsProfileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        final String firstname = settingsFirstName.getText().toString();
        final String lastname = settingsLastName.getText().toString();
        final String phone = settingsPhone.getText().toString();
        final String email = settingsEmail.getText().toString();

        if (TextUtils.isEmpty(firstname)){
            settingsFirstName.setError("First name is required!");
            return;
        }
        if (TextUtils.isEmpty(lastname)){
            settingsLastName.setError("Last name is required!");
            return;
        }
        if (TextUtils.isEmpty(phone)){
            settingsPhone.setError("Phone is required!");
            return;
        }

        if (TextUtils.isEmpty(email)){
            settingsEmail.setError("Email Required");
        }

        if (resultUri==null){
            Toast.makeText(this, "Profile Image required", Toast.LENGTH_SHORT).show();
        }

        else {
            loader.setMessage("Uploading details");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            Map userInfo = new HashMap();
            userInfo.put("firstname",firstname);
            userInfo.put("lastname",lastname);
            userInfo.put("email", email);
            userInfo.put("phone",phone);


            userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        String error = task.getException().toString();
                        Toast.makeText(ProfileActivity.this, "Update Failed: "+ error, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    loader.dismiss();
                }
            });
        }

        if (resultUri !=null){
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile pictures").child(userID);
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
                                    userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProfileActivity.this, "successful", Toast.LENGTH_SHORT).show();
                                            }else {
                                                String error = task.getException().toString();
                                                Toast.makeText(ProfileActivity.this, "Process failed "+ error, Toast.LENGTH_SHORT).show();
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
        }else {
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            settingsProfileImage.setImageURI(resultUri);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                verifyGoingBack();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void verifyGoingBack() {
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    finish();
                }else {
                    Toast.makeText(ProfileActivity.this, "You Must Update Your Profile First", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
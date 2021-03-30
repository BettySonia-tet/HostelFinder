package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinder.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddHostelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Toolbar toolbar;

    private ImageView hostelImage1, hostelImage2, hostelImage3;
    private EditText hostelName, hostelLocation, hostelType, hostelSharing, hostelPrice, hostelAvailableSpaces, hostelRules;
    private Button  setLocationBtn;

    private Uri imageUri1;
    private Uri imageUri2;
    private Uri imageUri3;

    private DatabaseReference reference;
    StorageTask uploadTask;
    StorageReference storageReference;
    private ProgressDialog loader;
    private String imageSaveLocationUrl ="";
    private String postid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_hostel);

        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        toolbar = findViewById(R.id.tool_bar1);

        setSupportActionBar(toolbar);


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.drawer_layout1);

        hostelImage1 = findViewById(R.id.hostelImage1);
        hostelImage2 = findViewById(R.id.hostelImage2);
        hostelImage3 = findViewById(R.id.hostelImage3);
        hostelName = findViewById(R.id.hostelName);
        hostelLocation = findViewById(R.id.hostelLocation);
        hostelType = findViewById(R.id.hostelType);
        hostelSharing = findViewById(R.id.hostelSharing);
        hostelPrice = findViewById(R.id.hostelPrice);
        hostelAvailableSpaces = findViewById(R.id.hostelAvailableSpaces);
        hostelRules = findViewById(R.id.hostelRules);
        setLocationBtn = findViewById(R.id.setLocationBtn);
        loader = new ProgressDialog(this);



        reference = FirebaseDatabase.getInstance().getReference().child("hostels");
        postid = reference.push().getKey();
        storageReference = FirebaseStorage.getInstance().getReference("hostels images");


        hostelImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        hostelImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        hostelImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });

        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performValidations();
                Intent intent = new Intent(AddHostelActivity.this, MapsActivity.class);
                intent.putExtra("PostID", postid);
                startActivity(intent);
            }
        });


    }

    private void performValidations(){
        String hostel_name = hostelName.getText().toString();
        String hostel_location = hostelLocation.getText().toString();
        String hostel_type = hostelType.getText().toString();
        String hostel_sharing = hostelSharing.getText().toString();
        String hostel_price = hostelPrice.getText().toString();
        String available_spaces = hostelAvailableSpaces.getText().toString();
        String hostel_rules = hostelRules.getText().toString();

        if (!(TextUtils.isEmpty(hostel_name) ||
                TextUtils.isEmpty(hostel_location) ||
                TextUtils.isEmpty(hostel_type) ||
                TextUtils.isEmpty(hostel_sharing) ||
                TextUtils.isEmpty(hostel_price) ||
                TextUtils.isEmpty(available_spaces) ||
                TextUtils.isEmpty(hostel_rules) ||
                imageUri1 ==null ||
                imageUri2 ==null ||
                imageUri3 ==null) ){
            uploadHostelDetails();
        }else {
            Toast.makeText(this, "Please fill the missing fields", Toast.LENGTH_SHORT).show();
        }

    }

    private void startLoader(){
        loader.setMessage("Adding Hostel. Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadHostelDetails() {
        startLoader();

        HashMap<String, Object> hashMap =   new HashMap<>();
        hashMap.put("postid", postid);
        hashMap.put("hostelName", hostelName.getText().toString());
        hashMap.put("location", hostelLocation.getText().toString());
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("type", hostelType.getText().toString());
        hashMap.put("sharing", hostelSharing.getText().toString());
        hashMap.put("price", hostelPrice.getText().toString());
        hashMap.put("availableSpaces", hostelAvailableSpaces.getText().toString());
        hashMap.put("rules", hostelRules.getText().toString());
        hashMap.put("search",hostelLocation.getText().toString()+hostelPrice.getText().toString()+hostelName.getText().toString());

        reference.child(postid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddHostelActivity.this, "Details Uploaded successfully", Toast.LENGTH_SHORT).show();
                    uploadHostelImage1();
                }else {
                    Toast.makeText(AddHostelActivity.this, "Failed to upload details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadHostelImage1() {
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri1));
        uploadTask = fileReference.putFile(imageUri1);
        uploadTask.continueWithTask(new Continuation(){
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    imageSaveLocationUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("hostelImage1", imageSaveLocationUrl);

                    assert postid != null;
                    reference.child(postid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddHostelActivity.this, "image 1 added successfully", Toast.LENGTH_SHORT).show();

                            uploadImage2();
                        }
                    });
                   // finish();

                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(AddHostelActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddHostelActivity.this, "Hostel could not be posted."+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage2() {
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri2));
        uploadTask = fileReference.putFile(imageUri2);
        uploadTask.continueWithTask(new Continuation(){
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    imageSaveLocationUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("hostelImage2", imageSaveLocationUrl);

                    assert postid != null;
                    reference.child(postid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddHostelActivity.this, "image 2 added successfully", Toast.LENGTH_SHORT).show();

                            uploadImage3();
                        }
                    });
                    // finish();

                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(AddHostelActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddHostelActivity.this, "Hostel could not be posted." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage3() {

        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri3));
        uploadTask = fileReference.putFile(imageUri3);
        uploadTask.continueWithTask(new Continuation(){
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    imageSaveLocationUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("hostelImage3", imageSaveLocationUrl);

                    assert postid != null;
                    reference.child(postid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddHostelActivity.this, "image 3 added successfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                    loader.dismiss();
                    finish();

                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(AddHostelActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddHostelActivity.this, "Hostel could not be posted."+e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri1 = data.getData();
            hostelImage1.setImageURI(imageUri1);
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri2 = data.getData();
            hostelImage2.setImageURI(imageUri2);
        }
        if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            imageUri3 = data.getData();
            hostelImage3.setImageURI(imageUri3);
        }

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.addhostel:
                Intent intent = new Intent(AddHostelActivity.this,AddHostelActivity.class);
                startActivity(intent);
                break;
            case R.id.viewhostel:
                Intent intent1= new Intent(AddHostelActivity.this,ViewHostelActivity.class);
                startActivity(intent1);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
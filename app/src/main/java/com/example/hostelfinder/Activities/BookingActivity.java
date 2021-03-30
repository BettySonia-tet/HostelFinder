package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BookingActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextView hostelName,hostelSharing, hostelAvailableSpaces, hostelType, hostelPrice, hostelLocation;
    private Button bookHostelNowBtn, etpay;

    private String postid, name, sharing, availablespaces, type, price, location;
    private String userid;

    private DatabaseReference ref,bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_booking);

        etpay = findViewById(R.id.etPay);


        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user==null){
                    Intent intent = new Intent(BookingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

        hostelName = findViewById(R.id.hostelName);
        hostelSharing = findViewById(R.id.hostelSharing);
        hostelAvailableSpaces = findViewById(R.id.hostelAvailableSpaces);
        hostelType = findViewById(R.id.hostelType);
        hostelPrice = findViewById(R.id.hostelPrice);
        hostelLocation = findViewById(R.id.hostelLocation);
        bookHostelNowBtn = findViewById(R.id.bookHostelNowBtn);

        postid = getIntent().getStringExtra("postid");
        name = getIntent().getStringExtra("name");
        sharing = getIntent().getStringExtra("sharing");
        availablespaces = getIntent().getStringExtra("availablespaces");
        type = getIntent().getStringExtra("type");
        price = getIntent().getStringExtra("price");
        location = getIntent().getStringExtra("location");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking "+name+ " Hostel");

        userid = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("hostels").child(postid);
        bookingsRef = FirebaseDatabase.getInstance().getReference().child("bookings");

        setTextToTextView();

        bookHostelNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BookingActivity.this)
                        .setTitle("Hostel Finder")
                        .setMessage("Are you sure you want to book")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //bookHostel();
                                checkIfAlreadyBookedHostel();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        etpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingActivity.this, MpesaActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setTextToTextView() {
        hostelName.setText("Hostel Name: "+ name);
        hostelSharing.setText("Hostel Sharing: "+sharing);
        hostelAvailableSpaces.setText("Available spaces: "+availablespaces);
        hostelType.setText("Hostel Type: "+type);
        hostelPrice.setText("Hostel Price: "+price);
        hostelLocation.setText("Hostel Location: "+location);
    }

    private void checkIfAlreadyBookedHostel() {
        Query query = bookingsRef.orderByChild(userid).equalTo("booked");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   Toast.makeText(BookingActivity.this, "You have already booked a hostel", Toast.LENGTH_LONG).show();
               }else {
                   bookHostel();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bookHostel() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("bookings").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference.child(userid).setValue("booked").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(BookingActivity.this, "Hostel Booked Successfully", Toast.LENGTH_SHORT).show();

                            int spaces = Integer.valueOf(availablespaces);
                            int spaces_remaining = spaces - 1;
                            String availablespaces = String.valueOf(spaces_remaining);

                            bookHostelNowBtn.setText("Hostel Booked");

                            ref.child("availableSpaces").setValue(availablespaces).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(BookingActivity.this, "Available spaces updated", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(BookingActivity.this, "Could not update spaces", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }else {
                            Toast.makeText(BookingActivity.this, "Error booking hostel "+ task.getException() , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

}
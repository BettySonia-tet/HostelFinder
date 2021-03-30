package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.Adapters.HostelAdapter;
import com.example.hostelfinder.Model.HostelData;
import com.example.hostelfinder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewHostelActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ProgressDialog loader;

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID = "";



    private HostelAdapter hostelAdapter;
    private List<HostelData> hostelDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hostel);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        hostelDataList = new ArrayList<>();
        hostelAdapter = new HostelAdapter(ViewHostelActivity.this, hostelDataList);
        recyclerView.setAdapter(hostelAdapter);


        readHostels();

    }

    private void readHostels() {
        //database ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("hostels");
        Query query = reference.orderByChild("publisher").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hostelDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    HostelData hostelData = snapshot.getValue(HostelData.class);
                    hostelDataList.add(hostelData);
                }

                hostelAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addhostel){
            Intent intent = new Intent(getApplicationContext(), AddHostelActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.viewhostel){
            Intent intent = new Intent(getApplicationContext(), ViewHostelActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
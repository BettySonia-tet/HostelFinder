package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.hostelfinder.Adapters.HostelDetailsAdapter;
import com.example.hostelfinder.Model.HostelData;
import com.example.hostelfinder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {


    private String postid;
    private HostelDetailsAdapter hostelDetailsAdapter;
    private List<HostelData> hostelDataList;
    private RecyclerView recyclerView;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_content);


        postid = getIntent().getStringExtra("postid");
        reference = FirebaseDatabase.getInstance().getReference().child("hostels").child(postid);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        hostelDataList = new ArrayList<>();
        hostelDetailsAdapter = new HostelDetailsAdapter(hostelDataList, ContentActivity.this);
        recyclerView.setAdapter(hostelDetailsAdapter);

        readPost();


    }

    private void readPost() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               hostelDataList.clear();
               HostelData hostelData = snapshot.getValue(HostelData.class);
               hostelDataList.add(hostelData);
               hostelDetailsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
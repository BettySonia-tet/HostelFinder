package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.hostelfinder.Adapters.HistoryAdapter;
import com.example.hostelfinder.Adapters.HostelAdapter;
import com.example.hostelfinder.Model.HostelData;
import com.example.hostelfinder.R;
import com.example.hostelfinder.databinding.ActivityHistoryBinding;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding aBinding;

    private RecyclerView recyclerView;

    private ProgressDialog loader;

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID = "";

    private HistoryAdapter historyAdapter;
    private List<HostelData> hostelDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        aBinding = DataBindingUtil.setContentView(this, R.layout.activity_history);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();


        recyclerView = findViewById(R.id.recycler_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        hostelDataList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(HistoryActivity.this, hostelDataList);
        recyclerView.setAdapter(historyAdapter);

        readHostels();
    }

    private void readHostels() {
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

                historyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
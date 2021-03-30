package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.hostelfinder.Adapters.HostelAdapter;
import com.example.hostelfinder.Model.HostelData;
import com.example.hostelfinder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private HostelAdapter hostelAdapter;
    private List<HostelData> hostelDataList;

    EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);


        recyclerView = findViewById(R.id.recycler_view_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        search_bar = findViewById(R.id.searchBar);

        hostelDataList = new ArrayList<>();
        hostelAdapter = new HostelAdapter(SearchActivity.this, hostelDataList);
        recyclerView.setAdapter(hostelAdapter);
        readHostels();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchUsers (String s){
        Query query = FirebaseDatabase.getInstance().getReference("hostels").orderByChild("hostelName").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hostelDataList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    HostelData post = snapshot.getValue(HostelData.class);
                    hostelDataList.add(post);
                }
                hostelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readHostels(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("hostels");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")){
                    hostelDataList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        HostelData hostelData = snapshot.getValue(HostelData.class);
                        hostelDataList.add(hostelData);
                    }
                    hostelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
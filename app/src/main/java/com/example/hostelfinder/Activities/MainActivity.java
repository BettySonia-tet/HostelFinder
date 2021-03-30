package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.Adapters.HostelAdapter;
import com.example.hostelfinder.Model.HostelData;
import com.example.hostelfinder.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private HostelAdapter hostelAdapter;
    private List<HostelData> hostelDataList;

    private TextView mnavHeaderName,mNavHeaderEmail;
    private CircleImageView nav_header_user_image;
    private DatabaseReference userRef;
    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hostel Finder");




        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_view);

        mnavHeaderName =navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_name);
        mNavHeaderEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_email);
        nav_header_user_image = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_image);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null){
            fetchHeaderItems();

        }





        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        hostelDataList = new ArrayList<>();
        hostelAdapter = new HostelAdapter(MainActivity.this, hostelDataList);
        recyclerView.setAdapter(hostelAdapter);

        readHostels();


    }

    private void fetchHeaderItems() {
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String fname = dataSnapshot.child("firstname").getValue().toString();
                    String sname = dataSnapshot.child("lastname").getValue().toString();
                    mnavHeaderName.setText(fname+" "+ sname);

                    String userEmail = dataSnapshot.child("email").getValue().toString();
                    mNavHeaderEmail.setText(userEmail);

                    String image = dataSnapshot.child("profilepictureurl").getValue(String.class);
                    Glide.with(getApplication()).load(image).into(nav_header_user_image);
                }else {
                    mnavHeaderName.setText("name");
                    mNavHeaderEmail.setText("email");
                    nav_header_user_image.setImageResource(R.drawable.profile_image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readHostels() {
        //database ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("hostels");
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

       /* if (id == R.id.search_view){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }*/
        if (id == R.id.search){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.isDrawerOpen(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.home:
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
                break;


            case R.id.profile:
                Intent intent1 = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent1);
                break;

            case  R.id.history:
                Intent intent3 = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent3);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2  = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
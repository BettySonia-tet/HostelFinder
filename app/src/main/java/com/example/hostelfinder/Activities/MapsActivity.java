package com.example.hostelfinder.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hostelfinder.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private Button setLocationButton;
    private EditText hosteLocation;
    private ImageView menusearch;
    private MapView mapView;
    String hostelLocation, postid, hostellatitude, hostellongitude;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        setLocationButton = findViewById(R.id.setLocationButton);
        hosteLocation = findViewById(R.id.hosteLocation);
        mapView = findViewById(R.id.map);
        menusearch = findViewById(R.id.menusearch);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        postid = getIntent().getStringExtra("PostID");

        menusearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostelLocation = hosteLocation.getText().toString().trim();
                if (hostelLocation.isEmpty())
                {
                    hosteLocation.setError("Enter Location");
                    hosteLocation.requestFocus();
                }else
                {
                    geolocate();
                }


            }


        });

        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference().child("hostels");
                HashMap map = new HashMap();
                map.put("HostelLatitude", hostellatitude);
                map.put("HostelLongitude", hostellongitude);
                reference.child(postid).updateChildren(map);
                Intent intent = new Intent(MapsActivity.this, AddHostelActivity.class);
                startActivity(intent);
        }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng Nairobi = new LatLng(1.2921, 36.8219);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Nairobi));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


    }
    private void geolocate()
    {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(hostelLocation,1);
        }catch (IOException e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (list.size()>0){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),15f, address.getAddressLine(0));
            hostellatitude = String.valueOf(address.getLatitude()) ;
            hostellongitude = String.valueOf(address.getLongitude());


        }else {
            Toast.makeText(this, "Could Not Find That Location", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
    }
}
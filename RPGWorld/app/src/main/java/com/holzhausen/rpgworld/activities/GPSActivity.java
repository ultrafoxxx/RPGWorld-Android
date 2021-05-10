package com.holzhausen.rpgworld.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.holzhausen.rpgworld.R;

public class GPSActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;

    private LocationCallback locationCallback;

    private String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private GoogleMap googleMap;

    private MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_s);

        ActivityCompat.requestPermissions(this, permissions, 1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    marker.position(latLng);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            finishAffinity();
        }
        fusedLocationProviderClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            finishAffinity();
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    marker = new MarkerOptions()
                            .position(latLng);
                    googleMap.addMarker(marker);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                });
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
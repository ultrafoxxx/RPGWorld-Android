package com.holzhausen.rpgworld.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.IGameAPI;
import com.holzhausen.rpgworld.fragments.PlayerInfoFragment;
import com.holzhausen.rpgworld.fragments.QuestFragment;
import com.holzhausen.rpgworld.model.Objective;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.Quest;
import com.holzhausen.rpgworld.viewmodel.GameViewModel;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;

public class GPSActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;

    private LocationCallback locationCallback;

    private String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private GoogleMap googleMap;

    private MarkerOptions playerMarker;

    private List<MarkerOptions> questMarkers;

    private BottomNavigationView bottomNavigationView;

    private SupportMapFragment mapFragment;

    private Player player;

    private IGameAPI api;

    private CompositeDisposable compositeDisposable;

    private GameViewModel gameViewModel;

    private List<Quest> quests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_s);

        ActivityCompat.requestPermissions(this, permissions, 1);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavItemSelected);
        mapFragment = SupportMapFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_view, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    gameViewModel.setCurrentLocation(latLng);
                    playerMarker.position(latLng);
                }
            }
        };
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        gameViewModel.createDistanceSubject();
        player = (Player) getIntent().getExtras().get(getString(R.string.player_key));
        gameViewModel.setPlayerSubject(player);
        gameViewModel.createQuestSubject();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = gameViewModel.getQuestFlowable().subscribe(this::onAcceptedQuest);
        compositeDisposable.add(disposable);
        disposable = gameViewModel.getPlayerFlowable().subscribe(player -> {
            this.player = player;
        });
        compositeDisposable.add(disposable);
        Retrofit retrofit = ApiCaller.getRetrofitClient();
        api = retrofit.create(IGameAPI.class);
        questMarkers = new LinkedList<>();
    }

    private void getQuestMarkers(){
        for(Quest quest : quests) {
            if(quest.isActive()){
                Objective activeObjective = quest.getObjectives()
                        .stream()
                        .filter(objective -> quest.getActiveObjective() == objective.getId())
                        .findFirst()
                        .orElse(new Objective());
                LatLng latLng = new LatLng(activeObjective.getLatitude(), activeObjective.getLongitude());
                questMarkers.add(new MarkerOptions().position(latLng).title(activeObjective.getName()));
            }
        }
    }

    private void onAcceptedQuest(Quest quest) {
        Objective activeObjective = quest
                .getObjectives()
                .stream()
                .filter(objective -> objective.getId() == quest.getActiveObjective())
                .findFirst()
                .orElse(new Objective());
        LatLng objectiveCoordinates = new LatLng(activeObjective.getLatitude(),
                activeObjective.getLongitude());
        MarkerOptions questMarker = new MarkerOptions()
                .position(objectiveCoordinates);
        googleMap.addMarker(questMarker);
    }

    private void downloadQuests() {

        ApiCaller<Quest> apiCaller = new ApiCaller<>();
        Disposable disposable = apiCaller.getListOfObjectsFromApi(api.getQuestForPlayer(
                player.getIdentifier()), this::onGetQuests, this::onError);
        compositeDisposable.add(disposable);

    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(this, getString(R.string.error_info), Toast.LENGTH_SHORT).show();
    }

    private void onGetQuests(List<Quest> quests) {
        this.quests = quests;
        if(googleMap != null){
            getQuestMarkers();
            for(MarkerOptions marker : questMarkers){
                googleMap.addMarker(marker);
            }
        }
        gameViewModel.setQuestListSubject(quests);
    }


    private boolean onNavItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.map){
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_view, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
            return true;
        }
        else if(menuItem.getItemId() == R.id.quests){
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_view, QuestFragment.class, null)
                    .commit();
            return true;
        }
        else if(menuItem.getItemId() == R.id.player_info){
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_view, PlayerInfoFragment.class, null)
                    .commit();
            return true;
        }
        return false;
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
                    gameViewModel.setCurrentLocation(latLng);
                    playerMarker = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation));
                    googleMap.addMarker(playerMarker);
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
        downloadQuests();
        getLastKnownLocation();
        createLocationRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
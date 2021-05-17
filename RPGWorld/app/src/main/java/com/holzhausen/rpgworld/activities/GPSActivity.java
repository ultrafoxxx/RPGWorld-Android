package com.holzhausen.rpgworld.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.SphericalUtil;
import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.IGameAPI;
import com.holzhausen.rpgworld.fragments.PlayerInfoFragment;
import com.holzhausen.rpgworld.fragments.QuestFragment;
import com.holzhausen.rpgworld.model.Objective;
import com.holzhausen.rpgworld.model.ObjectiveCompletionResponse;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.PlayerObjective;
import com.holzhausen.rpgworld.model.Quest;
import com.holzhausen.rpgworld.viewmodel.GameViewModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;

public class GPSActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;

    private LocationCallback locationCallback;

    private String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private GoogleMap googleMap;

    private Marker playerMarker;

    private Map<Marker, Quest> questMarkersMap;

    private BottomNavigationView bottomNavigationView;

    private SupportMapFragment mapFragment;

    private Player player;

    private IGameAPI api;

    private CompositeDisposable compositeDisposable;

    private GameViewModel gameViewModel;

    private List<Quest> quests;

    private LatLng currentLocation;

    private ActivityResultLauncher<Intent> resultLauncher;

    private final static int QUEST_PROXIMITY_THRESHOLD = 1000;

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
                    currentLocation = latLng;
                    gameViewModel.setCurrentLocation(latLng);
                    playerMarker.setPosition(latLng);
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
        questMarkersMap = new HashMap<>();

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::onObjectiveCompletion
                        );
    }

    private void onObjectiveCompletion(ActivityResult result){
        if(result.getResultCode() != RESULT_OK){
            return;
        }
        ApiCaller<ObjectiveCompletionResponse> apiCaller = new ApiCaller<>();
        Intent intent = result.getData();
        PlayerObjective playerObjective = new PlayerObjective();
        playerObjective.setPlayerIdentifier(player.getIdentifier());
        playerObjective.setObjectiveId(intent.getIntExtra(getString(R.string.objective_id), 0));
        apiCaller.getObjectFromApi(api.completeObjective(playerObjective), this::onConfirmObjective, this::onError);
    }

    private void onConfirmObjective(ObjectiveCompletionResponse objectiveCompletionResponse) {
        gameViewModel.sendPlayer(objectiveCompletionResponse.getPlayer());
        gameViewModel.setQuestList(objectiveCompletionResponse.getQuests());
        quests = objectiveCompletionResponse.getQuests();
        questMarkersMap.forEach((marker, quest) -> marker.remove());
        getQuestMarkers();
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

                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(activeObjective
                                .getName()));
                questMarkersMap.put(marker, quest);
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
        Marker marker = googleMap.addMarker(questMarker);
        questMarkersMap.put(marker, quest);
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

    @SuppressLint("PotentialBehaviorOverride")
    private void onGetQuests(List<Quest> quests) {
        this.quests = quests;
        if(googleMap != null){
            getQuestMarkers();
            googleMap.setOnMarkerClickListener(this::onMarkerClick);
        }
        gameViewModel.setQuestListSubject(quests);
    }

    private boolean onMarkerClick(Marker marker) {
        double distance = SphericalUtil.computeDistanceBetween(marker.getPosition(), currentLocation);
        if(distance < QUEST_PROXIMITY_THRESHOLD && !marker.equals(playerMarker)) {
            Quest quest = questMarkersMap.get(marker);
            Intent intent = new Intent(this, ObjectiveActivity.class);
            intent.putExtra(getString(R.string.player_key), player);
            intent.putExtra(getString(R.string.quest_key), quest);
            resultLauncher.launch(intent);
        }
        else if(marker.isInfoWindowShown()){
            marker.hideInfoWindow();
        }
        else {
            marker.showInfoWindow();
        }
        return true;
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
                    currentLocation = latLng;
                    gameViewModel.setCurrentLocation(latLng);
                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation));
                    playerMarker = googleMap.addMarker(options);
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
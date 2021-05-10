package com.holzhausen.rpgworld.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.IMainMenuAPI;
import com.holzhausen.rpgworld.model.Player;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private Button startButton;

    private Button loadButton;

    private Button quitButton;

    private CompositeDisposable compositeDisposable;

    private String playerIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compositeDisposable = new CompositeDisposable();
        assignViews();
        setOnClickListeners();
        disableLoadIfGameDoesNotExist();

    }

    private void disableLoadIfGameDoesNotExist() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains(getString(R.string.player_identifier))){
            playerIdentifier = sharedPreferences.getString(getString(R.string.player_identifier), "");
            loadButton.setOnClickListener(this::loadGame);
        }
        else {
            loadButton.setEnabled(false);
        }
    }

    private void setOnClickListeners() {
        startButton.setOnClickListener(this::startCharacterCreation);
        quitButton.setOnClickListener(view -> finishAffinity());
    }

    private void loadGame(View view) {
        Retrofit retrofit = ApiCaller.getRetrofitClient();
        IMainMenuAPI api = retrofit.create(IMainMenuAPI.class);
        ApiCaller<Player> apiCaller = new ApiCaller<>();
        Disposable disposable = apiCaller.getObjectFromApi(api.getPlayer(playerIdentifier),
                this::onLoadPlayer, this::onLoadError);
        compositeDisposable.add(disposable);
        loadButton.setEnabled(false);
        loadButton.setText(R.string.loading_info);
        startButton.setEnabled(false);
    }

    private void onLoadError(Throwable throwable) {
        throwable.printStackTrace();
        loadButton.setText(R.string.load_game);
        loadButton.setEnabled(true);
        startButton.setEnabled(true);
        Toast.makeText(this, getString(R.string.error_info), Toast.LENGTH_SHORT).show();
    }

    private void onLoadPlayer(Player player) {
        Intent intent = new Intent(this, GPSActivity.class);
        intent.putExtra("player", player);
        startActivity(intent);
    }

    private void startCharacterCreation(View view) {
        Intent intent = new Intent(this, CharacterCreationActivity.class);
        startActivity(intent);
    }

    private void assignViews() {
        startButton = findViewById(R.id.start_button);
        loadButton = findViewById(R.id.load_button);
        quitButton = findViewById(R.id.quit_button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
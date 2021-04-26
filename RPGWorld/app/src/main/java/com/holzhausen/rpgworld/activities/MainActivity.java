package com.holzhausen.rpgworld.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.holzhausen.rpgworld.R;

public class MainActivity extends AppCompatActivity {

    private Button startButton;

    private Button loadButton;

    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assignViews();
        setOnClickListeners();
        disableLoadIfGameDoesNotExist();

    }

    private void disableLoadIfGameDoesNotExist() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setOnClickListeners() {
        startButton.setOnClickListener(this::startCharacterCreation);
        loadButton.setOnClickListener(this::loadGame);
        quitButton.setOnClickListener(view -> finishAffinity());
    }

    private void loadGame(View view) {
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
}
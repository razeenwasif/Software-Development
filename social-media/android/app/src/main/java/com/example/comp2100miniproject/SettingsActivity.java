package com.example.comp2100miniproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        appPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Setup layouts and listener
        ConstraintLayout settingConstraintLayout = findViewById(R.id.settingMenuConstraintLayout);
        ViewCompat.setOnApplyWindowInsetsListener(settingConstraintLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.settingToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize dark mode follow system switch
        MaterialSwitch darkModeFollowSystemSwitch = findViewById(R.id.darkModeFollowSystemSwitch);
        boolean isDarkModeFollowSystem = appPreferences.getBoolean(Constants.DARK_MODE_FOLLOW_SYSTEM_SETTING, false);
        darkModeFollowSystemSwitch.setChecked(isDarkModeFollowSystem);

        // Initialize dark mode enable switch
        MaterialSwitch darkModeSwitch = findViewById(R.id.darkModeSwitch);
        boolean isDarkModeEnabled = appPreferences.getBoolean(Constants.DARK_MODE_ENABLED_SETTING, false);
        darkModeSwitch.setChecked(!isDarkModeFollowSystem && isDarkModeEnabled);

        // Configure listener for dark mode follow system switch
        darkModeFollowSystemSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            saveSettingSwitchState(Constants.DARK_MODE_FOLLOW_SYSTEM_SETTING, isChecked);
            darkModeSwitch.setEnabled(!isChecked);
            darkModeSwitch.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                                                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM :
                                                    AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Configure listener for dark mode enable switch
        darkModeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            saveSettingSwitchState(Constants.DARK_MODE_ENABLED_SETTING, isChecked);
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                                                    AppCompatDelegate.MODE_NIGHT_YES :
                                                    AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Handle deactivate dark mode enable switch when follow system setting
        darkModeSwitch.setEnabled(!isDarkModeFollowSystem);
    }

    private void saveSettingSwitchState(String setting, boolean isChecked) {
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putBoolean(setting, isChecked);
        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle back button press
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
